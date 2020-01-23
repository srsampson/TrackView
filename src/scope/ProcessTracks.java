package scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import math.OrthographicNavigator;

public final class ProcessTracks {

    private static final long RATE = 1500L;                 // 1.5 second
    private static final long TIMEOUT = 30L * 1000L;        // 30 seconds
    //
    private final Config config;
    //
    private final ConcurrentHashMap<String, Track> tracks;
    private final ZuluMillis zulu = new ZuluMillis();
    private final OrthographicNavigator nav;
    private final Timer timer1, timer2;
    private final TimerTask task1, task2;

    /**
     * Class constructor
     *
     * @param c a reference to the configuration class
     */
    public ProcessTracks(Config c) {
        config = c;
        tracks = new ConcurrentHashMap<>();
        nav = new OrthographicNavigator();

        task1 = new TimeoutThread1();
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(task1, 0L, RATE);

        task2 = new TimeoutThread2();
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(task2, 5L, RATE);
    }

    public void close() {
        timer1.cancel();
        timer2.cancel();
    }

    public boolean hasTrack(String acid) {
        synchronized (tracks) {
            try {
                return tracks.containsKey(acid);
            } catch (NullPointerException e) {
                System.err.println("ProcessTracks::hasTrack Exception during containsKey " + e.toString());
                return false;
            }
        }
    }

    public int getTrackCount() {
        synchronized (tracks) {
            return tracks.size();
        }
    }

    public Track getTrack(String acid) {
        synchronized (tracks) {
            try {
                return (Track) tracks.get(acid);
            } catch (NullPointerException e) {
                System.err.println("ProcessTracks::getTrack Exception during get " + e.toString());
                return (Track) null;
            }
        }
    }

    /**
     * Method to return a collection of all tracks.
     *
     * @return a collection Representing all tracks.
     */
    public List<Track> getTrackList() {
        List<Track> result = new ArrayList<>();

        synchronized (tracks) {
            result.addAll(tracks.values());
        }

        return result;
    }

    /**
     * Method to return a collection of all tracks that have a non-zero
     * position.
     *
     * @return a vector Representing all updated tracks with non-zero positions.
     */
    public List<Track> getTrackListWithPositions() {
        List<Track> result = new ArrayList<>();

        synchronized (tracks) {
            for (Track trk : tracks.values()) {
                if (trk.getLatitude() != 0.0 && trk.getLongitude() != 0.0) {
                    try {
                        result.add(trk);
                    } catch (Exception e) {
                        System.err.println("ProcessTracks::getAllTracksWithPositions Exception during add " + e.toString());
                    }
                }
            }
        }

        return result;
    }

    /**
     * The aircraft track is received from the database and comes here to queue
     * the data.
     *
     * @param acid a String representing the Aircraft ID
     * @param track an Object representing the Track data
     */
    public void addTrack(String acid, Track track) {
        synchronized (tracks) {
            try {
                tracks.put(acid, track);
            } catch (NullPointerException e) {
                System.err.println("ProcessTracks::addTrack Exception during put " + e.toString());
            }
        }
    }

    /*
     * This is only called locally so far
     */
    private void removeTrack(String acid) {
        synchronized (tracks) {
            if (acid != null) {
                if (tracks.containsKey(acid) == true) {
                    try {
                        tracks.remove(acid);
                    } catch (NullPointerException e) {
                        System.err.println("ProcessTracks::removeTrack Exception during remove " + e.toString());
                    }
                }
            }
        }
    }

    /**
     * As new data arrives the old track object is updated.
     *
     * @param acid a String representing the Aircraft ID
     * @param trk an Object representing the Track data
     */
    public void replaceTrack(String acid, Track trk) {
        synchronized (tracks) {
            if (acid != null) {
                try {
                    tracks.replace(acid, trk);
                } catch (NullPointerException e) {
                    System.err.println("ProcessTracks::replaceTrack Exception during replace " + e.toString());
                }
            }
        }
    }

    /**
     * This is the Track with position values DIM and conflict timer
     */
    class TimeoutThread1 extends TimerTask {

        private long delta;
        private long currentTime;
        private long dimtime;
        private long trackTime;
        private List<Track> tracks;
        private String acid = "";

        @Override
        public void run() {
            currentTime = zulu.getUTCTime();
            dimtime = config.getIntegerSetting(Config.DISP_INSTRM_DIM) * 1000L;

            tracks = getTrackList();

            for (Track track : tracks) {

                /*
                 * Delete all the stations to force it to start over
                 */
                track.clearSites();

                try {
                    acid = track.getAcid();

                    // find the targets that haven't been position updated in x seconds
                    trackTime = track.getUpdatedPosTime();

                    if (trackTime != 0L) {
                        delta = Math.abs(currentTime - trackTime);

                        /*
                         * If track hasn't been updated in xx seconds or more
                         * then signal the track block to go dim if it isn't
                         * already and to delete all conflict alerts pointing to
                         * this track. Also set vertical trend to level.
                         */
                        if (delta >= dimtime) {
                            if (track.getTrackOption(Track.TRACKBLOCK_DIM).equals(Boolean.FALSE)) {
                                track.setTrackBooleanOption(Track.TRACKBLOCK_DIM, Boolean.TRUE);
                                track.setVerticalDIM();
                            }

                            /*
                             * Get rid of all the conflict links in other
                             * targets
                             */
                            String[] table = track.getConflicts();

                            for (String table1 : table) {
                                Track tt = (Track) getTrack(table1);
                                tt.removeConflict(acid);
                            }

                            /*
                             * Now get rid of all conflicts on this target
                             */
                            track.removeAllConflict();
                        }
                    }
                } catch (Exception e2) {
                }
            }
        }
    }

    /**
     * This is the DROP position timer
     */
    class TimeoutThread2 extends TimerTask {

        private long delta;
        private long currentTime;
        private long targetTime;
        private long droptime;
        private List<Track> tracks;

        @Override
        public void run() {
            currentTime = zulu.getUTCTime();
            droptime = config.getIntegerSetting(Config.DISP_INSTRM_DROP) * 1000L;

            tracks = getTrackList();

            if (!tracks.isEmpty()) {
                for (Track track : tracks) {
                    try {
                        targetTime = track.getUpdatedTime();

                        if (targetTime != 0L) {
                            delta = Math.abs(currentTime - targetTime); // abs() in case of wierdness

                            // find the tracks that haven't been updated in xx seconds
                            if (delta >= droptime) {
                                removeTrack(track.getAcid());
                            }
                        }
                    } catch (Exception e2) {
                    }
                }
            }
        }
    }
}
