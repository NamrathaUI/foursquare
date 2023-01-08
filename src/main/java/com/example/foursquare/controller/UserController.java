package com.example.foursquare.controller;

import com.cloudinary.utils.ObjectUtils;
import com.example.foursquare.MyUserDetails;
import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Feedback;
import com.example.foursquare.model.Users;
import com.example.foursquare.requestModel.SearchRequest;
import com.example.foursquare.responseModel.PlaceResponse;
import com.example.foursquare.service.IUserService;
import com.example.foursquare.service.ImageUploadInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService iUserService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ImageUploadInterface uploadToCloudInterface;

    @PostMapping("/favourites")
    ResponseEntity<String> addFavRestaurant(@RequestParam long placeId) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.addFavourite(userDetails.getUsers(), placeId));
    }

    @GetMapping("favourites")
    public ResponseEntity<List<PlaceResponse>> viewFavouritePlaces(@Param("latitude") double latitude, @Param("longitude") double longitude) throws CustomException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.viewFavourite(userDetails.getUsers(), latitude, longitude));

    }

    @PutMapping("/rating")
    ResponseEntity<String> rating(@RequestParam long placeId, @RequestParam float rating) throws CustomException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.giveRatings(userDetails.getUsers(), placeId, rating));
    }

    @GetMapping("/view/ratings")
    ResponseEntity<?> getRatings(@RequestParam long placeId) {
        return ResponseEntity.ok(iUserService.viewRatings(placeId));
    }


    @PostMapping("/feedback")
    ResponseEntity<String> feedback(@RequestParam String feedback) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.giveFeedback(userDetails.getUsers(), feedback));
    }

    @GetMapping("/view/feedbacks")
    List<Feedback> viewFeedBack() {
        return iUserService.viewFeedback();
    }

    @GetMapping("/feedback")
    public ResponseEntity<List<Feedback>> feedback() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.feedback(userDetails.getUsers(),userDetails.getUsers().getUserId()));
    }

    @PostMapping("/review")
    ResponseEntity<?> review(@RequestParam Long placeId, @RequestParam  @Nullable String review, @RequestPart @Nullable List<MultipartFile> file) throws IOException {

        List<String> url = new ArrayList<>();
        if (!(file == null)) {
            for (MultipartFile m : file) {
                Map result = null;
                if (file == null)
                    url = null;
                else if (file.isEmpty())
                    url = null;
                else
                    result = uploadToCloudInterface.uploadImage(m.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                if (result != null)
                    url.add(result.get("secure_url").toString());
            }
        }
        try {
            MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return ResponseEntity.of(Optional.of(iUserService.addReview(userDetails.getUsers(), review, placeId, url)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @GetMapping("/view/review")
    ResponseEntity<?> getReview(@RequestParam long placeId) {
        return ResponseEntity.ok(iUserService.viewReview(placeId));
    }

    @GetMapping("/view/nearMe")
    ResponseEntity<?> nearMe(@Param("latitude") Double latitude, @Param("longitude") Double longitude) throws CustomException {
        return ResponseEntity.ok(iUserService.nearMe(latitude, longitude));
    }

    @GetMapping("/view/topPick")
    ResponseEntity<?> topPick(@Param("latitude") Double latitude, @Param("longitude") Double longitude) throws CustomException {
        return ResponseEntity.ok(iUserService.topPick(latitude, longitude));
    }

    @GetMapping("/view/popular")
    ResponseEntity<?> popular(@Param("latitude") Double latitude, @Param("longitude") Double longitude) throws CustomException {
        return ResponseEntity.ok(iUserService.popular(latitude, longitude));
    }

    @GetMapping("/view/cafe")
    ResponseEntity<?> cafe(@Param("latitude") Double latitude, @Param("longitude") Double longitude) throws CustomException {
        return ResponseEntity.ok(iUserService.cafe(latitude, longitude));
    }

    @GetMapping("/view/lunch")
    ResponseEntity<?> lunch(@Param("latitude") Double latitude, @Param("longitude") Double longitude) throws CustomException {
        return ResponseEntity.ok(iUserService.lunch(latitude, longitude));
    }

    @PatchMapping("/profile")
    ResponseEntity<?> updateProfile(@RequestHeader String authorization, @ModelAttribute MultipartFile file) throws IOException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Users> usersList = jdbcTemplate.query("select * from users where user_id =?", new BeanPropertyRowMapper<>(Users.class), userDetails.getUsers().getUserId());
        String profilePic = "";
        Map result = null;
        if (file == null) {
            profilePic = usersList.get(0).getProfilePic();

        } else if (file.isEmpty())
            profilePic = usersList.get(0).getProfilePic();
        else
            result = uploadToCloudInterface.uploadImage(file.getBytes(), ObjectUtils.asMap("resource type", "auto"));
        if (result != null)
            profilePic = (result.get("secure_url").toString());

        return ResponseEntity.of(Optional.of(iUserService.editProfile(profilePic, usersList.get(0).getUserId())));
    }

    @GetMapping("/view/reviewPhotos")
    ResponseEntity<?> getReviewPhotos(@RequestParam long placeId) {
        return ResponseEntity.ok(iUserService.images(placeId));
    }

    @GetMapping("/view/details")
    ResponseEntity<?> getDetails(@RequestParam long placeId) {
        return ResponseEntity.ok(iUserService.placeDetails(placeId));
    }
    @PostMapping("/view/search")
    public ResponseEntity<?>  getSearch(@RequestBody  SearchRequest searchRequest ) throws CustomException {
        return ResponseEntity.ok(iUserService.search(searchRequest));
    }

    @GetMapping("/view/aboutUs")
    ResponseEntity<?> getAboutUs() {
        return ResponseEntity.ok(iUserService.viewAboutUs());
    }
    @GetMapping("/profile")
    ResponseEntity<?> getProfile() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.viewProfile(userDetails.getUsers().getUserId()));
    }

    @GetMapping("/favourite/filter")
    ResponseEntity<?> favouriteFilter(@Param("option") String option, @Param("searchRequest") SearchRequest searchRequest, @Param("latitude") Double latitude, @Param("longitude") Double longitude) throws CustomException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(iUserService.filterFavourite(userDetails.getUsers().getUserId(),option,searchRequest,latitude,longitude));
    }
    @GetMapping("/view/nearByPlace")
    ResponseEntity<?> nearByPlace(@RequestParam double longitude,@RequestParam double latitude) throws CustomException {
      return ResponseEntity.ok(iUserService.nearByPlace(longitude,latitude))  ;
    }
}

