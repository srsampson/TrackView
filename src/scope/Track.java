package scope;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import math.OrthographicNavigator;

/*
 * This class is used to store all track objects, and provide access methods to
 * use and update them.
 */
public final class Track {

    public final static int MODE_STANDBY = 0;
    public final static int MODE_NORMAL = 1;
    public final static int MODE_IDENT = 2;
    public final static int MODE_GLOBAL = 3;
    public final static int MODE_COAST = 4;
    //
    public static final long ECHO_INTERVAL = 5L * 1000L;        // 5 seconds (12 RPM Antenna)
    //public static final double MS_PER_NM = 1852.0 / 3600.00;  // Metres per NM

    /*
     * Contains a list of the most recent echoes of this track. The list must be
     * sorted, so that the most recent echo is the first element of the vector.
     */
    //private List<Echo> echoHistory;
    private List<String> sites;

    /*
     * Each track can have options, so we store the options by name and object
     * value.
     */
    private ConcurrentHashMap<String, Object> trackOptions;
    //
    public static final String TRACKBLOCK_DIM = "trackBlockDim";
    public static final String TRACKBLOCK_BRIGHT = "trackBlockBright";
    public static final String TRACKBLOCK_POSITION = "trackBlockPosition";
    /*
     * Climb/Descend trend box averager
     */
    private long verticalTrend_time;
    private int verticalRate;
    private int verticalTrend;
    private boolean verticalDIM;
    //
    private String callsign;
    private String acid;
    private String registration;
    private int mode;
    private String squawk;
    private int altitude;
    private int amsl_altitude;
    //
    private boolean mouseOver;
    private boolean alert;
    private boolean emergency;
    private boolean spi;
    private boolean isOnGround;                // Might be on ground but no position
    //
    private double latitude;
    private double longitude;
    private double heading;
    private double bearing;
    private double range;
    private double groundSpeed;
    private double groundTrack;
    private boolean useComputed;
    private double computedGroundSpeed;         // Some planes don't send speed
    private double computedGroundTrack;         // Some planes don't send heading
    //
    private long updatedPosTime;
    private long updatedTime;
    //
    private ConcurrentHashMap<String, Integer> conflicts;  // List of target ID's and altitude that are possible conflicts
    //
    private Config dc;
    private ZuluMillis zulu = new ZuluMillis();

    /**
     * A track is the complete data structure of the Aircraft ID (acid). It
     * takes several different target reports to gather all the data, but this
     * is where it is finally stored.
     *
     * @param a Aircraft ID
     * @param d Config params
     */
    public Track(String a, Config d) {
        long now = zulu.getUTCTime();
        
        dc = d;
        acid = a;
        callsign = "";
        registration = "";
        mode = MODE_NORMAL;
        squawk = "0000";
        latitude = 0.0;
        longitude = 0.0;
        altitude = -9999;
        amsl_altitude = 0;
        verticalRate = 0;
        verticalTrend = 0;
        alert = emergency = spi = mouseOver = isOnGround = false; // Might be on ground but no position
        heading = 0.0;                    // Aircraft Magnetic heading (if known)
        groundSpeed = 0.0;
        groundTrack = 0.0;
        updatedPosTime = 0L;        // might not have a position
        verticalTrend_time = now;   // good default
        updatedTime = now;           // good default
        verticalDIM = false;

        /*
         * Some planes don't ever send a velocity message, so assume the worst
         */
        useComputed = true;
        computedGroundSpeed = 0.0;        // Some planes don't send speed
        computedGroundTrack = 0.0;        // Some planes don't send heading
        sites = new ArrayList<>();
        conflicts = new ConcurrentHashMap<>();
        trackOptions = new ConcurrentHashMap<>();

        try {
            // de-clutter the track by making it dim
            trackOptions.put(TRACKBLOCK_DIM, Boolean.FALSE);
            // highlight the track by making it brighter
            trackOptions.put(TRACKBLOCK_BRIGHT, Boolean.FALSE);
            // One of six positions around the clock
            trackOptions.put(TRACKBLOCK_POSITION, 0);
        } catch (NullPointerException e) {
            System.err.println("Track::Constructor exception during initialization put " + e.toString());
        }
    }

    /**
     * Method to return the Aircraft ID
     *
     * @return a String Representing the 6-character ICAO ID
     */
    public String getAcid() {
        return acid;
    }

    public void setHeading(double head) {
        heading = head;
    }

    public double getHeading() {
        return heading;
    }

