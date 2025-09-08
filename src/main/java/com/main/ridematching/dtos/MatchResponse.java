package com.main.ridematching.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MatchResponse {
    private TripResponse trip;
    List<MatchingTrips> suggestedMatches;
}
