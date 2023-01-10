package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.*;
import com.example.foursquare.requestModel.SearchRequest;
import com.example.foursquare.responseModel.*;
import com.example.foursquare.service.system.SystemInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService implements IUserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SystemInterface systemInterface;

    @Override
    public String addFavourite(Users users, long placeId) {

        List<Places> places = jdbcTemplate.query("select * " +
                "from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), placeId);
        if (places.isEmpty()) {
            return "places not found";
        }
        List<Favourites> favourites = jdbcTemplate.query("select *from favourites where user_id=? and place_id=?", new BeanPropertyRowMapper<>(Favourites.class), users.getUserId(), placeId);
        if (favourites.isEmpty()) {
            jdbcTemplate.update("insert into  favourites values(?,?)", users.getUserId(), placeId);
            return "Places is added to your favourite list";
        }
        jdbcTemplate.update("delete from favourites where user_id=? and place_id=?", users.getUserId(), placeId);
        return "places removed from favourite";
    }

    @Override
    public List<PlaceResponse> viewFavourite(Users users, double latitude, double longitude) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        final int r = 6371;
        List<PlaceResponse> placeResponseList = new ArrayList<>();
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select * from places where place_id in (select place_id from favourites where user_id=?) ", new BeanPropertyRowMapper<>(Places.class), users.getUserId());
        if (!(places.isEmpty())) {
            for (Places p : places) {

                double lat1 = p.getLatitude();
                double long1 = p.getLongitude();
                long1 = Math.toRadians(long1);
                lat1 = Math.toRadians(lat1);
                double distance = 0;
                distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);

                PlaceResponse placeResponse = new PlaceResponse();
                placeResponse.setType(p.getType());
                placeResponse.setName(p.getName());
                placeResponse.setAddress(p.getAddress());
                placeResponse.setRatings(p.getCurrentRatings());
                placeResponse.setPriceRange(p.getPriceRange());
                placeResponse.setPlaceId(p.getPlaceId());
                placeResponse.setImage(p.getImages());
                placeResponse.setPhoneNumber(p.getPhoneNumber());
                placeResponse.setDistance(Double.valueOf(df.format(distance)));

                placeResponseList.add(placeResponse);

            }

        }
        return placeResponseList;
    }

    @Override
    public String giveRatings(Users users, long placeId, float numberOfStarsRated) throws CustomException {
        if(numberOfStarsRated>5 || numberOfStarsRated<=0){
            return "Rating cannot be greater than 5 or less than 0";
        }


        List<Places> places = jdbcTemplate.query("select *from places where place_id=? ", new BeanPropertyRowMapper<>(Places.class), placeId);
        List<Ratings> ratings1 = jdbcTemplate.query("select *from ratings where user_id=? and place_id=? ", new BeanPropertyRowMapper<>(Ratings.class), users.getUserId(), placeId);
        if (!(places.isEmpty())) {
            if (ratings1.isEmpty()) {
                jdbcTemplate.update("insert into ratings(user_id,place_id,number_of_stars_rated) values(?,?,?)", users.getUserId(), placeId, numberOfStarsRated);
            } else {
                jdbcTemplate.update("update ratings set number_of_stars_rated=? where place_id=? and user_id=?", numberOfStarsRated, placeId, users.getUserId());
            }


            if (places.get(0).getCurrentRatings() == 0) {
                jdbcTemplate.update("update places set current_ratings=? where place_id=? ", numberOfStarsRated, placeId);
            } else {
                List<Ratings> ratings = jdbcTemplate.query("select *from ratings where place_id=?", new BeanPropertyRowMapper<>(Ratings.class), placeId);
                float rating = 0;
                for (Ratings r : ratings) {
                    rating = rating + r.getNumberOfStarsRated();
                }

                float newRatings = rating / ratings.size();
                jdbcTemplate.update("update places set current_ratings =? where place_id=? ", newRatings, placeId);
            }
            return "Thank you for rating";
        }
        throw new CustomException("Places not found");

    }

    @Override
    public String
    addReview(Users users, String review, long placeId, List<String> url) throws CustomException {
        List<Places> places = jdbcTemplate.query("select * from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), placeId);
        if (places.isEmpty()) {
            throw new CustomException("places not found");
        }
        List<Review> reviews = jdbcTemplate.query("select * from review where user_id=? and place_id=?", new BeanPropertyRowMapper<>(Review.class), users.getUserId(), placeId);
        //List<ReviewPhotos> reviewPhotos = jdbcTemplate.query("select * from review_photos where user_id=? and place_id = ?", new BeanPropertyRowMapper<>(ReviewPhotos.class),users.getUserId(),placeId);

        if ((review == null || review.isBlank()) && (url == null || url.isEmpty())) {
           /* if(!(url==null || url.isEmpty())) {
                for (String s : url) {
                    if (!(s == null || s.isBlank())) {
                        jdbcTemplate.update("insert into review_photos(review_pics,place_id,user_id) values(?,?,?) ", s, placeId, users.getUserId());
                    }
                }
            }*/
            throw new CustomException("review  is null or photo is null");
        }
        if (!(review == null || review.isBlank())) {
            if (reviews.isEmpty()) {
                jdbcTemplate.update("insert into review (user_id,review,place_id,review_date) values(?,?,?,?)", users.getUserId(), review, placeId, LocalDateTime.now().toString());
            } else {
                jdbcTemplate.update("update review set review = ? where user_id = ? and place_id = ?", review, users.getUserId(), placeId);
            }
        }


        if (!(url == null || url.isEmpty())) {
            for (String s : url) {
                if (!(s == null || s.isBlank())) {
                    jdbcTemplate.update("insert into review_photos(review_pics,place_id,user_id,review_photo_date) values(?,?,?,?) ", s, placeId, users.getUserId(), LocalDate.now());
                }
            }
        }
/*
        if (reviews.isEmpty()) {
                jdbcTemplate.update("insert into review (user_id,review,place_id,review_date) values(?,?,?,?)", users.getUserId(), review, placeId, LocalDateTime.now().toString());
                Integer reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
                for (String s : url) {
                    if (!(s == null || s.isBlank())) {
                        jdbcTemplate.update("insert into review_photos values(?,?)", reviewId, s);
                    }
            }

        }
        else {
            jdbcTemplate.update("update review set review=? where user_id=? and place_id=?", review, users.getUserId(), placeId);
            for (String s : url) {
                if (!(s == null || s.isBlank())) {
                    jdbcTemplate.update("insert into review_photos values(?,?)", reviews.get(0).getReviewId(), s);
                }
            }

        }*/
        return "Review added Successfully";
    }

    @Override
    public List<ReviewResponse> viewReview(long placeId) {
        return jdbcTemplate.query("select profile_pic ,name,review,review_date from users inner join  review on users.user_id=review.user_id where place_id=?", new BeanPropertyRowMapper<>(ReviewResponse.class), placeId);
    }

    @Override
    public List<PlaceResponse> nearMe(double latitude, double longitude, int limit, int page, long userId) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        final int r = 6371;
        if(page<=0){
            page = Math.max(0,page-1);
        }

        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select * from places", new BeanPropertyRowMapper<>(Places.class));
        List<PlaceResponse> placeResponseList = new ArrayList<>();

        if (!(places.isEmpty())) {

            for (Places p : places) {
                double lat1 = p.getLatitude();
                double long1 = p.getLongitude();
                long1 = Math.toRadians(long1);
                lat1 = Math.toRadians(lat1);
                double distance = 0.0;
                distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);

                PlaceResponse placeResponse = new PlaceResponse();
                placeResponse.setType(p.getType());
                placeResponse.setName(p.getName());
                placeResponse.setAddress(p.getAddress());
                placeResponse.setRatings(p.getCurrentRatings());
                placeResponse.setPriceRange(p.getPriceRange());
                placeResponse.setPlaceId(p.getPlaceId());
                placeResponse.setImage(p.getImages());
                placeResponse.setPhoneNumber(p.getPhoneNumber());
                placeResponse.setFavourite(jdbcTemplate.queryForObject("select count(*) from favourites  where user_id=? and place_id=?", Integer.class,userId,p.getPlaceId())==1);
                placeResponse.setDistance(Double.valueOf(df.format(distance)));
                if (placeResponse.getDistance() < 50) {
                    placeResponseList.add(placeResponse);
                }


            }
        }

        for (int i = 0; i < placeResponseList.size() - 1; i++)
            for (int j = 0; j < placeResponseList.size() - i - 1; j++)
                if (placeResponseList.get(j).getDistance() > placeResponseList.get(j + 1).getDistance()) {
                    PlaceResponse temp = placeResponseList.get(j);
                    placeResponseList.set(j, placeResponseList.get(j + 1));
                    placeResponseList.set(j + 1, temp);
                }
        if(page==0){
            page=1;
        }
        if(page==1){
            if(limit>placeResponseList.size()){
                limit=placeResponseList.size();
            }

        }

        int startLimit = 0;
        int endLimit = limit;


        if (page==1)
            return placeResponseList.subList(startLimit,endLimit);
        else {
            startLimit =( (limit * page ) - limit ) ;
            if(startLimit>placeResponseList.size()){
                return new ArrayList<>();
            }
            endLimit=limit*page;
            return placeResponseList.subList(startLimit,endLimit);
        }
      //  return placeResponseList;
    }

    @Override
    public List<PlaceResponse> topPick(double latitude, double longitude, int limit, int page, long userId) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        final int r = 6371;

        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select * from places order by current_ratings desc limit ? offset ?", new BeanPropertyRowMapper<>(Places.class),limit ,page);
        List<PlaceResponse> placeResponseList = new ArrayList<>();
        if (!(places.isEmpty())) {

            for (Places p : places) {
                double lat1 = p.getLatitude();
                double long1 = p.getLongitude();
                long1 = Math.toRadians(long1);
                lat1 = Math.toRadians(lat1);
                double distance = 0;
                distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);

                PlaceResponse placeResponse = new PlaceResponse();
                placeResponse.setType(p.getType());
                placeResponse.setName(p.getName());
                placeResponse.setAddress(p.getAddress());
                placeResponse.setRatings(p.getCurrentRatings());
                placeResponse.setPriceRange(p.getPriceRange());
                placeResponse.setPlaceId(p.getPlaceId());
                placeResponse.setImage(p.getImages());
                placeResponse.setPhoneNumber(p.getPhoneNumber());
                placeResponse.setDistance(Double.valueOf(df.format(distance)));
                placeResponse.setFavourite(jdbcTemplate.queryForObject("select count(*) from favourites  where user_id=? and place_id=?", Integer.class,userId,p.getPlaceId())==1);

                placeResponseList.add(placeResponse);

            }
        }

        return placeResponseList;
    }

    @Override
    public List<PlaceResponse> popular(double latitude, double longitude, int limit, int page, long userId) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        if(page<=0){
            page = Math.max(0,page-1);
        }
        final int r = 6371;
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<PlaceResponse> placeResponseList = new ArrayList<>();
        Map<Long, Integer> pid = new HashMap<>();
        List<Places> places = jdbcTemplate.query("select * from places", new BeanPropertyRowMapper<>(Places.class));
        if (!(places.isEmpty())) {
            for (Places p : places) {
                Integer count = jdbcTemplate.queryForObject("select count(*) from review where place_id = ?", Integer.class, p.getPlaceId());
                pid.put(p.getPlaceId(), count);
            }
        }
        List<Map.Entry<Long, Integer>> nList = new ArrayList<>(pid.entrySet());
        nList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));//to save map in desc we use list
        for (Map.Entry<Long, Integer> map : nList) {
            List<Places> p = jdbcTemplate.query("select * from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), map.getKey());
            double lat1 = p.get(0).getLatitude();
            double long1 = p.get(0).getLongitude();
            long1 = Math.toRadians(long1);
            lat1 = Math.toRadians(lat1);
            double distance = 0;
            distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));


            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);

            PlaceResponse placeResponse = new PlaceResponse();
            placeResponse.setType(p.get(0).getType());
            placeResponse.setName(p.get(0).getName());
            placeResponse.setAddress(p.get(0).getAddress());
            placeResponse.setRatings(p.get(0).getCurrentRatings());
            placeResponse.setPriceRange(p.get(0).getPriceRange());
            placeResponse.setPlaceId(p.get(0).getPlaceId());
            placeResponse.setImage(p.get(0).getImages());
            placeResponse.setPhoneNumber(p.get(0).getPhoneNumber());
            placeResponse.setDistance(Double.valueOf(df.format(distance)));
            placeResponse.setFavourite(jdbcTemplate.queryForObject("select count(*) from favourites  where user_id=? and place_id=?", Integer.class,userId,p.get(0).getPlaceId())==1);

            placeResponseList.add(placeResponse);
        }
        if(page==0){
            page=1;
        }
        if(page==1){
            if(limit>placeResponseList.size()){
                limit=placeResponseList.size();
            }

        }


        int startLimit = 0;
        int endLimit = limit;


        if (page==1)
            return placeResponseList.subList(startLimit,endLimit);
        else {
            startLimit =( (limit * page ) - limit ) ;
            if(startLimit>placeResponseList.size()){
                return new ArrayList<>();
            }
            endLimit=limit*page;
            return placeResponseList.subList(startLimit,endLimit);
        }
        //return placeResponseList;
    }

    @Override
    public List<PlaceResponse> cafe(double latitude, double longitude, int limit, int page,long userId) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        final int r = 6371;
        if(page<=0){
            page = Math.max(0,page-1);
        }
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select *from places where cafe=true", new BeanPropertyRowMapper<>(Places.class));
        List<PlaceResponse> placeResponseList = new ArrayList<>();
        if (!(places.isEmpty())) {

            for (Places p : places) {
                double lat1 = p.getLatitude();
                double long1 = p.getLongitude();
                long1 = Math.toRadians(long1);
                lat1 = Math.toRadians(lat1);
                double distance = 0;
                distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);

                PlaceResponse placeResponse = new PlaceResponse();
                placeResponse.setType(p.getType());
                placeResponse.setName(p.getName());
                placeResponse.setAddress(p.getAddress());
                placeResponse.setRatings(p.getCurrentRatings());
                placeResponse.setPriceRange(p.getPriceRange());
                placeResponse.setPlaceId(p.getPlaceId());
                placeResponse.setImage(p.getImages());
                placeResponse.setPhoneNumber(p.getPhoneNumber());
                placeResponse.setDistance(Double.valueOf(df.format(distance)));
                placeResponse.setFavourite(jdbcTemplate.queryForObject("select count(*) from favourites  where user_id=? and place_id=?", Integer.class,userId,p.getPlaceId())==1);
                placeResponseList.add(placeResponse);

            }
        }

        for (int i = 0; i < placeResponseList.size() - 1; i++)
            for (int j = 0; j < placeResponseList.size() - i - 1; j++)
                if (placeResponseList.get(j).getDistance() > placeResponseList.get(j + 1).getDistance()) {
                    PlaceResponse temp = placeResponseList.get(j);
                    placeResponseList.set(j, placeResponseList.get(j + 1));
                    placeResponseList.set(j + 1, temp);
                }
        if(page==0){
            page=1;
        }
        if(page==1){
            if(limit>placeResponseList.size()){
                limit=placeResponseList.size();
            }

        }


        int startLimit = 0;
        int endLimit = limit;


        if (page==1)
            return placeResponseList.subList(startLimit,endLimit);
        else {
            startLimit =( (limit * page ) - limit ) ;
            if(startLimit>placeResponseList.size()){
                return new ArrayList<>();
            }
            endLimit=limit*page;
            return placeResponseList.subList(startLimit,endLimit);
        }
        //return placeResponseList;
    }

    @Override
    public List<PlaceResponse> lunch(double latitude, double longitude, int limit, int page, long userId) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        if(page<=0){
            page = Math.max(0,page-1);
        }
        final int r = 6371;
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select *from places where lunch=true", new BeanPropertyRowMapper<>(Places.class));
        List<PlaceResponse> placeResponseList = new ArrayList<>();
        if (!(places.isEmpty())) {

            for (Places p : places) {
                double lat1 = p.getLatitude();
                double long1 = p.getLongitude();
                long1 = Math.toRadians(long1);
                lat1 = Math.toRadians(lat1);
                double distance = 0;
                distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(1);

                PlaceResponse placeResponse = new PlaceResponse();
                placeResponse.setType(p.getType());
                placeResponse.setName(p.getName());
                placeResponse.setAddress(p.getAddress());
                placeResponse.setRatings(p.getCurrentRatings());
                placeResponse.setPriceRange(p.getPriceRange());
                placeResponse.setPlaceId(p.getPlaceId());
                placeResponse.setImage(p.getImages());
                placeResponse.setPhoneNumber(p.getPhoneNumber());
                placeResponse.setDistance(Double.valueOf(df.format(distance)));
                placeResponse.setFavourite(jdbcTemplate.queryForObject("select count(*) from favourites  where user_id=? and place_id=?", Integer.class,userId,p.getPlaceId())==1);

                placeResponseList.add(placeResponse);

            }
        }

        for (int i = 0; i < placeResponseList.size() - 1; i++)
            for (int j = 0; j < placeResponseList.size() - i - 1; j++)
                if (placeResponseList.get(j).getDistance() > placeResponseList.get(j + 1).getDistance()) {
                    PlaceResponse temp = placeResponseList.get(j);
                    placeResponseList.set(j, placeResponseList.get(j + 1));
                    placeResponseList.set(j + 1, temp);
                }
        if(page==0){
            page=1;
        }
        if(page==1){
            if(limit>placeResponseList.size()){
                limit=placeResponseList.size();
            }

        }


        int startLimit = 0;
        int endLimit = limit;


        if (page==1)
            return placeResponseList.subList(startLimit,endLimit);
        else {
            startLimit =( (limit * page ) - limit ) ;
            if(startLimit>placeResponseList.size()){
                return new ArrayList<>();
            }
            endLimit=limit*page;
            return placeResponseList.subList(startLimit,endLimit);
        }
        //return placeResponseList;
    }


    @Override
    public List<Ratings> viewRatings(long placeId) {
        return jdbcTemplate.query("select *from ratings where place_id=?", new BeanPropertyRowMapper<>(Ratings.class), placeId);
    }


    @Override
    public UserResponse editProfile(String profilePic, long userId) throws IOException {
        List<Users> usersList = jdbcTemplate.query("select *from users where user_id=?", new BeanPropertyRowMapper<>(Users.class), userId);
        if (!(userId == usersList.get(0).getUserId())) {
            throw new IOException("Access Denied");
        }
        jdbcTemplate.update("update users set profile_pic=? where user_id=?", profilePic, userId);

        return new UserResponse("profile pic updated successfully");
    }

    @Override
    public String giveFeedback(Users users, String feedback) {
        List<Feedback> feedbacks = jdbcTemplate.query("select * from feedback where user_id=?", new BeanPropertyRowMapper<>(Feedback.class), users.getUserId());
        if (feedbacks.isEmpty()) {
            jdbcTemplate.update("insert into feedback(user_id,feedback) values(?,?) ", users.getUserId(), feedback);
        } else {
            jdbcTemplate.update("update feedback set feedback = ? where user_id=?", feedback, users.getUserId());
        }
        return "feedback added successfully";
    }


    @Override
    public List<Feedback> viewFeedback() {
        return jdbcTemplate.query("select *from feedback", new BeanPropertyRowMapper<>(Feedback.class));
    }

    @Override
    public List<Feedback> feedback(Users users, long userId) {
        return jdbcTemplate.query("select *from feedback where user_id=?", new BeanPropertyRowMapper<>(Feedback.class), userId);
    }

    @Override
    public List<ImageResponse> images(long placeId) {


        List<ImageResponse> reviewPhotos = jdbcTemplate.query("select review_photo_id , review_pics, profile_pic ,name, review_photo_date from review_photos inner join users on users.user_id = review_photos.user_id where place_id= ?", new BeanPropertyRowMapper<>(ImageResponse.class), placeId);
        return reviewPhotos;
    }

    @Override
    public List<PlaceOverviewResponse> placeDetails(long placeId, double latitude, double longitude) {
        List<PlaceOverviewResponse> responses = jdbcTemplate.query("select * from places where place_id=?", new BeanPropertyRowMapper<>(PlaceOverviewResponse.class), placeId);
        final int r = 6371;
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        double long1 = Math.toRadians(responses.get(0).getLongitude());
        double lat1 = Math.toRadians(responses.get(0).getLatitude());
        double distance = 0;
        distance = r * Math.acos(Math.sin(lat1) * Math.sin(latitude) + Math.cos(lat1) * Math.cos(latitude) * Math.cos(longitude - long1));
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(1);
        responses.get(0).setDistance(Double.valueOf(df.format(distance)));

        return responses;
    }

    @Override
    public List<PlaceResponse> search(SearchRequest searchRequest) throws CustomException {
        if (systemInterface.verifyLatLong(searchRequest.getLatitude(), searchRequest.getLongitude()))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        final int r = 6371;
        if (searchRequest.getOption() == null || searchRequest.getOption().isBlank()) {
            searchRequest.setOption("");
        }
        String query = "select * from places join features on places.place_id=features.place_id where (name like'%" + searchRequest.getOption() + "%' or city like'%" + searchRequest.getOption()+"%' or type like '%"+ searchRequest.getOption()+"%')";
        if (searchRequest != null) {
            if (!(searchRequest.getPriceRange() == null))
                query = query + " and price_range=" + searchRequest.getPriceRange();
            if (searchRequest.getFeatureRequestList() != null) {
                query = searchRequest.getFeatureRequestList().isAcceptsCreditCards() ? query + " and accepts_credit_cards=1" : query;
                query = searchRequest.getFeatureRequestList().isDelivery() ? query + " and delivery=1" : query;
                query = searchRequest.getFeatureRequestList().isParking() ? query + " and parking=1" : query;
                query = searchRequest.getFeatureRequestList().isDogFriendly() ? query + " and dog_friendly=1" : query;
                query = searchRequest.getFeatureRequestList().isWiFi() ? query + " and wi_fi=1" : query;
                query = searchRequest.getFeatureRequestList().isFamilyFriendlyPlaces() ? query + " and family_friendly_places=1" : query;
                query = searchRequest.getFeatureRequestList().isOutdoorSeating() ? query + " and outdoor_seating=1" : query;
                query = searchRequest.getFeatureRequestList().isInWalkingDistance() ? query + " and in_walking_distance=1" : query;


            }
            if (searchRequest.getSortBy() != null) {
                if (searchRequest.getSortBy().equalsIgnoreCase("ratings")) {
                    query = query + " order by current_ratings desc";
                }

            }
        }
        List<Places> places = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Places.class));


        if (searchRequest.getSortBy() != null) {
            if (searchRequest.getSortBy().equalsIgnoreCase("distance")) {
                List<PlaceResponse> placeList = new ArrayList<>();
                searchRequest.setLongitude(Math.toRadians(searchRequest.getLongitude()));
                searchRequest.setLatitude(Math.toRadians(searchRequest.getLatitude()));
                for (Places p : places) {
                    double lat1 = p.getLatitude();
                    double long1 = p.getLongitude();
                    long1 = Math.toRadians(long1);
                    lat1 = Math.toRadians(lat1);
                    double distance = 0;
                    distance = r * Math.acos(Math.sin(lat1) * Math.sin(searchRequest.getLatitude()) + Math.cos(lat1) * Math.cos(searchRequest.getLatitude()) * Math.cos(searchRequest.getLongitude() - long1));


                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(1);

                    PlaceResponse placeResponse = new PlaceResponse();
                    placeResponse.setType(p.getType());
                    placeResponse.setName(p.getName());
                    placeResponse.setAddress(p.getAddress());
                    placeResponse.setRatings(p.getCurrentRatings());
                    placeResponse.setPriceRange(p.getPriceRange());
                    placeResponse.setPlaceId(p.getPlaceId());
                    placeResponse.setImage(p.getImages());
                    placeResponse.setPhoneNumber(p.getPhoneNumber());
                    placeResponse.setDistance(Double.valueOf(df.format(distance)));
                    if (searchRequest.getRadius() == null) {
                        placeList.add(placeResponse);
                    } else {
                        if (placeResponse.getDistance() < searchRequest.getRadius()) {
                            placeList.add(placeResponse);
                        }
                    }


                }
                if (searchRequest.getSortBy() != null) {
                    if (searchRequest.getSortBy().equalsIgnoreCase("distance")) {
                        for (int i = 0; i < placeList.size() - 1; i++)
                            for (int j = 0; j < placeList.size() - i - 1; j++)
                                if (placeList.get(j).getDistance() > placeList.get(j + 1).getDistance()) {
                                    // swap arr[j+1] and arr[j]
                                    PlaceResponse temp = placeList.get(j);
                                    placeList.set(j, placeList.get(j + 1));
                                    placeList.set(j + 1, temp);
                                }
                        return placeList;
                    }
                }
            } else if (searchRequest.getSortBy().equalsIgnoreCase("popular")) {

                searchRequest.setLongitude(Math.toRadians(searchRequest.getLongitude()));
                searchRequest.setLatitude(Math.toRadians(searchRequest.getLatitude()));
                List<PlaceResponse> placeResponseList = new ArrayList<>();
                Map<Long, Integer> pid = new HashMap<>();
                //     List<Places> places = jdbcTemplate.query("select * from places", new BeanPropertyRowMapper<>(Places.class));
                if (!(places.isEmpty())) {
                    for (Places p : places) {
                        Integer count = jdbcTemplate.queryForObject("select count(*) from review where place_id = ?", Integer.class, p.getPlaceId());
                        pid.put(p.getPlaceId(), count);
                    }
                }
                List<Map.Entry<Long, Integer>> nList = new ArrayList<>(pid.entrySet());
                nList.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));//to save map in desc we use list
                for (Map.Entry<Long, Integer> map : nList) {
                    List<Places> p = jdbcTemplate.query("select * from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), map.getKey());
                    double lat1 = p.get(0).getLatitude();
                    double long1 = p.get(0).getLongitude();
                    long1 = Math.toRadians(long1);
                    lat1 = Math.toRadians(lat1);
                    double distance = 0;
                    distance = r * Math.acos(Math.sin(lat1) * Math.sin(searchRequest.getLatitude()) + Math.cos(lat1) * Math.cos(searchRequest.getLatitude()) * Math.cos(searchRequest.getLongitude() - long1));


                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(1);

                    PlaceResponse placeResponse = new PlaceResponse();
                    placeResponse.setType(p.get(0).getType());
                    placeResponse.setName(p.get(0).getName());
                    placeResponse.setAddress(p.get(0).getAddress());
                    placeResponse.setRatings(p.get(0).getCurrentRatings());
                    placeResponse.setPriceRange(p.get(0).getPriceRange());
                    placeResponse.setPlaceId(p.get(0).getPlaceId());
                    placeResponse.setImage(p.get(0).getImages());
                    placeResponse.setPhoneNumber(p.get(0).getPhoneNumber());
                    placeResponse.setDistance(Double.valueOf(df.format(distance)));
                    if (searchRequest.getRadius() == null) {
                        placeResponseList.add(placeResponse);
                    } else {
                        if (placeResponse.getDistance() < searchRequest.getRadius()) {
                            placeResponseList.add(placeResponse);
                        }
                    }


                }
                return placeResponseList;
            }

        }
        List<PlaceResponse> placeList = new ArrayList<>();
        searchRequest.setLongitude(Math.toRadians(searchRequest.getLongitude()));
        searchRequest.setLatitude(Math.toRadians(searchRequest.getLatitude()));
        for (Places p : places) {
            double lat1 = p.getLatitude();
            double long1 = p.getLongitude();
            long1 = Math.toRadians(long1);
            lat1 = Math.toRadians(lat1);
            double distance = 0;
            distance = r * Math.acos(Math.sin(lat1) * Math.sin(searchRequest.getLatitude()) + Math.cos(lat1) * Math.cos(searchRequest.getLatitude()) * Math.cos(searchRequest.getLongitude() - long1));


            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);
            PlaceResponse placeResponse = new PlaceResponse();
            placeResponse.setPhoneNumber(p.getPhoneNumber());
            placeResponse.setType(p.getType());
            placeResponse.setName(p.getName());
            placeResponse.setAddress(p.getAddress());
            placeResponse.setRatings(p.getCurrentRatings());
            placeResponse.setPriceRange(p.getPriceRange());
            placeResponse.setPlaceId(p.getPlaceId());
            placeResponse.setImage(p.getImages());
            placeResponse.setPhoneNumber(p.getPhoneNumber());
            placeResponse.setDistance(Double.valueOf(df.format(distance)));
            if (searchRequest.getRadius() == null) {
                placeList.add(placeResponse);
            } else {
                if (placeResponse.getDistance() < searchRequest.getRadius()) {
                    placeList.add(placeResponse);
                }
            }
        }
        return placeList;
    }

    @Override
    public String viewAboutUs() {
        return jdbcTemplate.queryForObject("select about_us from about_us", String.class);
    }

    @Override
    public ProfileResponse viewProfile(long userId) {
        List<Users> users =
                jdbcTemplate.query("select * from users where user_id=?", new BeanPropertyRowMapper<>(Users.class), userId);
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setName(users.get(0).getName());
        profileResponse.setProfilePic(users.get(0).getProfilePic());
        return profileResponse;
    }

    @Override
    public List<PlaceResponse> filterFavourite(long userId, String option, SearchRequest searchRequest, double latitude, double longitude) throws CustomException {
        if (systemInterface.verifyLatLong(latitude, longitude))
            throw new CustomException("Latitude or Longitude cannot be negative or zero");
        List<Favourites> favourites = jdbcTemplate.query("select * from favourites where user_id= ?", new BeanPropertyRowMapper<>(Favourites.class), userId);
        List<PlaceResponse> placeResponse = new ArrayList<>();
        List<PlaceResponse> placeResponseList = search(searchRequest);
        for (Favourites f : favourites) {
            for (PlaceResponse p : placeResponseList) {
                if (p.getPlaceId() == f.getPlaceId()) {
                    placeResponse.add(p);
                }
            }
        }
        return placeResponse;
    }

    @Override
    public List<NearPlaceResponse> nearByPlace(double longitude, double latitude, long userId) throws CustomException {
        List<PlaceResponse> placeResponseList = nearMe(latitude, longitude,Integer.MAX_VALUE,0,userId );
        List<NearPlaceResponse> nearPlaceResponseList = new ArrayList<>();
        int j = 1;
        for (int i = 0; i < placeResponseList.size(); i++) {
            NearPlaceResponse nearPlaceResponse = new NearPlaceResponse();
            nearPlaceResponse.setImage(placeResponseList.get(i).getImage());
            String city = jdbcTemplate.queryForObject("select city from places where place_id=?", String.class, placeResponseList.get(i).getPlaceId());
            if (i == 0) {
                nearPlaceResponse.setCity(city);
                nearPlaceResponseList.add(nearPlaceResponse);
            } else {
                if (!(nearPlaceResponseList.get(i - j).getCity().equalsIgnoreCase(city))) {
                    nearPlaceResponse.setCity(city);
                    nearPlaceResponseList.add(nearPlaceResponse);
                }
                j++;
                if (nearPlaceResponseList.size() == 2) {
                    return nearPlaceResponseList;
                }
            }
        }
        return nearPlaceResponseList;
    }

}
