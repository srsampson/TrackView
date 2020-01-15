package gui;

import java.awt.Color;
import scope.LatLon;

/*
 * Fields are:
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
 * I don't use the Name, and Altitude values.
 */
public final class MapObject {

    private String id;
    private final Color color;
    private final int zoomFactor;            // Not used yet
    private final LatLon coordinate; // lon/lat

    public MapObject() {
        this.id = "";
        this.color = null;
        this.zoomFactor = 1;
        this.coordinate = new LatLon();
    }

    public MapObject(Color c) {
        this.id = "";
        this.color = c;
        this.zoomFactor = 1;
        this.coordinate = new LatLon();
    }
    
    /*
     * All objects are just a colored rectangle for now
     */
    public MapObject(String id, Color c, int z, LatLon coord) {
        if (id.length() > 6) {        // Limit string length
            this.id = id.substring(0, 6);
        }

        this.color = c;
        this.zoomFactor = z;

        // x is longitude, y is latitude

        if (coord.lon < -180.0 || coord.lon > 180.0 || coord.lat < -90.0 || coord.lat > 90.0) {
            System.err.println("MapObject latitude/Longitude value out of bounds: " + coord.lat + "/" + coord.lon);
            this.coordinate = new LatLon();
        } else {
            this.coordinate = coord;
        }
    }

    public Color getMapObjectColor() {
        return this.color;
    }

    public String getMapObjectID() {
        return this.id;
    }

    public int getMapObjectZoomFactor() {
        return this.zoomFactor;
    }

    public LatLon getMapObjectCoordinate() {
        return this.coordinate;
    }
}
