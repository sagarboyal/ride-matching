package com.main.ridematching.controller;

import com.main.ridematching.dtos.*;
import com.main.ridematching.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@RequestBody TripRequest tripRequest) {
        return ResponseEntity.ok(tripService.createTrip(tripRequest));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips() {
        return ResponseEntity.ok(tripService.getTrips());
    }

    @PutMapping
    public ResponseEntity<TripResponse> updateTrip(@RequestBody TripUpdateRequest tripRequest) {
        return ResponseEntity.ok(tripService.updateTrip(tripRequest));
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long tripId) {
        tripService.deleteTrip(tripId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/matches/{tripId}")
    public ResponseEntity<MatchResponse> getMatches(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.findMatching(tripId));
    }
}
