package com.example.foursquare.responseModel;

import lombok.Data;

@Data
public class PlaceResponse {
    private String name;
    private String Type;
    private double distance;
    private String address;
    private int priceRange;
    private int ratings;
}
