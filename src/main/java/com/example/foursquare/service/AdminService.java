package com.example.foursquare.service;

import com.example.foursquare.model.Places;
import com.example.foursquare.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService implements IAdminService{
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public String addPlaces(Users users, Places places, MultipartFile file) {


            jdbcTemplate.update("insert into places(name,address,type,price_range,phone_number,images,longitude,latitude,current_ratings,about_us)values (?,?,?,?,?,?,?,?,?,?) ",places.getName(),places.getAddress(),places.getType(),places.getPriceRange(),places.getPhoneNumber(),places.getImages(),places.getLongitude(),places.getLatitude(),places.getCurrentRatings(),places.getAboutUs() );

        return "places added successfully";
    }
}

