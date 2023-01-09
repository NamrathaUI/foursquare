package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Places;
import com.example.foursquare.model.Users;
import com.example.foursquare.requestModel.PlaceRequest;

public interface IAdminService {
    String addPlaces(Users users, PlaceRequest placeRequest, String url) throws CustomException;

    int deletePlaces(long placeId);

    String updatePlaces(Users users, long placeId, String name, Integer priceRange, String type, String aboutUs) throws CustomException;


}
