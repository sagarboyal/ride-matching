package com.main.ridematching.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchingTrips {
    private TripResponse trip;
    private double matchPercentage;
    private double overlapPercentage;
    private double additionalDistanceKm;
}
