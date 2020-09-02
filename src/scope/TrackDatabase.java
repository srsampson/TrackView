package scope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

public final class TrackDatabase extends Thread {

    private static final long RATE1 = 15L * 60L * 1000L;    // 15 minutes
    //
    private Thread database;
    private static boolean EOF;
    private boolean metarValid;
    //
    private int pressureAlt;
    //
    private String airport;
    //
    private Config config;
    private ProcessTracks process;
    private Connection db;
    //
    private final Timer timer1;
    private final TimerTask task1;
    
    public TrackDatabase(ProcessTracks p, Config cf) {
        process = p;
        config = cf;
        EOF = false;
        metarValid = false;
        pressureAlt = 0;

        database = new Thread(this);
        database.setName("TrackDatabase");

        airport = cf.getHomeName().trim();     // might be version number instead of airport

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            db = DriverManager.getConnection(config.getDatabaseURL(), config.getDatabaseLogin(), config.getDatabasePassword());
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("TrackDatabase Fatal: Unable to open database " + config.getDatabaseURL());
            System.exit(-1);
        }

        task1 = new MetarRefresh();
        timer1 = new Timer();
    }
    
    public void startDatabase() {
        database.start();
        timer1.scheduleAtFixedRate(task1, 0L, RATE1);
    }

    public void closeDatabase() {
        timer1.cancel();
        EOF = true;

        try {
            db.close();
        } catch (SQLException e) {
            System.out.println("TrackDatabase::close Closing Bug " + e.toString());
            System.exit(-1);
        }
    }

    public Connection getDatabaseConnection() {
        return db;
    }

    class MetarRefresh extends TimerTask {

        @Override
        public void run() {
            Statement query = null;
            ResultSet rs = null;
            String queryString;
            
            // read the latest metar from database
            queryString = String.format("SELECT t.airport, t.utcupdate, t.pressureAlt FROM "
                    + "adsb.metar t INNER JOIN (SELECT airport, MAX(utcupdate) AS "
                    + "MaxDate FROM adsb.metar GROUP BY airport) tm "
                    + "ON t.airport = tm.airport "
                    + "AND t.utcupdate = tm.MaxDate "
                    + "AND t.airport = '%s'",
                    airport);

            try {
                query = db.createStatement();
                rs = query.executeQuery(queryString);
                
                if (rs.next()) {
                    pressureAlt = rs.getInt("pressureAlt");
                    metarValid = true;
                } else {
                    metarValid = false;
                }
                
                query.close();
            } catch (SQLException e2) {
                System.err.println("MetarRefresh::Select error " + e2.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }
                if (query != null) {
                    try {
                        query.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }
    }
    
    @Override
    public void run() {
        Statement query = null;
        ResultSet rs = null;
        Track track;
        int lowAltitude, highAltitude;
        String queryString;
        
        while (EOF == false) {
            lowAltitude = config.getIntegerSetting(Config.DISP_INSTRM_LOW) * 100;
            highAltitude = config.getIntegerSetting(Config.DISP_INSTRM_HIGH) * 100;
            
            lowAltitude -= 1000;    // account for pressure altitude
            
            //
            // We assume that one or more radars are populating
            // the database. Thus, the ACID will appear more than
            // once, and may have different track quality values,
            // so pick the greatest track quality.
            //
            queryString = String.format("SELECT * FROM adsb.target WHERE (acid,quality) "
                    + "IN (SELECT acid,MAX(quality) FROM adsb.target GROUP BY acid) "
                    + "HAVING altitude >= %d && altitude <= %d",
                    lowAltitude,
                    highAltitude);

            try {
                query = db.createStatement();
                rs = query.executeQuery(queryString);
                long utcnow = System.currentTimeMillis();

                while (rs.next()) {
                    String acid = rs.getString("acid");
                    long utcupdate = rs.getLong("utcupdate");

                    if ((utcnow - utcupdate) < (config.getIntegerSetting(Config.DISP_INSTRM_DIM) * 1000L)) {
                        if (!process.hasTrack(acid)) {
                            track = new Track(acid, config);
                            process.addTrack(acid, track);
                        } else {
                            track = process.getTrack(acid);
                        }

                        track.setUpdatedTime(utcupdate);
                        
                        int alt = rs.getInt("altitude");
                        track.setAltitude(alt);
                        
                        if ((alt < 36000) && (metarValid == true)) {
                            track.setAMSLAltitude(alt - pressureAlt);
                        } else {
                            track.setAMSLAltitudeZero();
                        }
                        
                        track.setGroundSpeed(rs.getFloat("groundSpeed"));
                        track.setGroundTrack(rs.getFloat("groundTrack"));
                        track.setComputedGroundSpeed(rs.getFloat("gsComputed"));
                        track.setComputedGroundTrack(rs.getFloat("gtComputed"));
                        track.setCallsign(rs.getString("callsign"));
                        track.setPosition(rs.getFloat("latitude"), rs.getFloat("longitude"), utcupdate);
                        track.setVerticalRateAndTrend(rs.getInt("verticalRate"), rs.getInt("verticalTrend"));

                        int val = rs.getInt("squawk");  // returns 0 on SQL null

                        if (val != 0) {
                            if (val < 100) {
                                track.setSquawk("00" + String.valueOf(val));
                            } else if (val < 1000) {
                                track.setSquawk("0" + String.valueOf(val));
                            } else {
                                track.setSquawk(String.valueOf(val));
                            }
                        } else {
                            track.setSquawk("0000");
                        }

                        track.setAlert(rs.getBoolean("alert"));
                        track.setEmergency(rs.getBoolean("emergency"));
                        track.setSPI(rs.getBoolean("spi"));
                        track.setIsOnGround(rs.getBoolean("onground"));
                        track.addSiteID(rs.getString("radar_id"));

                        process.replaceTrack(acid, track);
                    }
                }

                query.close();
            } catch (SQLException e1) {
                // database probably locked
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }
                if (query != null) {
                    try {
                        query.close();
                    } catch (SQLException e) {
                    }
                }
            }

            for (Track trk : process.getTrackListWithPositions()) {
                if (trk.getRegistration().isEmpty()) {
                    queryString = String.format("SELECT acft_reg FROM modestable WHERE acid='%s'",
                            trk.getAcid());

                    try {
                        query = db.createStatement();
                        rs = query.executeQuery(queryString);

                        if (rs.next()) {
                            String tmp = rs.getString("acft_reg");

                            try {
                                if (tmp != null) {
                                    trk.setRegistration(tmp);
                                }
                            } catch (Exception e8) {
                            }
                        }

                        query.close();
                    } catch (SQLException e1) {
                        // database locked probably
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException e) {
                            }
                        }
                        if (query != null) {
                            try {
                                query.close();
                            } catch (SQLException e) {
                            }
                        }
                    }
                }
            }

            //
            // Altitude window may have changed, so drop all Tracks
            // outside of the window the user has selected.
            //
            for (Track trk : process.getTrackListWithPositions()) {
                int alt = trk.getAltitude();
                String ac = trk.getAcid();
                
                if ((alt < lowAltitude) || (alt > highAltitude)) {
                    process.removeTrack(ac);
                }
            }
            
            /*
             * Take a nap
             */

            try {
                Thread.sleep(1000L);    // @ 1 second nap, no need to hammer database
            } catch (InterruptedException e3) {
            }
        }
    }
}