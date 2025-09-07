package com.main.ridematching.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Path(
    double distance,
    long time,
    String points
) {}