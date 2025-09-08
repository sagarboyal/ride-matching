package com.main.ridematching.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GraphHopperGeocodeResponse(List<Hit> hits) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hit(String name, String country, String state, String city) {}
}