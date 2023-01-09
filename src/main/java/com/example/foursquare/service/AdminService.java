package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Users;
import com.example.foursquare.requestModel.PlaceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService implements IAdminService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String addPlaces(Users users, PlaceRequest placeRequest, String url) throws CustomException {

        if (!(users.getRole().equalsIgnoreCase("admin"))) {
            throw new CustomException("Access Denied");
        }

        jdbcTemplate.update("insert into places(name,address,type,price_range,phone_number,images,longitude,latitude,current_ratings,about_us,city,cafe,lunch)values (?,?,?,?,?,?,?,?,?,?,?,?,?) ", placeRequest.getName(), placeRequest.getAddress(), placeRequest.getType(), placeRequest.getPriceRange(), placeRequest.getPhoneNumber(), url, placeRequest.getLongitude(), placeRequest.getLatitude(), placeRequest.getCurrentRatings(), placeRequest.getAboutUs(),placeRequest.getCity(),placeRequest.isCafe(),placeRequest.isLunch());
        long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        jdbcTemplate.update("insert into  features values(?,?,?,?,?,?,?,?,?) ",id,placeRequest.isAcceptsCreditCards(),placeRequest.isDelivery(),placeRequest.isParking(),
        placeRequest.isDogFriendly(),placeRequest.isWiFi(),placeRequest.isFamilyFriendlyPlaces(),placeRequest.isOutdoorSeating(),placeRequest.isInWalkingDistance());
        return "places added successfully";
    }

    @Override
    public int deletePlaces(long placeId) {
        return jdbcTemplate.update("delete from places where place_id=?", placeId);
    }

    @Override
    public String updatePlaces(Users users,long placeId, String name,Integer priceRange, String type,String aboutUs) throws CustomException {
        if (!(users.getRole().equalsIgnoreCase("admin"))) {
            throw new CustomException("Access Denied");
        }
        if (!(name.isBlank() || name == null)) {
            jdbcTemplate.update("update places set name=? where place_id=?", name, placeId);

        }
        if (!(type.isBlank() || type == null)) {
            jdbcTemplate.update("update places set type=? where place_id=? ", type, placeId);
        }
        if (!(priceRange == null)) {
            jdbcTemplate.update("update places set price_range=? where place_id=?", priceRange, placeId);
        }
        if (!(aboutUs.isBlank() || aboutUs == null)) {
            jdbcTemplate.update("update places set about_us=? where place_id=?", aboutUs, placeId);
        }
        return "place updated successfully";
    }
}

