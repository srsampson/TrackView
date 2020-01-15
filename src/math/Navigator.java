package math;

import scope.LatLon;

/*
 * Classes which extend this interface can be used to calculate navigational
 * data, e.g. the location an aircraft would reach in a given time.
 *
 * Copyright (C) 2004 Stefan Bissell
 */
public interface Navigator {

    /**
     * Calculates the future location (in lon/lat coordinates) which is the 
     * given number of nautical miles in the given direction (in degrees)
     * from the given starting location.
     *
     * @param start The stating location in lon/lat coordinates. Positive
     * values are north/east.
     *
     * @param heading The heading (in degrees) to the desired location.
     *
     * @param miles The distance (in nautical miles) to the desired location.
     *
     * @return a Point2D The location which lies the given number of miles in the
     * given direction from the given starting location.
     */
    public LatLon getFutureLocation(LatLon start,
            double heading, double miles);

    /**
     * Calculates the future location, an aircraft reaches that travels at 
     * the given speed (in knots) in the given groundtrack (degrees) for a
     * given time (seconds) starting at the given location.
     *
     * @param start The starting location in lat/lon coordinates. Positive
     * values are north/east.
     *
     * @param groundtrack The groundtrack (degrees).
     * @param speed The speed (knots).
     * @param time The time (seconds) into the future.
     *
     * @return LatLon The location the aircraft would reach given the above settings.
     */
    public LatLon getFutureLocation(LatLon start, double groundtrack, double speed, double time);

    /**
     * Calculates the distance between two given points (lat/lon coordinates) 
     * on earth.
     *
     * @param first First of two given lat/lon coordinates whose distance is to
     * be calculated.
     *
     * @param second Second of two given lat/lon coordinates whose distance is to
     * be calculated.
     *
     * @return a float The distance between the two given points (in nautical miles).
     */
    public double getDistance(LatLon first, LatLon second);

    /**
     * Calculates the bearing between two given points (lat/lon coordinates)
     * on earth.
     *
     * @param first First of two given lat/lon coordinates whose bearing is to
     * be calculated.
     *
     * @param second Second of two given lat/lon coordinates whose bearing is to
     * be calculated.
     *
     * @return a float The bearing between the two given points (in degrees true north).
     */
    public double getBearing(LatLon first, LatLon second);
}
