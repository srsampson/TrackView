package math;

import scope.LatLon;

/*
 * Classes which extend this interface can be used to project positions of
 * objects on earth`s surface given in degrees of longitude/latitude onto a
 * (2-dimensional) plane. It is assumed that this plane is a tangential plane of
 * earth`s surface, touching it only at one point. Hence this point is the only
 * information needed to fully describe the projection plane.
 *
 * Copyright (C) 2004 Stefan Bissell
 */
public abstract class Projection {

    /**
     * The approximate radius of earth in meters. In reality this is not really
     * constant, as earth is not really a perfect sphere. (WGS84).
     */
    public static final double EARTH_RADIUS = 6378137.0;
    /**
     * The approximate length of one degree of latitude. Radius * 2PI =
     * 40074982.83566 divided by 360 is 111319.3968
     */
    public static final double METERS_PER_LAT = 111319.3968;
    /**
     * The center of the projection given in degrees of longitude/latitude.
     * Positive values are east/north. This is the point where earth and the
     * plane of the projection touch.
     */
    protected LatLon center;

    /**
     * Constructs a new projection which is centered at the given location.
     *
     * @param c The center of the projection given in degrees of
     * latitude/Longitude. Positive values are east/north.
     */
    public Projection(LatLon c) {
        center = c;
    }

    /**
     * Returns the center of the projection.
     *
     * @return The longitude/latitude coordinates of the center in degrees.
     * Positive values are east/north.
     */
    public LatLon getCenter() {
        return new LatLon(center.lat, center.lon);
    }

    /**
     * Re-centers the projection at the given location.
     *
     * @param c The center of the projection given in degrees of
     * longitude/latitude. Positive values are east/north.
     */
    public void setCenter(LatLon c) {
        this.center = c;
    }

    /**
     * Convenience method which calls {@link #convertToMeters(float,float)}
     * internally but takes a point-object as a parameter.
     *
     * @param coords The longitude/latitude of the coordinates in degrees.
     * Positive values are east/north.
     *
     * @return The x-y-coordinates on the projection plane, measured in meters
     * from the center.
     */
    public LatLon convertToMeters(LatLon coords) {
        return convertToMeters(coords.lat, coords.lon);
    }

    /**
     * Converts the given lon/lat coordinates to meters on the projection plane
     * measured from the center.
     *
     * @param lon The longitude of the coordinates in degrees. Positive values
     * are east.
     *
     * @param lat The latitude of the coordinates in degrees. Positive values
     * are north.
     *
     * @return The corresponding coordinates on the projection plane, measured
     * in meters from the center.
     */
    public abstract LatLon convertToMeters(double lat, double lon);

    /**
     * Converts the given x/y-coordinates on the projection plane to coordinates
     * on earth`s surface.
     *
     * @param lat The y-coordinate on the projection plane, measured in meters
     * from the center.
     *
     * @param lon The x-coordinate on the projection plane, measured in meters
     * from the center.
     *
     * @return The longitude/latitude coordinates on earth`s surface in degrees.
     * Positive values are east/north.
     */
    public abstract LatLon convertToCoords(double lat, double lon);

    /**
     * Constructs a new object which is a projection with the same center as the
     * instance on which this method is invoked.
     *
     * @return The clone.
     */
    @Override
    public abstract Object clone();
}
