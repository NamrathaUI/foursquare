package com.example.foursquare.service;

import com.example.foursquare.model.Places;
import com.example.foursquare.model.Users;
import org.springframework.web.multipart.MultipartFile;

public interface IAdminService {
    String addPlaces(Users users, Places places, MultipartFile file);
}
