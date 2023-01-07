package com.example.foursquare.controller;

import com.cloudinary.utils.ObjectUtils;
import com.example.foursquare.MyUserDetails;
import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Places;
import com.example.foursquare.service.IAdminService;
import com.example.foursquare.service.ImageUploadInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    IAdminService iAdminService;

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    ImageUploadInterface uploadToCloudInterface;

    @PostMapping("/places")
    ResponseEntity<?> addPlaces(@ModelAttribute Places places, @RequestParam @Nullable MultipartFile file) throws IOException {

        String url = "";
        Map result = null;
        if (file == null) {
          url=null;
        } else if (file.isEmpty())
            url = null;
        else
            result = uploadToCloudInterface.uploadImage(file.getBytes(), ObjectUtils.asMap("resource type", "auto"));
        if (result != null)
            url = (result.get("secure_url").toString());

        try {
            MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return ResponseEntity.of(Optional.of(iAdminService.addPlaces(userDetails.getUsers(),places,url)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("places already added");
        }
    }

    @DeleteMapping("/places")
    public ResponseEntity<?> deleteSubjectById(@RequestParam long placeId) throws IOException {
        return ResponseEntity.ok(iAdminService.deletePlaces(placeId));
    }
    @PatchMapping("/places")
    ResponseEntity<?> updatePlaces(@RequestParam long placeId, @RequestParam String name,@RequestParam Integer priceRange, @RequestParam String type,@RequestParam String aboutUs) throws CustomException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.of(Optional.of(iAdminService.updatePlaces(userDetails.getUsers(),placeId,name,priceRange,type, aboutUs)));
    }

}

