package com.example.foursquare.controller;

import com.example.foursquare.exception.InvalidUserCredentialException;
import com.example.foursquare.jwtutils.JwtUserDetailsService;
import com.example.foursquare.jwtutils.TokenManager;
import com.example.foursquare.model.Users;
import com.example.foursquare.responseModel.JwtResponseModel;
import com.example.foursquare.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenManager tokenManager;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    IAuthService iAuthService;

    @PostMapping("/register")
    ResponseEntity<?> register(@Valid @RequestBody Users users) throws InvalidUserCredentialException {
        return ResponseEntity.ok(iAuthService.signUp(users));
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestHeader  String email,@RequestHeader String password) throws Exception {
        authenticationManager.authenticate(
                new
                        UsernamePasswordAuthenticationToken(email, password)
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
;
        Long userId=jdbcTemplate.queryForObject("select user_id from users where email=?",Long.class,userDetails.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken,email,userId));
    }
    @GetMapping("/forgot")
    ResponseEntity<?> forgotPassword(@RequestHeader String email) throws InvalidUserCredentialException {
        return ResponseEntity.of(Optional.of(iAuthService.forgotPassword(email)));
    }
    @PostMapping("/otp-verify")
    ResponseEntity<?> otpVerify(@RequestHeader Integer sentOtp, @RequestHeader String email) throws InvalidUserCredentialException {
        return ResponseEntity.ok(iAuthService.verifyOtp(sentOtp, email));
    }
    @PatchMapping("/password")
    ResponseEntity<?> updatePassword(@RequestHeader String email, @RequestHeader String newPassword) throws InvalidUserCredentialException {
        return ResponseEntity.ok(iAuthService.updatePassword(email, newPassword));
    }
}

