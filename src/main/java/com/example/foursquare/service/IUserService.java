package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Users;
import com.example.foursquare.responseModel.PlaceResponse;

import java.util.List;

public interface IUserService {
    String addFavourite(Users users, long placeId);

     List<PlaceResponse> viewFavourite(Users users, double latitude, double longitude);

    String giveRatings(Users users, long placeId, float numberOfStarsRated) throws CustomException;
    String addReview(Users users ,String review,long placeId,String url) throws CustomException;



}
