package math;

import scope.LatLon;

/*
 * Represents an orthogonal projection onto the tangential plane
 * of the given center point.
 *
 * This projection projects each point on earth`s surface onto the
 * projection plane by drawing a line through the point, which is at
 * a right angle to the projection plane.
 *
 * It can be used for large areas (significant parts of a hemisphere)
 * centered anywhere on the earth. If the center is located at one of
 * the poles the longitude is used to determine the orientation (opposite
 * longitude is "north").
 *
 * Copyright (C) 2004 Stefan Bissell
 */
public class OrthographicProjection extends Projection {

    private class Point3D {

        private double x;
        private double y;
        private double z;

        public Point3D(double val1, double val2, double val3) {
            x = val1;
            y = val2;
            z = val3;
        }
    }

    private Point3D centerVector;
    private Point3D centerTangent;

    /**
     * Constructs a new projection which is centered at the given location.
     *
     * @param center The center of the projection given in degrees of
     * longitude/latitude. Positive values are east/north. If the center
     * is located at one of the poles the longitude is used to determine
     * the orientation (opposite longitude is "north").
     */
    public OrthographicProjection(LatLon center) {
        super(center);

        if (center.lon != center.lon // check for "NaN"
                || center.lon > 180.0 || center.lon < -180.0) {
            return;
            //throw new IllegalArgumentException("Illegal longitude: " + center.x);
        }

        if (center.lat != center.lat // check for "NaN"
                || center.lat > 90.0 || center.lat < -90.0) {
            return;
            //throw new IllegalArgumentException("Illegal latitude: " + center.y);
        }

        centerVector = computeVector(center.lat, center.lon);
        centerTangent = computeTangent(center.lon);
    }

    /**
     * Re-centers the projection at the given location.
     *
     * @param centerval The center of the projection given in degrees of
     * longitude/latitude. Positive values are east/north.
     */
    @Override
    public void setCenter(LatLon centerval) {
        if (centerval.lon != centerval.lon // check for "NaN"
                || centerval.lon > 180.0 || centerval.lon < -180.0) {
            return;
            //throw new IllegalArgumentException("Illegal longitude: " + center.x);
        }

        if (centerval.lat != centerval.lat // check for "NaN"
                || centerval.lat > 90.0 || centerval.lat < -90.0) {
            return;
            //throw new IllegalArgumentException("Illegal latitude: " + center.y);
        }

        center = centerval;
        centerVector = computeVector(centerval.lat, centerval.lon);
        centerTangent = computeTangent(centerval.lon);
    }
    
    @Override
    public LatLon convertToMeters(LatLon point) {
        return convertToMeters(point.lat, point.lon);
    }
    
    /**
     * Converts the given lon/lat coordinates to meters on the
     * projection plane measured from the center.
     *
     * @param lon The longitude of the coordinates in degrees.
     * Positive values are east.
     *
     * @param lat The latitude of the coordinates in degrees.
     * Positive values are north.
     *
     * @return Point2D The corresponding coordinates on the projection
     * plane, measured in meters from the center.
     */
    @Override
    public LatLon convertToMeters(double lat, double lon) {
        if (lon > 180.0 || lon < -180.0) {
            return new LatLon();
        }

        if (lat > 90.0 || lat < -90.0) {
            return new LatLon();
        }

        // subtract vector from center to given point
        Point3D p = computeVector(lat, lon);
        p.x -= centerVector.x;
        p.y -= centerVector.y;
        p.z -= centerVector.z;

        // rotate along tangent of latitude toward equator
        double a = Math.toRadians(-center.lat);
        double s = Math.sin(a);
        double c = Math.cos(a);
        double u = 1.0 - c;

        Point3D t = centerTangent;

        double x = p.x * ((t.x * t.x) * u + c) +
                p.y * (t.y * t.x * u - t.z * s) +
                p.z * (t.z * t.x * u + t.y * s);

        double y = p.x * (t.x * t.y * u + t.z * s) +
                p.y * ((t.y * t.y) * u + c) +
                p.z * (t.z * t.y * u - t.x * s);
        
        double z = p.x * (t.x * t.z * u - t.y * s) +
                p.y * (t.y * t.z * u + t.x * s) +
                p.z * ((t.z * t.z) * u + c);

        // rotate along y-axis toward prime meridian
        a = Math.toRadians(-center.lon);
        x = z * Math.sin(a) + x * Math.cos(a);

        // drop z-coordinate (project onto plane)
        return new LatLon(y, x);
    }

