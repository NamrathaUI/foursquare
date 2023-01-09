package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Feedback;
import com.example.foursquare.model.Ratings;
import com.example.foursquare.model.Users;

import com.example.foursquare.requestModel.SearchRequest;
import com.example.foursquare.responseModel.*;

import java.io.IOException;
import java.util.List;

public interface IUserService {
    String addFavourite(Users users, long placeId);

    List<PlaceResponse> viewFavourite(Users users, double latitude, double longitude) throws CustomException;

    String giveRatings(Users users, long placeId, float numberOfStarsRated) throws CustomException;

    String addReview(Users users, String review, long placeId, List<String> url) throws CustomException;

    List<ReviewResponse> viewReview(long placeId);

    List<PlaceResponse> nearMe(double latitude, double longitude ) throws CustomException;

    List<PlaceResponse> topPick(double latitude, double longitude) throws CustomException;

    List<PlaceResponse> popular(double latitude, double longitude) throws CustomException;
    List<PlaceResponse> cafe(double latitude,double longitude) throws CustomException;

    List<PlaceResponse> lunch(double latitude, double longitude) throws CustomException;

    List<Ratings> viewRatings(long placeId);

    UserResponse editProfile(String profilePic, long userId) throws IOException;

    String giveFeedback(Users users, String feedback);

    List<Feedback> viewFeedback();

    List<Feedback> feedback(Users users,long userId);
    List<ImageResponse> images(long placeId);
    List<PlaceOverviewResponse> placeDetails(long placeId,double latitude,double longitude);

    List<PlaceResponse> search( SearchRequest searchRequest) throws CustomException;

    String viewAboutUs();

    ProfileResponse viewProfile(long userId);


    List<PlaceResponse> filterFavourite(long userId, String option, SearchRequest searchRequest, double latitude, double longitude) throws CustomException;

    List<NearPlaceResponse> nearByPlace(double longitude,double latitude) throws CustomException;
}
