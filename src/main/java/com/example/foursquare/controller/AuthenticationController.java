package com.example.foursquare.controller;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.exception.InvalidUserCredentialException;
import com.example.foursquare.jwtutils.JwtUserDetailsService;
import com.example.foursquare.jwtutils.TokenManager;
import com.example.foursquare.model.Users;
import com.example.foursquare.responseModel.JwtResponseModel;
import com.example.foursquare.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
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
        try {
            return ResponseEntity.ok(iAuthService.signUp(users));
        } catch (com.example.foursquare.exception.CustomException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestHeader String email, @RequestHeader String password) throws Exception {
        authenticationManager.authenticate(
                new
                        UsernamePasswordAuthenticationToken(email, password)
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        List<Users> usersList = jdbcTemplate.query("select *from users where email=?", new BeanPropertyRowMapper<>(Users.class), userDetails.getUsername());
        final String jwtToken = tokenManager.generateJwtToken(userDetails);
        return ResponseEntity.ok(new JwtResponseModel(jwtToken, usersList.get(0).getName(), usersList.get(0).getUserId(), usersList.get(0).getEmail()));
    }

    @GetMapping("/forgot")
    ResponseEntity<?> forgotPassword(@RequestHeader String email) throws CustomException {
        return ResponseEntity.of(Optional.of(iAuthService.forgotPassword(email)));
    }

    @PostMapping("/otp-verify")
    ResponseEntity<?> otpVerify(@RequestHeader Integer sentOtp, @RequestHeader String email) throws CustomException {
        return ResponseEntity.ok(iAuthService.verifyOtp(sentOtp, email));
    }

    @PatchMapping("/password")
    ResponseEntity<?> updatePassword(@RequestHeader String email, @RequestHeader String newPassword) throws CustomException {
        try {
            return ResponseEntity.ok(iAuthService.updatePassword(email, newPassword));
        } catch (com.example.foursquare.exception.CustomException e) {
            throw new RuntimeException(e);
        }
    }
//    @PostMapping("/logout")
//    ResponseEntity<?> logout(@RequestHeader String authorization) throws CustomException {
//        return ResponseEntity.ok(iAuthService.logout(authorization));
//    }
}

