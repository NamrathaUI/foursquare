package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Favourites;
import com.example.foursquare.model.Places;
import com.example.foursquare.model.Users;

import java.util.List;

public interface IUserService {
    String addFavourite(Users users, long placeId);

     List<Places> viewFavouritePlaces(Users users);

    String ratePlace(Users users, long placeId, float numberOfStarsRated) throws CustomException;
}
