package com.example.foursquare.responseModel;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ImageResponse {
    private long review_photo_id;
    private String reviewPics;
    private String profilePic;
    private String name;
    private LocalDate review_photo_date;
}
