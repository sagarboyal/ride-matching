package com.main.ridematching.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripResponse {
    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;
    public Instant departureTime;
}
