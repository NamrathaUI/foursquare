package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Places;
import com.example.foursquare.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminService implements IAdminService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String addPlaces(Users users, Places places, MultipartFile file) throws CustomException {

        if (!(users.getRole().equalsIgnoreCase("admin"))) {
            throw new CustomException("Access Denied");
        }

        jdbcTemplate.update("insert into places(name,address,type,price_range,phone_number,images,longitude,latitude,current_ratings,about_us)values (?,?,?,?,?,?,?,?,?,?) ", places.getName(), places.getAddress(), places.getType(), places.getPriceRange(), places.getPhoneNumber(), places.getImages(), places.getLongitude(), places.getLatitude(), places.getCurrentRatings(), places.getAboutUs());

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