    /**
     * Converts the given x/y-coordinates on the projection plane
     * to coordinates on earth`s surface.
     *
     * @param lon The x-coordinate on the projection plane, measured
     * in meters from the center.
     *
     * @param lat The y-coordinate on the projection plane, measured
     * in meters from the center.
     *
     * @return a LatLon coordinates on earth`s surface
     * in degrees. Positive values are east/north.
     */
    @Override
    public LatLon convertToCoords(double lat, double lon) {
        // calculate distance from center
        double d = Math.sqrt(lat * lat + lon * lon);

        // check whether click is inside earth`s radius
        if (d > EARTH_RADIUS) {
            return new LatLon();
        }

        // restore z-coordinate (project onto sphere)
        double z = Math.sqrt((EARTH_RADIUS * EARTH_RADIUS) - (d * d)) - EARTH_RADIUS;

        // rotate along y-axis toward center meridian
        double a = Math.toRadians(center.lon);
        double x1 = z * Math.sin(a) + lon * Math.cos(a);
        double y1 = lat;
        double z1 = z * Math.cos(a) - lon * Math.sin(a);

        // rotate along tangent of latitude toward center
        a = Math.toRadians(center.lat);
        double s = Math.sin(a);
        double c = Math.cos(a);
        double u = 1.0 - c;

        Point3D t = centerTangent;

        double x2 = x1 * (t.x * t.x * u + c) +
                y1 * (t.y * t.x * u - t.z * s) +
                z1 * (t.z * t.x * u + t.y * s);

        double y2 = x1 * (t.x * t.y * u + t.z * s) +
                y1 * (t.y * t.y * u + c) +
                z1 * (t.z * t.y * u - t.x * s);
        
        double z2 = x1 * (t.x * t.z * u - t.y * s) +
                y1 * (t.y * t.z * u + t.x * s) +
                z1 * (t.z * t.z * u + c);

        // compute vector from earth's center
        x2 += centerVector.x;
        y2 += centerVector.y;
        z2 += centerVector.z;

        // compute lat/lon (in radians)
        double latitude = Math.asin(y2 / EARTH_RADIUS);
        double latRadius = Math.cos(latitude) * EARTH_RADIUS;
        double rad = x2 / latRadius;
        double longitude;

        if (z2 >= 0.0) {
            longitude = Math.asin(rad);
        } else {
            if (x2 >= 0.0) {
                longitude = Math.PI - Math.asin(rad);
            } else {
                longitude = -Math.PI - Math.asin(rad);
            }
        }

        // convert to degrees
        latitude = Math.toDegrees(latitude);
        longitude = Math.toDegrees(longitude);

        return new LatLon(latitude, longitude);
    }

    @Override
    public Object clone() {
        return new OrthographicProjection(new LatLon(center.lat, center.lon));
    }

    private Point3D computeVector(double lat, double lon) {
        double sinLat = Math.sin(Math.toRadians(lat));
        double cosLat = Math.cos(Math.toRadians(lat));
        double sinLon = Math.sin(Math.toRadians(lon));
        double cosLon = Math.cos(Math.toRadians(lon));

        double latRadius = cosLat * EARTH_RADIUS;

        return new Point3D(
                sinLon * latRadius,
                sinLat * EARTH_RADIUS,
                cosLon * latRadius);
    }

    private Point3D computeTangent(double lon) {
        double l = lon - 90.0;
        double sinLon = Math.sin(Math.toRadians(l));
        double cosLon = Math.cos(Math.toRadians(l));

        return new Point3D(sinLon, 0.0, cosLon);
    }
}

