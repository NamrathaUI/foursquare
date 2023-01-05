package com.example.foursquare.responseModel;

import lombok.Data;

@Data
public class PlaceOverviewResponse {
    private String aboutUs;
    private String type;
    private float ratings;
    private String image;
    private String phoneNumber;
    private String name;
    private String address;

}
