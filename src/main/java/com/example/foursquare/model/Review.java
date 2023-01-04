package com.example.foursquare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private long reviewId;
    private long userId;
    private String review;
    private long placeId;
    private LocalDate reviewDate;
}
