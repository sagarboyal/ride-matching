package com.main.ridematching.service;

import com.main.ridematching.dtos.GraphHopperResponse;
import com.main.ridematching.dtos.TripResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GraphHopperService {

    private static final Logger logger = LoggerFactory.getLogger(GraphHopperService.class);
    private final WebClient webClient;
    private final String apiKey;
    private final TripService tripService;

    public GraphHopperService(@Value("${graphhopper.key}") String apiKey, TripService tripService) {
        this.apiKey = apiKey;
        this.webClient = WebClient.create("https://graphhopper.com/api/1");
        this.tripService = tripService;
    }

    public GraphHopperResponse getRoute(Long tripId) {
        TripResponse response = tripService.getTripById(tripId);
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/route")
                            .queryParam("point", response.getPickupLat() + "," + response.getPickupLng())
                            .queryParam("point", response.getDropLat() + "," + response.getDropLng())
                            .queryParam("vehicle", "car")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(GraphHopperResponse.class)
                    .doOnError(e -> logger.error("Error calling GraphHopper API: {}", e.getMessage()))
                    .block(); // still blocking for simplicity
        } catch (Exception e) {
            logger.error("Failed to get route for coordinates: [{},{}] to [{},{}]. Error: {}",
                    response.getPickupLat(), response.getPickupLng(),
                    response.getDropLat(), response.getDropLng(),
                    e.getMessage()); // only log error message
            return null;
        }
    }

}
