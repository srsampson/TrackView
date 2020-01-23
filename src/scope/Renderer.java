package scope;

import gui.DiamondSprite;
import gui.MapGeoData;
import gui.MapObject;
import gui.MapVector;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import math.Navigator;
import math.OrthographicNavigator;
import math.Projection;

public final class Renderer extends ScopeRenderer {

    private final ProcessTracks process;
    //
    private final ConcurrentHashMap<String, DiamondSprite> sprites;
    private ConcurrentHashMap<String, Object> option;
    private List<Track> tracks;
    //
    private final NumberFormat nf3;
    private final NumberFormat nf4;
    //
    private int windowX, windowY;
    private long echoLimit;
    //
    private MapGeoData mapGeoData;
    private MapVector mapVector;
    private MapObject mapObject;
    private Graphics2D graph;
    //
    private final Connection db;
    private final ZuluMillis zulu;

    public Renderer(ProcessTracks pr, Connection con, Projection p, Navigator n, double s, Config c) {
        super(p, n, s, c);

        zulu = new ZuluMillis();
        process = pr;
        db = con;
        sprites = new ConcurrentHashMap<>();

        nf3 = NumberFormat.getIntegerInstance();
        nf3.setMinimumIntegerDigits(3);
        nf3.setMaximumIntegerDigits(3);
        nf3.setGroupingUsed(false);

        nf4 = NumberFormat.getIntegerInstance();
        nf4.setMinimumIntegerDigits(4);
        nf4.setMaximumIntegerDigits(4);
        nf4.setGroupingUsed(false);
    }

    /**
     * Method to return sprites representing target current position
     *
     * @return a collection Representing all the sprites for target present
     * position
     */
    public Collection<DiamondSprite> getSprites() {
        return sprites.values();
    }