    public void setMouseOver(boolean val) {
        mouseOver = val;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    /**
     * Method to return the time this track was last updated
     *
     * @return a long representing the time in milliseconds
     */
    public long getUpdatedTime() {
        return updatedTime;
    }

    /**
     * Method to store the time this track was last updated
     *
     * @param val a long Representing the time in milliseconds
     */
    public void setUpdatedTime(long val) {
        updatedTime = val;
    }

    public ConcurrentHashMap<String, Object> getTrackOptions() {
        return trackOptions;
    }

    public Object getTrackOption(String option) {
        Object obj = (Object) null;

        try {
            obj = (Object) trackOptions.get(option);
            return obj;
        } catch (NullPointerException e) {
            System.err.println("Track::getTrackOption Exception during get " + e.toString());
        }

        return obj;     // obj == null on exception
    }

    public void setTrackBooleanOption(String option, Boolean val) {
        try {
            trackOptions.put(option, val);
        } catch (NullPointerException e) {
            System.err.println("Track::setTrackBooleanOption Exception during put " + e.toString());
        }
    }

    public void setTrackIntegerOption(String option, Integer val) {
        try {
            trackOptions.put(option, val);
        } catch (NullPointerException e) {
            System.err.println("Track::setTrackIntegerOption Exception during put " + e.toString());
        }
    }

    //public void setUseComputed(boolean val) {
    //    useComputed = val;
    //}
    public boolean getUseComputed() {
        return useComputed;
    }

    public long getUpdatedPosTime() {
        return updatedPosTime;
    }

    /**
     * Method to set the callsign
     *
     * <p>
     * Don't change the callsign to blank if it was a good value
     *
     * @param val a string representing the track callsign
     */
    public void setCallsign(String val) {
        if (!callsign.equals(val)) {
            if (!val.equals("")) {
                callsign = val;
            }
        }
    }

    public String getCallsign() {
        return callsign;
    }

    public void setRegistration(String val) {
        if (!registration.equals(val)) {
            if (!val.equals("")) {
                registration = val;
            }
        }
    }

    public String getRegistration() {
        return registration;
    }

    public int getSiteCount() {
        return sites.size();
    }

    public void addSiteID(String val) {
        try {
            if (!sites.contains(val)) {
                sites.add(val);

                if (sites.size() > 1) {
                    mode = MODE_GLOBAL;
                }
            }
        } catch (Exception e) {
        }
    }

    public String[] getSites() {
        return (String[]) sites.toArray();
    }

    public void clearSites() {
        sites.clear();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int val) {
        mode = val;
    }

    /**
     * Method to set the 4-digit octal Mode-3A squawk
     *
     * @param val a String Representing the targets Mode-3A 4-digit octal code
     */
    public void setSquawk(String val) {
        if (!squawk.equals(val)) {
            squawk = val;
        }
    }

    /**
     * Method to return the 4-digit octal Mode-3A squawk
     *
     * @return a String Representing the track Mode-3A 4-digit octal code
     */
    public String getSquawk() {
        return squawk;
    }

    /**
     * Method to set the track altitude in feet
     *
     * @param val an integer representing the track altitude in feet
     */
    public void setAltitude(int val) {
        if (val != -9999) {
            altitude = val;
        }
    }

    /**
     * Method to return the track altitude in feet
     *
     * @return an integer representing the track altitude in feet
     */
    public int getAltitude() {
        return altitude;
    }

    public void setAMSLAltitude(int altCorrected) {
        amsl_altitude = altCorrected;
    }

    public void setAMSLAltitudeZero() {
        amsl_altitude = 0;
    }

    public int getAMSLAltitude() {
        return amsl_altitude;
    }
    
    public int getVerticalRate() {
        return verticalRate;
    }

    public synchronized int getVerticalTrend() {
        if (verticalDIM == true) {
            return 0;   // level
        }
        
        return verticalTrend;
    }

    public synchronized void setVerticalDIM() {
        verticalDIM = true;
    }

    /**
     *
     * @param val1 vertical rate in fps
     * @param val2 trend -1, 0, or 1
     */
    public synchronized void setVerticalRateAndTrend(int val1, int val2) {
        if (verticalRate != val1) {
            verticalRate = val1;
        }
        
        if (verticalTrend != val2) {
            verticalTrend = val2;
            verticalTrend_time = zulu.getUTCTime();
        }
    }

    public synchronized long getVerticalTrendUpdateTime() {
        return verticalTrend_time;
    }

    public boolean getAlert() {
        return alert;
    }

    public void setAlert(boolean val) {
        if (alert != val) {
            if (val == true) {
                mode = MODE_IDENT;
            } else {
                mode = MODE_NORMAL;
            }

            alert = val;
        }
    }

    public boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(boolean val) {
        if (emergency != val) {
            if (val == true) {
                mode = MODE_IDENT;
            } else {
                mode = MODE_NORMAL;
            }

            emergency = val;
        }
    }

    public boolean getSPI() {
        return spi;
    }

    public void setSPI(boolean val) {
        if (spi != val) {
            if (val == true) {
                mode = MODE_IDENT;
            } else {
                mode = MODE_NORMAL;
            }

            spi = val;
        }
    }

    public void setIsOnGround(boolean val) {
        if (isOnGround != val) {
            if (val == true) {
                mode = MODE_STANDBY;
            } else {
                mode = MODE_NORMAL;
            }

            isOnGround = val;
        }
    }

    public boolean getIsOnGround() {
        return isOnGround;
    }

    public void setGroundSpeed(double val) {
        groundSpeed = val;

        if (val != 0.0) {
            useComputed = false;
        }
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundTrack(double val) {
        groundTrack = val;
    }

    public double getGroundTrack() {
        return groundTrack;
    }

    public void setComputedGroundTrack(double val) {
        computedGroundTrack = val;
    }

    public void setComputedGroundSpeed(double val) {
        computedGroundSpeed = val;
    }

    public double getComputedGroundTrack() {
        return computedGroundTrack;
    }

    public double getComputedGroundSpeed() {
        return computedGroundSpeed;
    }

    public void setPosition(double lat, double lon, long time) {
        if ((latitude != lat) && (longitude != lon)) {
            /*
             * If a good position is followed by a 0.0 then keep the old
             * position
             */
            if (lat != 0.0 && lon != 0.0) {
                latitude = lat;
                longitude = lon;
                updatedPosTime = time;
                setTrackBooleanOption(TRACKBLOCK_DIM, Boolean.FALSE);
                verticalDIM = false;
            }
        }
    }

    public LatLon getPosition() {
        return new LatLon(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double val) {
        bearing = val;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double val) {
        range = val;
    }

    public synchronized String[] getConflicts() {
        List<String> result = new ArrayList<>();

        for (Enumeration en = conflicts.keys(); en.hasMoreElements();) {
            result.add((String) en.nextElement());
        }

        return (String[]) result.toArray();
    }

    /**
     * Method to determine if there are any conflicts on the queue
     *
     * @return a boolean Representing whether there are conflicts on the queue
     */
    public boolean hasConflicts() {
        return !conflicts.isEmpty();
    }

    /**
     * Method to see if the specified ACID is on the table
     *
     * @param acid
     * @return a boolean representing whether the ACID is on the table or not
     */
    public boolean hasACIDConflicts(String acid) {
        try {
            if (conflicts.containsKey(acid)) {
                return true;
            }
        } catch (NullPointerException e) {
            System.err.println("Track::hasACIDConflict Exception during containsKey " + e.toString());
            return false;
        }

        return false;
    }

    /**
     * Adds a new conflict for this track on the conflict table
     *
     * @param conflict The new conflict ACID to be stored on the table.
     * @param alt
     */
    public void insertConflict(String conflict, int alt) {
        try {
            conflicts.put(conflict, alt);
        } catch (NullPointerException e) {
            System.err.println("Track::insertConflict Exception during put " + e.toString());
        }
    }

    public void removeConflict(String conflict) {
        try {
            conflicts.remove(conflict);
        } catch (NullPointerException e) {
            System.err.println("Track::removeConflict Exception during remove " + e.toString());
        }
    }

    public void removeAllConflict() {
        conflicts.clear();
    }

    /*
     * Returns the coordinates or 0,0 for future position
     *
     * TODO
     *
     * I'm thinking about using this for showing an intercept point
     * between two tracks.
     */
    public LatLon getFuturePosition(Track target, long seconds, boolean computed) {
        OrthographicNavigator nav = new OrthographicNavigator();
        LatLon Pos = target.getPosition();

        /*
         * Possible that track position hasn't been received yet
         */
        if (Pos.lat == 0.0 && Pos.lon == 0.0) {
            return Pos;
        }

        try {
            if (computed) {
                Pos = nav.getFutureLocation(Pos, target.getComputedGroundTrack(), target.getComputedGroundSpeed(), seconds);
            } else {
                Pos = nav.getFutureLocation(Pos, target.getGroundTrack(), target.getGroundSpeed(), seconds);
            }
        } catch (Exception e) {
            Pos = new LatLon();
        }

        return Pos;
    }
}
