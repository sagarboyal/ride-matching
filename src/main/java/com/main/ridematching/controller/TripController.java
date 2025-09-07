package com.main.ridematching.controller;

import com.main.ridematching.dtos.MatchResult;
import com.main.ridematching.dtos.TripRequest;
import com.main.ridematching.dtos.TripResponse;
import com.main.ridematching.service.MatchingService;
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
    private final MatchingService matchingService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@RequestBody TripRequest tripRequest) {
        return ResponseEntity.ok(tripService.createTrip(tripRequest));
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips() {
        return ResponseEntity.ok(tripService.getTrips());
    }

    @GetMapping("/matches/{tripId}")
    public ResponseEntity<List<MatchResult>> getMatches(@PathVariable Long tripId) {
        try {
            List<MatchResult> matches = matchingService.findMatches(tripId);
            return ResponseEntity.ok(matches);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
