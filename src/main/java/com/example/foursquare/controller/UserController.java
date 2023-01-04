package com.example.foursquare.controller;

import com.cloudinary.utils.ObjectUtils;
import com.example.foursquare.MyUserDetails;
import com.example.foursquare.exception.CustomException;
import com.example.foursquare.responseModel.PlaceResponse;
import com.example.foursquare.service.IUserService;
import com.example.foursquare.service.ImageUploadInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService iUserService;

    @Autowired
    ImageUploadInterface uploadToCloudInterface;

    @PostMapping("/favourites")
    ResponseEntity<String> addFavRestaurant(@RequestParam long placeId) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.addFavourite(userDetails.getUsers(), placeId));
    }
    @GetMapping("favourites")
    public ResponseEntity<List<PlaceResponse>> viewFavouritePlaces(@Param("latitude") double latitude , @Param("longitude") double longitude){
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.viewFavourite(userDetails.getUsers(),latitude,longitude));

    }
    @PutMapping("/rating")
    ResponseEntity<String> rating(@RequestParam long placeId, @RequestParam float rating) throws CustomException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.giveRatings(userDetails.getUsers(), placeId, rating));
    }
    @PostMapping("/review")
    ResponseEntity<?> review(@RequestParam Long placeId,@RequestParam String review , @RequestPart @Nullable MultipartFile file) throws IOException {

        String url="";
        Map result = null;
        if (file == null)
            url=null;
        else if (file.isEmpty())
            url=null;
        else
            result = uploadToCloudInterface.uploadImage(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        if (result != null)
            url = result.get("secure_url").toString();
        try {
            MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return ResponseEntity.of(Optional.of(iUserService.addReview(userDetails.getUsers(),review,placeId,url)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

}
