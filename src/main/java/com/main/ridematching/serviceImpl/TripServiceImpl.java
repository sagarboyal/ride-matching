package com.main.ridematching.serviceImpl;

import com.main.ridematching.dtos.TripRequest;
import com.main.ridematching.dtos.TripResponse;
import com.main.ridematching.entity.Trip;
import com.main.ridematching.repo.TripRepo;
import com.main.ridematching.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private TripResponse convertToResponse(Trip trip) {
        return TripResponse.builder()
                .pickupLng(trip.getPickupLng())
                .pickupLat(trip.getPickupLat())
                .dropLng(trip.getDropLng())
                .dropLat(trip.getDropLat())
                .departureTime(trip.getDepartureTime())
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
