package com.main.ridematching.serviceImpl;

import com.main.ridematching.dtos.TripRequest;
import com.main.ridematching.dtos.TripResponse;
import com.main.ridematching.entity.Trip;
import com.main.ridematching.repo.TripRepo;
import com.main.ridematching.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepo tripRepo;

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
                .orElseThrow(() -> new RuntimeException("Trip with this id: "+tripId+" Not Found"));
        return convertToResponse(trip);
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
}
