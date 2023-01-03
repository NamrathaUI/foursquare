package com.example.foursquare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ratings {
    private long ratingId;
    private long userId;
    private float numberOfStarsRated;
    private String review;
    private Date reviewDate;

}
