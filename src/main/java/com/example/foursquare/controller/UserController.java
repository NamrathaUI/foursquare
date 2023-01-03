package com.example.foursquare.controller;

import com.example.foursquare.MyUserDetails;
import com.example.foursquare.model.Places;
import com.example.foursquare.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService iUserService;

    @PostMapping("/favourites")
    ResponseEntity<String> addFavRestaurant(@RequestParam long placeId) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.addFavourite(userDetails.getUsers(), placeId));
    }
    @GetMapping("favourites")
    public ResponseEntity<List<Places>> viewFavouritePlaces(){
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.viewFavouritePlaces(userDetails.getUsers()));

    }
}
