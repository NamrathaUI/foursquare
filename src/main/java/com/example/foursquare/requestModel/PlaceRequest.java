package com.example.foursquare.requestModel;

import lombok.Data;

@Data
public class PlaceRequest {
    private String name;
    private String address;
    private String city;
    private String type;
    private int priceRange;
    private String phoneNumber;
    private String images;
    private boolean lunch;
    private boolean cafe;
    private double longitude;
    private double latitude;
    private float currentRatings;
    private String aboutUs;
    private boolean acceptsCreditCards;
    private boolean delivery;
    private boolean dogFriendly;
    private boolean familyFriendlyPlaces;
    private boolean inWalkingDistance;
    private boolean outdoorSeating;
    private boolean parking;
    private boolean wiFi;
}
