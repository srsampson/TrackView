package math;

import scope.LatLon;

// Copyright (C) 2004 Stefan Bissell

public class OrthographicNavigator implements Navigator {

    private static final float METERS_PER_NM = 1852.0F;
    //
    private OrthographicProjection projection;

    public OrthographicNavigator(LatLon center) {
        projection = new OrthographicProjection(center);
    }

    public OrthographicNavigator() {
        projection = new OrthographicProjection(new LatLon());
    }

    public OrthographicProjection getProjection() {
        return projection;
    }

    @Override
    public LatLon getFutureLocation(LatLon start, double heading, double nmiles) {
        projection.setCenter(start);

        double dist = nmiles * METERS_PER_NM;
        double deg;

        if (heading <= 90.0) {
            deg = 90.0 - heading;
        } else {
            if (heading <= 270.0) {
                deg = -(heading - 90.0);
            } else {
                deg = 90.0 + (360.0 - heading);
            }
        }

        double angle = Math.toRadians(deg);
        double lat = Math.sin(angle) * dist;
        double lon = Math.cos(angle) * dist;

        return projection.convertToCoords(lat, lon);
    }

    @Override
    public LatLon getFutureLocation(LatLon start, double heading, double speed, double time) {
        return getFutureLocation(start, heading, speed * time / 3600.0);
    }

    @Override
    public double getDistance(LatLon first, LatLon second) {
        projection.setCenter(first);
        LatLon p = projection.convertToMeters(second);
        
        return Math.sqrt(p.lat * p.lat + p.lon * p.lon) / METERS_PER_NM;
    }

    @Override
    public double getBearing(LatLon first, LatLon second) {
        double lat1 = Math.toRadians(first.lat);
        double lon1 = Math.toRadians(first.lon);
        double lat2 = Math.toRadians(second.lat);
        double lon2 = Math.toRadians(second.lon);

        double slat1 = Math.sin(lat1);
        double slat2 = Math.sin(lat2);
        double clat1 = Math.cos(lat1);
        double clat2 = Math.cos(lat2);
        double val;
        double ret;

        /*
         * This sometimes returns a Double.NaN so catch any exception
         * and return -1 so the calling routine will ignore the value
         */

        try {
            double d = Math.acos(slat1 * slat2 + clat1 * clat2 * Math.cos(lon1 - lon2));

            if (Math.sin(lon2 - lon1) < 0.0) {
                val = Math.acos((slat2 - slat1 * Math.cos(d)) / (Math.sin(d) * clat1));
            } else {
                val = 2.0 * Math.PI - Math.acos((slat2 - slat1 * Math.cos(d)) / (Math.sin(d) * clat1));
            }

            ret = Math.toDegrees(val);
        } catch (Exception e) {
            System.err.println("OrthographicNavigator::getBearing exception " + e.toString());
            return -1.0;
        }

        return (360.0 - ret);
    }
}
