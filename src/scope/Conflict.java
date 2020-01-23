package scope;

import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import math.OrthographicNavigator;

/**
 * Class to manage track conflicts.
 *
 * <p>A conflict is two or more tracks that are less than xx feet separated in
 * height, and within xx nautical miles of each other.
 */
public final class Conflict {

    private static final long RATE = 3L * 1000L;
    //
    private final ProcessTracks process;
    private final OrthographicNavigator nav;
    private final Config config;
    private final ConcurrentHashMap<String, Integer> alts;
    private final Timer timer;
    private final TimerTask task;

    public Conflict(ProcessTracks pr, Config c) {
        process = pr;
        nav = new OrthographicNavigator();
        config = c;

        alts = new ConcurrentHashMap<>(100);

        task = new ConflictTask();
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0L, RATE);
    }

    public void close() {
        timer.cancel();
    }

    class ConflictTask extends TimerTask {

        List<Track> tracks;
        Track candidate1, candidate2;

        @Override
        public void run() {

            /*
             * Determine if there are any other tracks within x nautical miles.
             * Those tracks that are within that range are checked to see if they
             * are less than xxxx feet apart in altitude. If they are, the track
             * is added to the conflict table of that track.
             */

            try {
                tracks = process.getTrackListWithPositions();

                /*
                 * Clear the altitude table
                 */

                alts.clear();

                if (config.getBooleanSetting(Config.DISP_INSTRM_CA)) {
                    if (tracks.size() >= 2) {

                        /*
                         * Create a list of track acid and altitude Then delete
                         * all the old conflicts
                         */

                        for (Track track : tracks) {
                            try {
                                alts.put(track.getAcid(), track.getAltitude());
                            } catch (NullPointerException e) {
                                System.err.println("Conflict::run1 Exception during put " + e.toString());
                            }

                            track.removeAllConflict();
                        }

                        /*
                         * Run through the list, and if any tracks altitude is
                         * within +/- xx feet of each other add that acid to the
                         * tracks conflict table.
                         */

                        for (Enumeration e1 = alts.keys(); e1.hasMoreElements();) {
                            String acid1 = (String) e1.nextElement();
                            int altitude1 = alts.get(acid1);

                            if (altitude1 >= (config.getIntegerSetting(Config.DISP_INSTRM_CAFLOOR) * 100)) {
                                /*
                                 * Compare this acid value to all the other acid
                                 * values
                                 */

                                for (Enumeration e2 = alts.keys(); e2.hasMoreElements();) {
                                    String acid2 = (String) e2.nextElement();
                                    int altitude2 = alts.get(acid2);

                                    if (altitude2 >= (config.getIntegerSetting(Config.DISP_INSTRM_CAFLOOR) * 100)) {

                                        if (!acid2.equals(acid1)) {

                                            candidate1 = (Track) process.getTrack(acid1);
                                            candidate2 = (Track) process.getTrack(acid2);

                                            /*
                                             * Compare if altitude within xx
                                             * feet
                                             */

                                            if (Math.abs(altitude1 - altitude2) < (config.getIntegerSetting(Config.DISP_INSTRM_CAALT) * 100)) {

                                                /*
                                                 * Add the acids to each others
                                                 * list of conflicts only if
                                                 * within x nm of each other.
                                                 */

                                                double range = nav.getDistance(candidate1.getPosition(), candidate2.getPosition());

                                                if (range <= (config.getIntegerSetting(Config.DISP_INSTRM_CARNG))) {
                                                    candidate1.insertConflict(acid2, altitude2);
                                                    candidate2.insertConflict(acid1, altitude1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    /*
                     * CA Turned Off Delete all the old conflicts per each track
                     */

                    for (Track track : tracks) {
                        track.removeAllConflict();
                    }
                }
            } catch (Exception e) {
                System.err.println("Conflict::run2 exception " + e.toString());
            }
        }
    }
}
