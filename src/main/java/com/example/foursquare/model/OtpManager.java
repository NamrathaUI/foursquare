package com.example.foursquare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpManager {
   private long userId;
    private int otp;
    private LocalTime expireAt;
    private String email;
}
