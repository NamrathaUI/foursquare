package com.example.foursquare.responseModel;

import lombok.Data;

@Data
public class PlaceResponse {
    private long placeId;
    private String name;
    private String type;
    private double distance;
    private String address;
    private int priceRange;
    private float ratings;
    private String image;
    private String phoneNumber;

}
