package com.example.foursquare.requestModel;

import lombok.Data;

@Data
public class FeatureRequest {
    private boolean acceptsCreditCards;
    private boolean delivery;
    private boolean dogFriendly;
    private boolean familyFriendlyPlaces;
    private boolean inWalkingDistance;
    private boolean outdoorSeating;
    private boolean parking;
    private boolean wiFi;

}
