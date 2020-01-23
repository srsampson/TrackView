package scope;

import java.io.Serializable;

public final class LatLon implements Serializable {

    public double lat;
    public double lon;

    /**
     * Create a default LatLon Object
     *
     * Initialized to zero
     */
    public LatLon() {
        lat = 0.0;
        lon = 0.0;
    }

    /**
     * Create a LatLon Object
     *
     * @param val1 latitude
     * @param val2 longitude
     */
    public LatLon(double val1, double val2) {
        lat = val1;
        lon = val2;
    }
    
    public double getLongitude() {
        return lon;
    }

    public double getLatitude() {
        return lat;
    }

    public void setLongitude(double longitude) {
        lon = longitude;
    }

    public void setLatitude(double latitude) {
        lat = latitude;
    }
}
