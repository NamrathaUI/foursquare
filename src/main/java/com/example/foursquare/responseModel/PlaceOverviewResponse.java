package com.example.foursquare.responseModel;

import lombok.Data;

@Data
public class PlaceOverviewResponse {
    private String name;
    private String type;
    private String aboutUs;
    private float currentRatings;
    private String images;
    private String phoneNumber;
    private String address;
    private double latitude;
    private double longitude;
    private double distance;


}
