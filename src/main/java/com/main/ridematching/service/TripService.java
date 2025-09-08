package com.main.ridematching.service;

import com.main.ridematching.dtos.TripRequest;
import com.main.ridematching.dtos.TripResponse;
import com.main.ridematching.dtos.TripUpdateRequest;

import java.util.List;

public interface TripService {
    TripResponse createTrip(TripRequest tripRequest);
    List<TripResponse> getTrips();
    TripResponse getTripById(long tripId);
    TripResponse updateTrip(TripUpdateRequest tripRequest);
    void deleteTrip(Long tripId);
}
