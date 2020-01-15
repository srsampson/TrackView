package scope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class TrackDatabase extends Thread {

    private Thread database;
    private static boolean EOF;
    //
    private Config config;
    private ProcessTracks process;
    private Connection db;
    private ZuluMillis zulu;

    public TrackDatabase(ProcessTracks p, Config cf) {
        this.process = p;
        this.config = cf;
        zulu = new ZuluMillis();
        EOF = false;

        database = new Thread(this);
        database.setName("TrackDatabase");

        try {
            db = DriverManager.getConnection(config.getDatabaseURL());
        } catch (SQLException e) {
            System.err.println("TrackDatabase Fatal: Unable to open database " + config.getDatabaseURL());
            System.exit(-1);
        }

        database.start();
    }

    public void close() {
        EOF = true;

        try {
            db.close();
        } catch (SQLException e) {
            System.out.println("TrackDatabase::close Closing Bug " + e.toString());
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        Statement query = null;
        ResultSet rs = null;
        String queryString, acid, tmp;
        Track track;

        long utcnow, utcupdate;
        int val;

        while (EOF == false) {
            queryString = "SELECT * FROM target WHERE (acid,quality) IN ( SELECT acid, MAX(quality) FROM target GROUP BY acid) order by utcupdate";

            try {
                query = db.createStatement();
                rs = query.executeQuery(queryString);

                while (rs.next()) {
                    acid = rs.getString("acid");
                    utcnow = zulu.getUTCTime();
                    utcupdate = rs.getLong("utcupdate");

                    if ((utcnow - utcupdate) < (config.getIntegerSetting(Config.DISP_INSTRM_DIM) * 1000L)) {
                        if (!process.hasTrack(acid)) {
                            track = new Track(acid, config);
                            process.addTrack(acid, track);
                        } else {
                            track = process.getTrack(acid);
                        }

                        track.setUpdatedTime(utcupdate);
                        track.setAltitude(rs.getInt("altitude"));
                        track.setGroundSpeed(rs.getFloat("groundSpeed"));
                        track.setGroundTrack(rs.getFloat("groundTrack"));
                        track.setComputedGroundSpeed(rs.getFloat("gsComputed"));
                        track.setComputedGroundTrack(rs.getFloat("gtComputed"));
                        track.setCallsign(rs.getString("callsign"));
                        track.setPosition(rs.getFloat("latitude"), rs.getFloat("longitude"), utcupdate);
                        track.setVerticalRate(rs.getInt("verticalRate"));

                        val = rs.getInt("squawk");  // returns 0 on SQL null

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
                            tmp = rs.getString("acft_reg");

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

            /*
             * Take a nap
             */

            try {
                Thread.sleep(500L);    // @ .5 second nap, no need to hammer database
            } catch (InterruptedException e3) {
            }
        }
    }
}