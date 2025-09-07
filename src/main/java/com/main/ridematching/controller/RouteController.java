package com.main.ridematching.controller;


import com.main.ridematching.dtos.GraphHopperResponse;
import com.main.ridematching.service.GraphHopperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route")
@RequiredArgsConstructor
public class RouteController {
    private final GraphHopperService graphHopperService;

    @GetMapping("/details/{tripId}")
    public ResponseEntity<GraphHopperResponse> getRouteDetails(@PathVariable Long tripId) {
        GraphHopperResponse response = graphHopperService.getRoute(tripId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
