package com.main.ridematching.utility;

public class Haversine {
    private static final int EARTH_RADIUS_KM = 6371;
    /**
     * Calculates the distance between two lat/lng points in meters.
     */
    public static double distance(double startLat, double startLng, double endLat, double endLng) {
        double dLat = Math.toRadians(endLat - startLat);
        double dLng = Math.toRadians(endLng - startLng);

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(startLat) * Math.cos(endLat) * Math.pow(Math.sin(dLng / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS_KM * c * 1000;
    }
}