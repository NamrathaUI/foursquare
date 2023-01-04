package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.*;
import com.example.foursquare.responseModel.PlaceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        List<Places>places=jdbcTemplate.query("select * from places where place_id in (select place_id from favourites where user_id=?)", new BeanPropertyRowMapper<>(Places.class), users.getUserId());
         if(!(places.isEmpty())){
             for (Places p:places){

                 double lat1=p.getLatitude();
                 double long1=p.getLongitude();
                 long1 = Math.toRadians(long1);
                 lat1 = Math.toRadians(lat1);
                 double distance=0;
                  distance = r * Math.acos(Math.sin(lat1)*Math.sin(latitude) + Math.cos(lat1)*Math.cos(latitude)*Math.cos(longitude - long1));


                 DecimalFormat df = new DecimalFormat();
                 df.setMaximumFractionDigits(1);

                 PlaceResponse placeResponse = new PlaceResponse();
                 placeResponse.setType(p.getType());
                 placeResponse.setName(p.getName());
                 placeResponse.setAddress(p.getAddress());
                 placeResponse.setRatings(p.getCurrentRatings());
                 placeResponse.setPriceRange(p.getPriceRange());
                 placeResponse.setDistance(Double.valueOf(df.format(distance)));

                 placeResponseList.add(placeResponse);

             }

         }
         return placeResponseList;
    }
    @Override
    public String giveRatings(Users users, long placeId, float numberOfStarsRated) throws CustomException {


        List<Places> places = jdbcTemplate.query("select *from places where place_id=? ", new BeanPropertyRowMapper<>(Places.class), placeId);
        if (!(places.isEmpty())) {
            if (places.get(0).getCurrentRatings() == 0) {
                jdbcTemplate.update("update places set current_ratings=? where place_id=? ",numberOfStarsRated,placeId);
                jdbcTemplate.update("insert into ratings(user_id,place_id,number_of_stars_rated) values(?,?,?)", users.getUserId(), placeId, numberOfStarsRated);
            } else {
                jdbcTemplate.update("update ratings set number_of_stars_rated=? where place_id=? and user_id=?",numberOfStarsRated,placeId,users.getUserId());
                List<Ratings> ratings = jdbcTemplate.query("select *from ratings where place_id=?",new BeanPropertyRowMapper<>(Ratings.class),placeId);
                float rating =0;
                for (Ratings r : ratings){
                    rating = rating+r.getNumberOfStarsRated();
                }

                float newRatings = rating / ratings.size();
                jdbcTemplate.update("update places set current_ratings =? where place_id=? ", newRatings, placeId);
            }
            return "Thank you for rating";
        }
        throw new CustomException("Places not found");
    }

    @Override
    public String addReview(Users users, String review, long placeId, String url) throws CustomException {
        List<Places> places = jdbcTemplate.query("select *from places where place_id=?", new BeanPropertyRowMapper<>(Places.class), placeId);
        if (places.isEmpty()) {
          throw new CustomException("places not found");
        }
        List<Review> reviews=jdbcTemplate.query("select *from review where user_id=? and place_id=?",new BeanPropertyRowMapper<>(Review.class),users.getUserId(),placeId);
        if (reviews.isEmpty()){
            jdbcTemplate.update("insert into review (user_id,review,place_id,review_date) values(?,?,?,?)",users.getUserId(),review,placeId, LocalDate.now());
            Integer reviewId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            if(!(url.isBlank())){
                jdbcTemplate.update("insert into review_photos values(?,?)",reviewId,url);
            }
        }else {
            jdbcTemplate.update("update review set review=? where user_id=? and place_id=?",review,users.getUserId(),placeId);
            if(!(url.isBlank())){
                jdbcTemplate.update("insert into review_photos values(?,?)",reviews.get(0).getReviewId(),url);
            }
        }
        return "Review added Successfully";
    }

}
