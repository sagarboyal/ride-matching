package com.main.ridematching.service;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import com.main.ridematching.dtos.GraphHopperResponse;
import com.main.ridematching.dtos.MatchResult;
import com.main.ridematching.entity.Trip;
import com.main.ridematching.repo.TripRepo;
import com.main.ridematching.utility.Haversine;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private static final Logger logger = LoggerFactory.getLogger(MatchingService.class);
    private static final long TIME_WINDOW_MINUTES = 30;
    private static final double MAX_DEVIATION_PERCENTAGE = 0.15;
    private static final double OVERLAP_PROXIMITY_METERS = 150.0;

    private final TripRepo tripRepository;
    private final GraphHopperService graphHopperService;

    public List<MatchResult> findMatches(Long newTripId) {
        Trip newTrip = tripRepository.findById(newTripId)
                .orElseThrow(() -> new IllegalArgumentException("New trip not found with ID: " + newTripId));

        List<Trip> potentialTrips = tripRepository.findAll();
        List<MatchResult> matches = new ArrayList<>();

        for (Trip existingTrip : potentialTrips) {
            if (existingTrip.getId().equals(newTrip.getId())) {
                continue;
            }
            calculateMatch(newTrip, existingTrip).ifPresent(matches::add);
        }

        matches.sort(Comparator.comparing(MatchResult::matchPercentage).reversed());
        return matches;
    }

    private Optional<MatchResult> calculateMatch(Trip newTrip, Trip existingTrip) {
        long timeDifference = Duration.between(newTrip.getDepartureTime(), existingTrip.getDepartureTime()).abs().toMinutes();
        if (timeDifference > TIME_WINDOW_MINUTES) return Optional.empty();

        GraphHopperResponse combinedRouteResponse = graphHopperService.getRouteWithWaypoints(
                existingTrip.getPickupLat(), existingTrip.getPickupLng(),
                newTrip.getPickupLat(), newTrip.getPickupLng(),
                newTrip.getDropLat(), newTrip.getDropLng(),
                existingTrip.getDropLat(), existingTrip.getDropLng()
        );

        if (combinedRouteResponse == null || combinedRouteResponse.paths().isEmpty()) return Optional.empty();

        double combinedDistance = combinedRouteResponse.paths().get(0).distance();

        GraphHopperResponse existingTripRoute = graphHopperService.getRoute(existingTrip.getId());
        if (existingTripRoute == null || existingTripRoute.paths().isEmpty()) return Optional.empty();

        double existingTripDistance = existingTripRoute.paths().get(0).distance();
        double deviationDistance = combinedDistance - existingTripDistance;
        double deviationPercent = deviationDistance / existingTripDistance;

        if (deviationPercent < 0 || deviationPercent > MAX_DEVIATION_PERCENTAGE) return Optional.empty();

        String existingPolyline = existingTripRoute.paths().get(0).points();
        GraphHopperResponse newTripRoute = graphHopperService.getRoute(newTrip.getId());
        if (newTripRoute == null || newTripRoute.paths().isEmpty()) return Optional.empty();
        String newPolyline = newTripRoute.paths().get(0).points();

        double overlapPercentage = calculateOverlapPercentage(newPolyline, existingPolyline);

        double overlapScore = overlapPercentage / 100.0;
        double deviationScore = 1.0 - (deviationPercent / MAX_DEVIATION_PERCENTAGE);
        double timeScore = 1.0 - ((double) timeDifference / TIME_WINDOW_MINUTES);

        double finalMatchScore = (overlapScore * 0.5) + (deviationScore * 0.3) + (timeScore * 0.2);
        double finalMatchPercentage = Math.round(finalMatchScore * 1000.0) / 10.0;

        if (finalMatchPercentage < 40) return Optional.empty();

        return Optional.of(new MatchResult(
                existingTrip.getId(),
                finalMatchPercentage,
                Math.round(overlapPercentage * 10.0) / 10.0,
                Math.round(deviationDistance)
        ));
    }

    private double calculateOverlapPercentage(String polyline1, String polyline2) {
        List<LatLng> decoded1 = PolylineEncoding.decode(polyline1);
        List<LatLng> decoded2 = PolylineEncoding.decode(polyline2);

        if (decoded1.isEmpty() || decoded2.isEmpty()) return 0.0;

        int overlappingPoints = 0;
        for (LatLng p1 : decoded1) {
            for (LatLng p2 : decoded2) {
                if (Haversine.distance(p1.lat, p1.lng, p2.lat, p2.lng) <= OVERLAP_PROXIMITY_METERS) {
                    overlappingPoints++;
                    break;
                }
            }
        }
        return ((double) overlappingPoints / decoded1.size()) * 100.0;
    }
}
