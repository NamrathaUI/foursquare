package com.example.foursquare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Places {
    private long placeId;
    private String name;
    private String address;
    private String type;
    private int priceRange;
    private String phoneNumber;
    private String images;
    private double longitude;
    private double latitude;
    private float currentRatings;
    private String aboutUs;
    private String isDeleted;

}