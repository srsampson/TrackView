package gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import scope.LatLon;

/*
 * The original data looks like this:
 *
 * {Danger Areas}
 * $TYPE=11
 * ; EGD001
 * 50.32167+-5.51167 50.40000+-5.65000
 * 50.53333+-5.56667 50.65833+-5.40000
 * 50.71667+-5.20833 50.64167+-5.07500
 * 50.32167+-5.51167 -1
 *
 * We throw out the comments (';' lines), and labels (Danger Areas for example),
 * convert the Type to a color mapping, and then store each lat/lon in a Vector.
 *
 * The TYPE command is pen-down, the last coordinate (before the -1) is pen-up.
 * A line is drawn between each coordinate.
 */
public final class MapVector {

    private final Color color;
    private final List<LatLon> coordinates;

    public MapVector(Color color) {
        this.color = color;
        this.coordinates = new ArrayList<>();
    }

    public Color getVectorColor() {
        return this.color;
    }

    public boolean addCoordinate(LatLon coordinate) {
        // x is longitude, y is latitude

        if (coordinate.lon < -180.0 || coordinate.lon > 180.0 || coordinate.lat < -90.0 || coordinate.lat > 90.0) {
            System.err.println("MapVector::addCoordinate latitude/Longitude value out of bounds: " + coordinate.lat + "/" + coordinate.lon);
            return false;
        }

        synchronized (coordinates) {
            if (this.coordinates.add(coordinate) == false) {
                System.err.println("MapVector::addCoordinate could not add coordinate point (out of memory?)");
                return false;
            }
        }

        return true;
    }

    /*
     * Method to return the vector coordinates as an array
     */
    public synchronized Object[] getAllCoords() {
        List<LatLon> result = new ArrayList<>();

        result.addAll(coordinates);

        return result.toArray();
    }
}
