package com.example.foursquare.service;

import com.example.foursquare.exception.InvalidUserCredentialException;
import com.example.foursquare.model.Users;
import com.example.foursquare.responseModel.UserResponse;

public interface IAuthService {
    UserResponse signUp(Users users) throws InvalidUserCredentialException;
    UserResponse forgotPassword(String email) throws  InvalidUserCredentialException;

    UserResponse verifyOtp(int otp, String email) throws InvalidUserCredentialException;

    UserResponse updatePassword(String email, String newPassword) throws InvalidUserCredentialException;

    String generateOtp();

}