    @Override
    public void renderer(Graphics graphics, int width, int height, boolean displayStep) {
        Statement query = null;
        ResultSet rs = null;
        String queryString;
        LatLon p, m;
        int x, y, alt, top, bottom, vert;
        boolean dim = false;
        boolean paint_echo;

        if (graphics == null) {
            return;
        }

        windowX = width;
        windowY = height;

        graph = (Graphics2D) graphics.create();
        graph.setFont(dc.getFontSetting(Config.DISP_INSTRM_FONT));
        
        echoLimit = ((dc.getIntegerSetting(Config.DISP_INSTRM_ECHOES) * 60L) + 10L) * 1000L;

        // mapGeoData is null until map data is finally read from file
        if (mapGeoData != null) {
            //paintMapObjects(graph);
            paintMapVectors(graph);
        }

        /*
         * First, get a copy of all the targets into a Vector
         */
        tracks = process.getTrackListWithPositions();

        /*
         * Second, plot the targets and echoes onto the projection
         */
        if (!tracks.isEmpty()) {
            long currentTime = zulu.getUTCTime();

            for (Track track : tracks) {
                alt = track.getAltitude();

                option = track.getTrackOptions();

                try {
                    dim = option.get(Track.TRACKBLOCK_DIM).equals(Boolean.TRUE);
                } catch (NullPointerException e) {
                    System.err.println("Renderer::render Exception during option get " + e.toString());
                }

                top = dc.getIntegerSetting(Config.DISP_INSTRM_HIGH) * 100;
                bottom = dc.getIntegerSetting(Config.DISP_INSTRM_LOW) * 100;

                if (bottom == 0) {
                    bottom = -1000;     // account for pressure altitude
                }

                if ((alt >= bottom) && (alt <= top)) {
                    p = track.getPosition();        // lat/lon
                    m = projection.convertToMeters(p);
                    x = (int) (m.lon * scale);
                    y = (int) (m.lat * (-scale));

                    if (track.getIsOnGround() == true) {      // On Ground
                        // This just paints the Hex ID and GroundSpeed
                        paintGndBlock(x, y, track, graph);
                        paint_echo = dc.getBooleanSetting(Config.DISP_INSTRM_GND_ECHO);
                    } else {                // not on ground
                        if (p.lon != 0.0 && p.lat != 0.0) {
                            if (!Double.isNaN(p.lon) || !Double.isNaN(p.lat)) {
                                paintSpeedVector(p, track, graph, dim);
                            }
                        }

                        paintAircraft(x, y, track, graph, displayStep, dim);
                        paint_echo = true;  // always paint echo for airborne
                    }
                } else {
                    paint_echo = false; // except when outside of plotting altitudes
                }

                /*
                 * Get this tracks echoes from the database
                 */
                if (paint_echo) {
                    try {
                        queryString = String.format("SELECT latitude,longitude,verticalTrend"
                                + " FROM targetecho WHERE acid='%s' && (utcdetect > %d) ORDER BY "
                                + "utcdetect DESC",
                                track.getAcid(),
                                (currentTime - echoLimit));

                        try {
                            query = db.createStatement();
                            rs = query.executeQuery(queryString);
                        } catch (SQLException esql) {
                            query.close();
                            continue;
                        }

                        /*
                         * and paint them
                         */
                        while (rs.next()) {
                            p = new LatLon(rs.getFloat("latitude"), rs.getFloat("longitude"));
                            vert = rs.getInt("verticalTrend");

                            if (p.lon != 0.0 && p.lat != 0.0) {
                                try {
                                    m = projection.convertToMeters(p);
                                    x = (int) (m.lon * scale);
                                    y = (int) (m.lat * (-scale));

                                    if (isInRange(x, y)) {
                                        if (vert < 0) {
                                            graph.setColor(dc.getColorSetting(Config.COLORS_TRACK_DESCEND_HIST));
                                        } else if (vert > 0) {
                                            graph.setColor(dc.getColorSetting(Config.COLORS_TRACK_CLIMB_HIST));
                                        } else {
                                            graph.setColor(dc.getColorSetting(Config.COLORS_TRACK_HIST));
                                        }

                                        graph.fillOval(x, y, dc.getIntegerSetting(Config.DISP_INSTRM_ESIZE), dc.getIntegerSetting(Config.DISP_INSTRM_ESIZE) + 1);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (SQLException e9) {
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
        }
    }

    private boolean isInRange(int x1, int y1) {
        return x1 > (-windowX / 2) && x1 < (windowX / 2) && y1 > (-windowY / 2) && y1 < (windowY / 2);
    }

    /*
     * Plot the current position on the scope
     */
    private void paintAircraft(int x, int y, Track track, Graphics2D graphics, boolean dStep, boolean dim) {
        DiamondSprite d;
        String acid = track.getAcid();
        Color clr1;
        Color clr;

        if (isInRange(x, y)) {
            graphics.setFont(dc.getFontSetting(Config.DISP_INSTRM_TRACK_FONT));
            clr1 = dc.getColorSetting(Config.COLORS_TRACK);

            if (dim) {
                clr = clr1.darker();
                clr1 = clr.darker();
            }

            graphics.setColor(clr1);

            switch (track.getMode()) {
                case Track.MODE_COAST:
                    d = new DiamondSprite(x, y, Color.GRAY, acid);
                    d.setFill(false);
                    break;
                case Track.MODE_GLOBAL:
                    // A filled diamond
                    d = new DiamondSprite(x, y, clr1, acid);
                    d.setFill(true);
                    break;
                case Track.MODE_IDENT:
                    // A diamond
                    d = new DiamondSprite(x, y, clr1, acid);
                    d.setFill(false);
                    break;
                default:
                case Track.MODE_NORMAL:
                    // A diamond
                    d = new DiamondSprite(x, y, clr1, acid);
                    d.setFill(false);
            }

            d.paint(graphics);
            sprites.put(acid, d);

            graphics.setColor(Color.ORANGE);

            if (track.getEmergency() == true) {
                // EMERG
                if (dStep == true) {
                    graphics.drawString("E", x - 2, y - 20);
                    graphics.drawLine(x - 4, y - 10, x, y - 6);
                    graphics.drawLine(x + 4, y - 10, x, y - 6);
                    graphics.drawLine(x, y - 15, x, y - 6);
                }
            } else if (track.getSPI() == true) {
                // IDENT
                if (dStep == true) {
                    graphics.drawString("P", x - 2, y - 20);
                    graphics.drawLine(x - 4, y - 10, x, y - 6);
                    graphics.drawLine(x + 4, y - 10, x, y - 6);
                    graphics.drawLine(x, y - 15, x, y - 6);
                }
            } else if (track.getAlert() == true) {
                // CODE Change (Squawk)
                if (dStep == true) {
                    graphics.drawString("C", x - 2, y - 20);
                    graphics.drawLine(x - 4, y - 10, x, y - 6);
                    graphics.drawLine(x + 4, y - 10, x, y - 6);
                    graphics.drawLine(x, y - 15, x, y - 6);
                }
            }
        }

        paintDataBlock(x, y, track, graphics, dStep, dim);
    }

    /*
     * Ground targets don't get a full data block
     */
    private void paintGndBlock(int x, int y, Track track, Graphics graphics) {
        String line1, line2;

        if (isInRange(x, y)) {
            graphics.setColor(dc.getColorSetting(Config.COLORS_TRACK));
            graphics.setFont(dc.getFontSetting(Config.DISP_INSTRM_TRACK_FONT));

            line1 = track.getAcid();
            line2 = nf3.format(track.getGroundSpeed());

            /*
             * Squash text together a bit by 20%
             */
            int fh = (int) ((float) graphics.getFontMetrics().getHeight() * .80F);

            graphics.drawString(line1, x + 6, y);
            graphics.drawString(line2, x + 6, y + fh);
            graphics.drawString("+", x - 3, y);
        }
    }

    private void paintDataBlock(int x, int y, Track track, Graphics graphics, boolean displayStep, boolean dim) {
        boolean mouseover = track.isMouseOver();
        String tmp;

        if (dc.getBooleanSetting(Config.DISP_INSTRM_TRACK) == false) {
            if ((track.getMode() != Track.MODE_IDENT) && (!track.hasConflicts())) {

                // On Ident, Conflict, or Mouse-Over, show track block
                if (!mouseover) {
                    return;
                }
            }
        }

        if (isInRange(x, y)) {
            int actualAlt = track.getAltitude();
            double groundSpeed = track.getGroundSpeed();
            String callsign = track.getCallsign();
            String squawk = track.getSquawk();
            int vr = track.getVerticalRate();
            int vt = track.getVerticalTrend();
            Color clr, clr1;

            if (track.getUseComputed() == true) {
                groundSpeed = track.getComputedGroundSpeed();
            }

            if (vt > 0) {
                clr1 = dc.getColorSetting(Config.COLORS_TRACK_CLIMB);
            } else if (vt < 0) {
                clr1 = dc.getColorSetting(Config.COLORS_TRACK_DESCEND);
            } else {
                clr1 = dc.getColorSetting(Config.COLORS_TRACK);
            }

            if (dim) {
                clr = clr1.darker();
                clr1 = clr.darker();
            }

            graphics.setColor(clr1);
            graphics.setFont(dc.getFontSetting(Config.DISP_INSTRM_TRACK_FONT));

            int dataPos = (Integer) track.getTrackOption(Track.TRACKBLOCK_POSITION);

            if (dataPos == 0) {
                dataPos = dc.getIntegerSetting(Config.DISP_INSTRM_BLOCK);
            }

            int dataDist = dc.getIntegerSetting(Config.DISP_INSTRM_LEADER);

            String firstLine = " ";
            String secondLine = " ";
            String thirdLine = " ";
            String fourthLine = " ";

            tmp = track.getRegistration();

            if (tmp.equals("")) {
                tmp = track.getAcid();
            }

            firstLine += tmp;

            if (!callsign.equals("")) {
                firstLine += " (" + callsign + ")";
            }

            boolean red = false;
            int ta = dc.getIntegerSetting(Config.DISP_INSTRM_TRANS) * 100;

            if (actualAlt == -9999) {
                secondLine += "----";
            } else if (actualAlt >= ta) {
                secondLine += "F" + nf3.format(actualAlt / 100);
            } else {
                secondLine += "A" + nf3.format(actualAlt / 100);
            }

            secondLine += " ";

            if (track.getUseComputed() == true) {
                if (groundSpeed == 0.0) {
                    secondLine += " ";
                } else {
                    secondLine += nf3.format(groundSpeed) + "*";
                }
            } else {
                secondLine += nf3.format(groundSpeed);
            }

            if (track.hasConflicts()) {
                if (!dim) {
                    // this should blink on the datablock
                    if (displayStep == false) {
                        red = false;
                        fourthLine = " ";
                    } else {
                        red = true;
                        fourthLine = " CONFLICT";
                    }
                }
            }

            if (!squawk.equals("0000")) {
                thirdLine += squawk + " ";

                if (!dim) {

                    /*
                     * Special Squawks that cause Blinking
                     */
                    switch (squawk) {
                        case "7500":
                            // this should blink on the datablock
                            if (displayStep == false) {
                                red = false;
                                fourthLine = " ";
                            } else {
                                red = true;
                                // squawk code for "hijack"
                                fourthLine = " HIJAC ";
                            }
                            break;
                        case "7600":
                            // this should blink on the datablock
                            if (displayStep == false) {
                                red = false;
                                fourthLine = " ";
                            } else {
                                red = true;
                                // squawk code for "radio failure"
                                fourthLine = " RADIO ";
                            }
                            break;
                        case "7700":
                            // this should blink on the datablock
                            if (displayStep == false) {
                                red = false;
                                fourthLine = " ";
                            } else {
                                red = true;
                                // squawk code for "emergency"
                                fourthLine = " EMERG ";
                            }
                    }
                }
            }

            if (vr != 0) {
                if (vr > 0) {
                    thirdLine += "+";
                }

                thirdLine += Integer.toString(vr);
            }

            int dataX = 0;
            int dataY = 6;

            switch (dataPos) {
                /*
                 * 0 means let the computer decide
                 *
                 * The target groundTrack determines where the track block
                 * should go.
                 *
                 * 316 - 45 = 3 46 - 135 = 5 136 - 225 = 7 226 - 315 = 1
                 */
                case 0:
                    int tk;

                    if (track.getUseComputed() == true) {
                        tk = (int) track.getComputedGroundTrack();
                    } else {
                        tk = (int) track.getGroundTrack();
                    }

                    if (tk > 315 && tk < 46) {
                        dataX = 6;
                        dataY = 0;
                        dataPos = 3;    // E
                    } else if (tk > 45 && tk < 136) {
                        dataX = 0;
                        dataY = -6;
                        dataPos = 5;    // N
                    } else if (tk > 135 && tk < 226) {
                        dataX = -6;
                        dataY = 0;
                        dataPos = 7;    // W
                    } else if (tk > 225 && tk < 316) {
                        dataX = 0;
                        dataY = 6;
                        dataPos = 1;    // S
                    }
                    break;
                default:
                case 1:             // S
                    dataX = 0;
                    dataY = 6;
                    break;
                case 2:             // SE
                    dataX = 6;
                    dataY = 6;
                    break;
                case 3:             // E
                    dataX = 6;
                    dataY = 0;
                    break;
                case 4:             // NE
                    dataX = 6;
                    dataY = -6;
                    break;
                case 5:             // N
                    dataX = 0;
                    dataY = -6;
                    break;
                case 6:             // NW
                    dataX = -6;
                    dataY = -6;
                    break;
                case 7:             // W
                    dataX = -6;
                    dataY = 0;
                    break;
                case 8:             // SW
                    dataX = -6;
                    dataY = 6;
            }

            // Draw leader line
            graphics.drawLine(x + dataX, y + dataY, x + dataX * (dataDist), y + dataY * (dataDist));

            /*
             * Move the text to the end of the leader line
             */
            dataX *= dataDist + 1;
            dataY *= dataDist + 1;

            /*
             * Squash text together a bit by 20%
             */
            int fh = (int) ((float) graphics.getFontMetrics().getHeight() * .80);
            int fw1 = (x + dataX) - graphics.getFontMetrics().stringWidth(firstLine);

            /*
             * If track block is on the left, then move block left
             */
            if (dataPos > 5) {
                graphics.drawString(firstLine, fw1, y + dataY);
                graphics.drawString(secondLine, fw1, y + fh + dataY);
                graphics.drawString(thirdLine, fw1, y + (fh * 2) + dataY);
            } else {
                graphics.drawString(firstLine, x + dataX, y + dataY);
                graphics.drawString(secondLine, x + dataX, y + fh + dataY);
                graphics.drawString(thirdLine, x + dataX, y + (fh * 2) + dataY);
            }

            if (red) {
                if (!dim) {
                    graphics.setColor(Color.ORANGE);

                    if (dataPos > 5) {
                        graphics.drawString(fourthLine, fw1, y + (fh * 3) + dataY);
                    } else {
                        graphics.drawString(fourthLine, x + dataX, y + (fh * 3) + dataY);
                    }
                }
            }
        }
    }

    private void paintSpeedVector(LatLon start, Track track, Graphics graphics, boolean dim) {
        OrthographicNavigator nav = new OrthographicNavigator();
        LatLon meters;
        double speed, heading;
        int vecMin = dc.getIntegerSetting(Config.DISP_INSTRM_VECTOR);
        Color clr;
        Color clr1;

        if (vecMin != 0) {
            if (track.getUseComputed() == true) {
                speed = track.getComputedGroundSpeed();
                heading = track.getComputedGroundTrack();

                if (speed > 600.0) {		// tame it
                    speed = 300.0;
                }
            } else {
                speed = track.getGroundSpeed();
                heading = track.getGroundTrack();
            }

            if (speed < 50.0) {
                return;			// punt for low speeds
            }

            // start point of the vector line
            meters = projection.convertToMeters(start);

            if (meters.lon == 0.0 && meters.lat == 0.0) {
                return;
            }

            if (Double.isNaN(meters.lon) || Double.isNaN(meters.lat)) {
                return;
            }

            int x1 = (int) (meters.lon * scale);
            int y1 = (int) (meters.lat * -scale);

            // end point of the vector line
            LatLon end = nav.getFutureLocation(start, heading, speed, (vecMin * 60L));
            meters = projection.convertToMeters(end);

            if (meters.lat == 0.0 && meters.lon == 0.0) {
                return;
            }

            if (Double.isNaN(meters.lat) || Double.isNaN(meters.lon)) {
                return;
            }

            int x2 = (int) (meters.lon * scale);
            int y2 = (int) (meters.lat * -scale);

            clr1 = dc.getColorSetting(Config.COLORS_TRACK);

            if (dim) {
                clr = clr1.darker();
                clr1 = clr.darker();
            }

            graphics.setColor(clr1);
            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    public void setMapData(MapGeoData m) {
        mapGeoData = m;
    }

    private void paintMapObjects(Graphics2D graph) {
        if (dc.getBooleanSetting(Config.MAP_OBJECTS) == true) {
            if (mapGeoData.getMapObjectCount() > 0) {
                List<MapObject> items = mapGeoData.getAllMapObjects();

                for (MapObject o : items) {
                    graph.setColor(o.getMapObjectColor());

                    LatLon p = projection.convertToMeters(o.getMapObjectCoordinate());

                    int x = (int) (p.lon * scale);
                    int y = (int) (p.lat * -scale);

                    if (isInRange(x, y)) {
                        graph.drawRect(x - 2, y - 2, 5, 5);

                        if (dc.getBooleanSetting(Config.MAP_NAMES) == true) {
                            graph.drawString(o.getMapObjectID(), x + 6, y);
                        }
                    }
                }
            }
        }
    }

    /*
     * Converted from old style Vector to new style Dequue
     * (I guess it was worth it...)
     */
    private void paintMapVectors(Graphics2D graph) {
        Object[] lineVector;

        if (dc.getBooleanSetting(Config.MAP_VECTORS) == true) {
            if (mapGeoData.getMapVectorCount() > 0) {
                List<MapVector> linevectors = mapGeoData.getAllMapVectors();

                for (Iterator j = linevectors.iterator(); j.hasNext();) {
                    mapVector = (MapVector) j.next();
                    lineVector = mapVector.getAllCoords();
                    graph.setColor(mapVector.getVectorColor());

                    for (int i = 0; i < (lineVector.length - 1); i++) {
                        LatLon start = projection.convertToMeters((LatLon) lineVector[i]);
                        int x1 = (int) (start.lon * scale);
                        int y1 = (int) (start.lat * -scale);

                        LatLon end = projection.convertToMeters((LatLon) lineVector[i + 1]);
                        int x2 = (int) (end.lon * scale);
                        int y2 = (int) (end.lat * -scale);

                        if (isInRange(x1, y1) && isInRange(x2, y2)) {
                            graph.drawLine(x1, y1, x2, y2);
                        }
                    }
                }
            }
        }
    }
}
