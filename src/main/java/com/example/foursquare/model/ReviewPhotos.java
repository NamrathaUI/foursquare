package com.example.foursquare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPhotos {
    private long reviewPhotoId;
    private String reviewPics;
    private long placeId;
    private long userId;
    private LocalDate reviewPhotoDate;
}
