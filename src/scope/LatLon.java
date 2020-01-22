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
        this.lat = 0.0;
        this.lon = 0.0;
    }

    /**
     * Create a LatLon Object
     *
     * @param val1 latitude
     * @param val2 longitude
     */
    public LatLon(double val1, double val2) {
        this.lat = val1;
        this.lon = val2;
    }
    
    public double getLongitude() {
        return this.lon;
    }

    public double getLatitude() {
        return this.lat;
    }

    public void setLongitude(double longitude) {
        this.lon = longitude;
    }

    public void setLatitude(double latitude) {
        this.lat = latitude;
    }
}
