package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.model.Users;
import com.example.foursquare.responseModel.UserResponse;

public interface IAuthService {
    UserResponse signUp(Users users) throws CustomException;

    UserResponse forgotPassword(String email) throws CustomException;

    UserResponse verifyOtp(int otp, String email) throws CustomException;

    UserResponse updatePassword(String email, String newPassword) throws CustomException;

    String generateOtp();
    String logout(String token);

}
