package com.main.ridematching.dtos;

public record MatchResult(
    long matchingRiderTripId,
    double matchPercentage,
    double overlapPercentage,
    double additionalDistanceMeters
) {}