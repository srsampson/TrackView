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
    private int[] trend = new int[10];
    private int trend_el = 0;
    //
    private String callsign;
    private String acid;
    private String registration;
    private int mode;
    private String squawk;
    private int altitude;
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
     * @param acid Aircraft ID
     * @param d Config params
     */
    public Track(String acid, Config d) {
        this.dc = d;
        this.acid = acid;
        this.callsign = "";
        this.registration = "";
        this.mode = MODE_NORMAL;
        this.squawk = "0000";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.altitude = -9999;
        this.verticalRate = 0;
        this.verticalTrend = 0;
        this.alert = this.emergency = this.spi = this.mouseOver =
                this.isOnGround = false;        // Might be on ground but no position
        this.heading = 0.0;                    // Aircraft Magnetic heading (if known)
        this.groundSpeed = 0.0;
        this.groundTrack = 0.0;
        this.updatedPosTime = 0L;
        //this.verticalTrend_time = 0L;
        this.updatedTime = 0L;

        /*
         * Some planes don't ever send a velocity message, so assume the worst
         */
        this.useComputed = true;
        this.computedGroundSpeed = 0.0;        // Some planes don't send speed
        this.computedGroundTrack = 0.0;        // Some planes don't send heading
        //this.echoHistory = new ArrayList<>();
        this.sites = new ArrayList<>();
        this.conflicts = new ConcurrentHashMap<>();
        this.trackOptions = new ConcurrentHashMap<>();

        try {
            // de-clutter the track by making it dim
            this.trackOptions.put(TRACKBLOCK_DIM, Boolean.FALSE);
            // highlight the track by making it brighter
            this.trackOptions.put(TRACKBLOCK_BRIGHT, Boolean.FALSE);
            // One of six positions around the clock
            this.trackOptions.put(TRACKBLOCK_POSITION, 0);
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
        return this.acid;
    }

    public void setHeading(double head) {
        this.heading = head;
    }

    public double getHeading() {
        return this.heading;
    }
    
    public void setMouseOver(boolean val) {
        this.mouseOver = val;
    }

    public boolean isMouseOver() {
        return this.mouseOver;
    }

    /**
     * Method to return the time this track was last updated
     *
     * @return a long representing the time in milliseconds
     */
    public long getUpdatedTime() {
        return this.updatedTime;
    }

    /**
     * Method to store the time this track was last updated
     *
     * @param var a long Representing the time in milliseconds
     */
    public void setUpdatedTime(long var) {
        this.updatedTime = var;
    }

    public ConcurrentHashMap<String, Object> getTrackOptions() {
        return this.trackOptions;
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
    //    this.useComputed = val;
    //}
    public boolean getUseComputed() {
        return this.useComputed;
    }

    public long getUpdatedPosTime() {
        return this.updatedPosTime;
    }

    /**
     * Method to set the callsign
     *
     * <p>Don't change the callsign to blank if it was a good value
     *
     * @param val a string representing the track callsign
     */
    public void setCallsign(String val) {
        if (!this.callsign.equals(val)) {
            if (!val.equals("")) {
                this.callsign = val;
            }
        }
    }

    public String getCallsign() {
        return this.callsign;
    }

    public void setRegistration(String val) {
        if (!this.registration.equals(val)) {
            if (!val.equals("")) {
                this.registration = val;
            }
        }
    }

    public String getRegistration() {
        return this.registration;
    }
    
    public int getSiteCount() {
        return this.sites.size();
    }

    public void addSiteID(String val) {
        try {
            if (!this.sites.contains(val)) {
                this.sites.add(val);
                
                if (this.sites.size() > 1) {
                    mode = MODE_GLOBAL;
                }
            }
        } catch (Exception e) {
        }
    }

    public String[] getSites() {
        return (String[]) this.sites.toArray();
    }

    public void clearSites() {
        this.sites.clear();
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int val) {
        this.mode = val;
    }

    /**
     * Method to set the 4-digit octal Mode-3A squawk
     *
     * @param val a String Representing the targets Mode-3A 4-digit octal code
     */
    public void setSquawk(String val) {
        if (!this.squawk.equals(val)) {
            this.squawk = val;
        }
    }

    /**
     * Method to return the 4-digit octal Mode-3A squawk
     *
     * @return a String Representing the track Mode-3A 4-digit octal code
     */
    public String getSquawk() {
        return this.squawk;
    }

    /**
     * Method to set the track altitude in feet
     *
     * @param val an integer representing the track altitude in feet
     */
    public void setAltitude(int val) {
        if (val != -9999) {
            this.altitude = val;
        }
    }

    /**
     * Method to return the track altitude in feet
     *
     * @return an integer representing the track altitude in feet
     */
    public int getAltitude() {
        return this.altitude;
    }

    public int getVerticalRate() {
        return this.verticalRate;
    }

    public synchronized int getVerticalTrend() {
        return this.verticalTrend;
    }

    public synchronized void setVerticalTrendZero() {
        verticalTrend = 0;
        verticalTrend_time = zulu.getUTCTime();
    }

    public synchronized void setVerticalRateAndTrend(int val1, int val2) {
        this.verticalRate = val1;

        verticalTrend = val2;
        verticalTrend_time = zulu.getUTCTime();
    }

    public synchronized long getVerticalTrendUpdateTime() {
        return this.verticalTrend_time;
    }

    public boolean getAlert() {
        return this.alert;
    }

    public void setAlert(boolean val) {
        if (this.alert != val) {
            if (val == true) {
                this.mode = MODE_IDENT;
            } else {
                this.mode = MODE_NORMAL;
            }

            this.alert = val;
        }
    }

    public boolean getEmergency() {
        return this.emergency;
    }

    public void setEmergency(boolean val) {
        if (this.emergency != val) {
            if (val == true) {
                this.mode = MODE_IDENT;
            } else {
                this.mode = MODE_NORMAL;
            }

            this.emergency = val;
        }
    }

    public boolean getSPI() {
        return this.spi;
    }

    public void setSPI(boolean val) {
        if (this.spi != val) {
            if (val == true) {
                this.mode = MODE_IDENT;
            } else {
                this.mode = MODE_NORMAL;
            }

            this.spi = val;
        }
    }

    public void setIsOnGround(boolean val) {
        if (this.isOnGround != val) {
            if (val == true) {
                this.mode = MODE_STANDBY;
            } else {
                this.mode = MODE_NORMAL;
            }

            this.isOnGround = val;
        }
    }

    public boolean getIsOnGround() {
        return this.isOnGround;
    }

    public void setGroundSpeed(double val) {
        this.groundSpeed = val;

        if (val != 0.0) {
            this.useComputed = false;
        }
    }

    public double getGroundSpeed() {
        return this.groundSpeed;
    }

    public void setGroundTrack(double val) {
        this.groundTrack = val;
    }

    public double getGroundTrack() {
        return this.groundTrack;
    }

    public void setComputedGroundTrack(double val) {
        this.computedGroundTrack = val;
    }

    public void setComputedGroundSpeed(double val) {
        this.computedGroundSpeed = val;
    }

    public double getComputedGroundTrack() {
        return this.computedGroundTrack;
    }

    public double getComputedGroundSpeed() {
        return this.computedGroundSpeed;
    }

    public void setPosition(double lat, double lon, long time) {
        if ((this.latitude != lat) && (this.longitude != lon)) {
            /*
             * If a good position is followed by a 0.0 then keep the old
             * position
             */
            if (lat != 0.0 && lon != 0.0) {
                this.latitude = lat;
                this.longitude = lon;
                this.updatedPosTime = time;
                this.setTrackBooleanOption(TRACKBLOCK_DIM, Boolean.FALSE);
            }
        }
    }

    public LatLon getPosition() {
        return new LatLon(this.latitude, this.longitude);
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getBearing() {
        return this.bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
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
