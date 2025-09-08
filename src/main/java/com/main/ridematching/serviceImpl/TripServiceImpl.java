package com.main.ridematching.serviceImpl;

import com.main.ridematching.dtos.*;
import com.main.ridematching.entity.Trip;
import com.main.ridematching.repo.TripRepo;
import com.main.ridematching.service.GraphHopperService;
import com.main.ridematching.service.MatchingService;
import com.main.ridematching.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepo tripRepo;
    private final GraphHopperService graphHopperService;
    private final MatchingService matchingService;

    @Override
    public TripResponse createTrip(TripRequest tripRequest) {
        Trip trip = convertToTrip(tripRequest);
        trip = tripRepo.save(trip);
        return convertToResponse(trip);
    }

    @Override
    public List<TripResponse> getTrips() {
        List<Trip> trips = tripRepo.findAll();
        return trips.stream().map(this::convertToResponse).toList();
    }

    @Override
    public TripResponse getTripById(long tripId) {
        Trip trip = tripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip with this id: " + tripId + " Not Found"));
        return convertToResponse(trip);
    }

    @Override
    public TripResponse updateTrip(TripUpdateRequest tripRequest) {
        Trip trip = tripRepo.findById(tripRequest.getId())
                .orElseThrow(() -> new RuntimeException("Trip with id " + tripRequest.getId() + " not found"));

        if (tripRequest.getPickupLat() != null) trip.setPickupLat(tripRequest.getPickupLat());
        if (tripRequest.getPickupLng() != null) trip.setPickupLng(tripRequest.getPickupLng());
        if (tripRequest.getDropLat() != null) trip.setDropLat(tripRequest.getDropLat());
        if (tripRequest.getDropLng() != null) trip.setDropLng(tripRequest.getDropLng());
        if (tripRequest.getDepartureTime() != null) trip.setDepartureTime(tripRequest.getDepartureTime());

        Trip updatedTrip = tripRepo.save(trip);
        return convertToResponse(updatedTrip);
    }

    @Override
    public void deleteTrip(Long tripId) {
        Trip trip = tripRepo.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip with this id: " + tripId + " Not Found"));
        tripRepo.delete(trip);
    }

    @Override
    public MatchResponse findMatching(Long tripId) {
        List<MatchResult> matches = matchingService.findMatches(tripId);
        TripResponse currTrip = getTripById(tripId);
        List<MatchingTrips> suggestions = matches.stream().map(this::convertToMatching).toList();
        return MatchResponse.builder()
                .trip(currTrip)
                .suggestedMatches(suggestions)
                .build();
    }

    private TripResponse convertToResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .pickupLng(trip.getPickupLng())
                .pickupLat(trip.getPickupLat())
                .dropLng(trip.getDropLng())
                .dropLat(trip.getDropLat())
                .departureTime(trip.getDepartureTime())
                .routes(String.format(
                        "https://graphhopper.com/maps/?point=%f,%f&point=%f,%f&vehicle=car",
                        trip.getPickupLat(),
                        trip.getPickupLng(),
                        trip.getDropLat(),
                        trip.getDropLng()
                ))
                .pickupLocation(graphHopperService.getLocationName(trip.getPickupLat(), trip.getPickupLng()))
                .dropLocation(graphHopperService.getLocationName(trip.getDropLat(), trip.getDropLng()))
                .build();
    }

    private Trip convertToTrip(TripRequest tripRequest) {
        return new Trip(null,
                tripRequest.getPickupLat(),
                tripRequest.getPickupLng(),
                tripRequest.getDropLat(),
                tripRequest.getDropLng(),
                tripRequest.getDepartureTime());
    }

    private MatchingTrips convertToMatching(MatchResult matchResult) {
        TripResponse response = getTripById(matchResult.matchingRiderTripId());
        return MatchingTrips.builder()
                .trip(response)
                .matchPercentage(matchResult.matchPercentage())
                .overlapPercentage(matchResult.overlapPercentage())
                .additionalDistanceKm(matchResult.additionalDistanceMeters() / 1000.0)
                .build();
    }
}
