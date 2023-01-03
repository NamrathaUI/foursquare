package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Favourites;
import com.example.foursquare.model.Places;
import com.example.foursquare.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
    public List<Places> viewFavouritePlaces(Users users) {
        return jdbcTemplate.query("select * from places where place_id in (select place_id from favourites where user_id=?)", new BeanPropertyRowMapper<>(Places.class), users.getUserId());

    }
    @Override
    public String ratePlace(Users users, long placeId, float numberOfStarsRated) throws CustomException {


        List<Places> places = jdbcTemplate.query("select *from places where place_id=? ", new BeanPropertyRowMapper<>(Places.class), placeId);
        if (!(places.isEmpty())) {
            if (places.get(0).getCurrentRatings() == 0) {
                jdbcTemplate.update("insert into ratings values(?,?,?)", users.getUserId(), placeId, numberOfStarsRated);
            } else {
                float newRatings = (places.get(0).getCurrentRatings() + numberOfStarsRated) / 2;
                jdbcTemplate.update("update places set current_ratings =? where place_id=?", newRatings, placeId);

            }
            return "Thank you for rating";
        }
        throw new CustomException("Places not found");

    }
}
