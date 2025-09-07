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
                    .block();
        } catch (Exception e) {
            logger.error("Failed to get route for coordinates: [{},{}] to [{},{}]. Error: {}",
                    response.getPickupLat(), response.getPickupLng(),
                    response.getDropLat(), response.getDropLng(),
                    e.getMessage());
            return null;
        }
    }

    public GraphHopperResponse getRouteWithWaypoints(
            double originLat, double originLng,
            double pickupLat, double pickupLng,
            double dropLat, double dropLng,
            double destinationLat, double destinationLng) {

        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/route")
                            .queryParam("point", originLat + "," + originLng)
                            .queryParam("point", pickupLat + "," + pickupLng)
                            .queryParam("point", dropLat + "," + dropLng)
                            .queryParam("point", destinationLat + "," + destinationLng)
                            .queryParam("vehicle", "car")
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(GraphHopperResponse.class)
                    .doOnError(e -> logger.error("Error calling GraphHopper API with waypoints: {}", e.getMessage()))
                    .map(route -> {
                        if (route.paths() != null && !route.paths().isEmpty()) {
                            logger.info("Route distance: {} meters", route.paths().get(0).distance());
                            String polyline = route.paths().get(0).points(); // later for overlapping calculation
                        }
                        return route;
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Failed to get route with waypoints: [{}], [{}], [{}], [{}]. Error: {}",
                    originLat, originLng, pickupLat + "," + pickupLng, dropLat + "," + dropLng, e.getMessage());
            return null;
        }
    }

}

