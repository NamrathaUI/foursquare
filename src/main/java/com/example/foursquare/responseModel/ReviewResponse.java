package com.example.foursquare.responseModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String profilePic;
    private String name;
    private String review;
    private LocalDate reviewDate;
}
