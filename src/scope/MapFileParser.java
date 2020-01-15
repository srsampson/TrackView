/*
 * MapFileParser.java
 */
package scope;

/*
 * OUT file syntax:
 *
 * {Danger Areas}
 * $TYPE=11
 * ; EGD001
 * 50.32167+-5.51167 50.40000+-5.65000
 * 50.53333+-5.56667 50.65833+-5.40000
 * 50.71667+-5.20833 50.64167+-5.07500
 * 50.32167+-5.51167 -1
 *
 * Object Fields are:
 *
 * Name, ID, Waypoint Field, Zoom Level, Latitude, Longitude, Altitude AMSL
 *
 * Name: This is a simple text description of the waypoint. ID: This is a code
 * if the point has one - like KTIK, may be empty (but include the comma)
 * Waypoint Field: This is one of the 30 colors available. Zoom Factor: This is
 * 1, 2, 3 or 4.
 *
 * 1 will permanently display the point. 2 will only show the waypoint if zoom
 * level is below 150 nm. 3 will only show the waypoint if zoom level is below 50
 * nm. 4 will only show the waypoint if zoom level is below 15 nm.
 *
 * Latitude: In Degrees and decimal Degrees. Use negative values for the
 * southern hemisphere. Longitude: In Degrees and decimal degrees. Use negative
 * values for points West of Greenwich. Altitude: Height in feet above sea level
 * or 0 if not known.
 *
 * I don't use the Altitude values.
 */
import gui.MapGeoData;
import gui.MapVector;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MapFileParser {

    private static final Pattern COORDS = Pattern.compile("(-*\\d{1,3}\\.\\d{1,10})\\+(-*\\d{1,3}\\.\\d{1,10})|(-1)");
    private static final Pattern TYPE = Pattern.compile("(\\$TYPE\\s*+=)\\s*+(\\d{1,2})");
    private static final Pattern TITLE = Pattern.compile("(\\{)(.+)(\\})");
    private final String OSMapPath;
    private final MapGeoData mapGeoData;
    private Color color;
    private String title;
    private final Color[] colorTable;
    private MapVector mv;

    public MapFileParser(Config c) {
        OSMapPath = c.getMapPath();
        mapGeoData = new MapGeoData();
        colorTable = c.getColorMap();
        color = Color.WHITE;
        title = null;
        mv = null;
    }

    public MapGeoData getMapData() {
        return mapGeoData;
    }

    public void parse() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(OSMapPath));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains(";")) { // strip comments
                line = line.substring(0, line.indexOf(";"));
            }

            if (!line.isEmpty()) {
                parseLine(line);

            }
        }
    }

    private void parseLine(String line) {
        Matcher m;
        String[] coords;

        m = TYPE.matcher(line);

        if (m.matches()) {
            int temp = Integer.parseInt(m.group(2));
            // In case someone puts a $Type=0 in, which is not correct syntax
            if (temp == 0)
                temp++;

            color = colorTable[temp - 1]; // array index 0..29

            if (mv == null) {
                mv = new MapVector(color);
            } else {
                // data file syntax error, close the last segment

                mapGeoData.addMapVector(mv);
                mv = new MapVector(color);
            }

            return;
        }

        m = TITLE.matcher(line);    // not used yet

        if (m.matches()) {
            title = m.group(2);
            return;
        }

        coords = line.split("\\s");

        for (String coord : coords) {
            if (mv == null) {
                mv = new MapVector(color);
            }
            m = COORDS.matcher(coord);
            if (m.matches()) {
                if (coord.equals("-1")) {
                    mapGeoData.addMapVector(mv);
                    mv = null;
                } else {
                    LatLon val = new LatLon(Double.parseDouble(m.group(1)), Double.parseDouble(m.group(2)));
                    mv.addCoordinate(val);
                }
            }
        }
    }
}
