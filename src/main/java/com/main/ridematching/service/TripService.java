package com.main.ridematching.service;

import com.main.ridematching.dtos.TripRequest;
import com.main.ridematching.dtos.TripResponse;

public interface TripService {
    TripResponse createTrip(TripRequest tripRequest);
}
