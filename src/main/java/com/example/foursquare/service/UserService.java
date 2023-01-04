package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.*;
import com.example.foursquare.responseModel.PlaceResponse;
import com.example.foursquare.responseModel.ReviewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService implements IUserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String addFavourite(Users users, long placeId) {

        List<Places> places = jdbcTemplate.query("select *from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), placeId);
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
    public List<PlaceResponse> viewFavourite(Users users, double latitude, double longitude) {
        final int r = 6371;
        List<PlaceResponse> placeResponseList = new ArrayList<>();
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select * from places where place_id in (select place_id from favourites where user_id=?)", new BeanPropertyRowMapper<>(Places.class), users.getUserId());
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
    public String addReview(Users users, String review, long placeId, List<String> url) throws CustomException {
        List<Places> places = jdbcTemplate.query("select *from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), placeId);
        if (places.isEmpty()) {
            throw new CustomException("places not found");
        }
        List<Review> reviews = jdbcTemplate.query("select *from review where user_id=? and place_id=?", new BeanPropertyRowMapper<>(Review.class), users.getUserId(), placeId);
        if (reviews.isEmpty()) {
            jdbcTemplate.update("insert into review (user_id,review,place_id,review_date) values(?,?,?,?)", users.getUserId(), review, placeId, LocalDate.now());
            Integer reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            for (String s : url) {
                if (!(s == null || s.isBlank())) {
                    jdbcTemplate.update("insert into review_photos values(?,?)", reviewId, s);
                }
            }

        } else {
            jdbcTemplate.update("update review set review=? where user_id=? and place_id=?", review, users.getUserId(), placeId);
            for (String s : url) {
                if (!(s == null || s.isBlank())) {
                    jdbcTemplate.update("insert into review_photos values(?,?)", reviews.get(0).getReviewId(), s);
                }
            }

        }
        return "Review added Successfully";
    }

    @Override
    public List<ReviewResponse> viewReview(long placeId) {
        return jdbcTemplate.query("select profile_pic ,name,review,review_date from users inner join  review on users.user_id=review.user_id where place_id=?", new BeanPropertyRowMapper<>(ReviewResponse.class), placeId);
    }

    @Override
    public List<PlaceResponse> nearMe(double latitude, double longitude) {
        final int r = 6371;
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

        for (int i = 0; i < placeResponseList.size() - 1; i++)
            for (int j = 0; j < placeResponseList.size() - i - 1; j++)
                if (placeResponseList.get(j).getDistance() > placeResponseList.get(j + 1).getDistance()) {
                    PlaceResponse temp = placeResponseList.get(j);
                    placeResponseList.set(j, placeResponseList.get(j + 1));
                    placeResponseList.set(j + 1, temp);
                }
        return placeResponseList;
    }

    @Override
    public List<PlaceResponse> topPick(double latitude, double longitude) {
        final int r = 6371;
        longitude = Math.toRadians(longitude);
        latitude = Math.toRadians(latitude);
        List<Places> places = jdbcTemplate.query("select * from places order by current_ratings desc ", new BeanPropertyRowMapper<>(Places.class));
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

                placeResponseList.add(placeResponse);

            }
        }
        return placeResponseList;
    }

    @Override
    public List<PlaceResponse> popular(double latitude, double longitude) {

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

            placeResponseList.add(placeResponse);
        }
        return placeResponseList;
    }

}
