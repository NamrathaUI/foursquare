package com.example.foursquare.service;

import com.example.foursquare.exception.CustomException;
import com.example.foursquare.exception.InvalidUserCredentialException;
import com.example.foursquare.model.OtpManager;
import com.example.foursquare.model.Users;
import com.example.foursquare.responseModel.UserResponse;
import com.example.foursquare.service.system.SystemInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import java.util.Random;

@Service
public class AuthService implements IAuthService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private SystemInterface systemInterface;
    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public UserResponse signUp(Users users) throws CustomException {
        boolean val = systemInterface.verifyDuplicateEmail(users.getEmail());
        if (systemInterface.verifyDuplicateEmail(users.getEmail())) {
            users.setPassword(new BCryptPasswordEncoder().encode(users.getPassword()));
            jdbcTemplate.update("insert into users(name,email,phone_number,password) values(?,?,?,?)",users.getName(), users.getEmail(), users.getPhoneNumber(), users.getPassword());
            return new UserResponse("Account is created");
        }
        throw new CustomException("Provided details are already existing");
    }

    @Override
    public UserResponse forgotPassword(String email) throws CustomException {

        if (systemInterface.verifyDuplicateEmail(email))
            throw new CustomException("Invalid email");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("raspberrypi001025@gmail.com");
        List<Users> usersList = jdbcTemplate.query("select *from users where email=? and is_deleted='false'", new BeanPropertyRowMapper<>(Users.class), email);
        List<OtpManager> otpManager = jdbcTemplate.query("select *from otp_manager where user_id=?", new BeanPropertyRowMapper<>(OtpManager.class), usersList.get(0).getUserId());
        simpleMailMessage.setTo(usersList.get(0).getEmail());
        simpleMailMessage.setSubject("Request for password");
        int otpVerification = new Random().nextInt(1000, 9999);
        simpleMailMessage.setText("Your otp is " + otpVerification);
        javaMailSender.send(simpleMailMessage);
        if (otpManager.isEmpty()) {
            jdbcTemplate.update("insert into otp_manager (user_id,otp,expire_at,email) values(?,?,?,?)", usersList.get(0).getUserId(), otpVerification, LocalDateTime.now().plusMinutes(3), email);

        } else {
            jdbcTemplate.update("update otp_manager set otp=? where user_id=?", otpVerification, usersList.get(0).getUserId());
        }
        return new UserResponse("Otp has been sent to your registered email");
    }

    @Override
    public UserResponse verifyOtp(int otp, String email) throws CustomException {

        List<Users> userAcc = jdbcTemplate.query("select *from users where email=? and is_deleted='false'", new BeanPropertyRowMapper<>(Users.class), email);
        List<OtpManager> otpManager = jdbcTemplate.query("select *from otp_manager where user_id=?", new BeanPropertyRowMapper<>(OtpManager.class), userAcc.get(0).getUserId());

        if (otpManager.isEmpty() && userAcc.isEmpty()) {
            throw new CustomException("email or phoneNumber not found");
        }

        if (otpManager.get(0).getOtp() == otp) {

            jdbcTemplate.update("delete from otp_manager where user_id=?", userAcc.get(0).getUserId());
            return new UserResponse("Verification Done");
        }
        throw new CustomException("Verification failed");
    }

    @Override
    public UserResponse updatePassword(String email, String newPassword) throws CustomException {

        List<Users> usersList = jdbcTemplate.query("select *from users where email=? and is_deleted='false'", new BeanPropertyRowMapper<>(Users.class), email);
        if (usersList.isEmpty()) {
            throw new CustomException("email or phoneNumber not found");

        }
        String regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$&])(?=\\S+$).{6,250}$";
        if (newPassword.matches(regexp)) {
            if (new BCryptPasswordEncoder().matches(newPassword, usersList.get(0).getPassword())) {
                throw new CustomException("New password is same as Old password");
            }
            jdbcTemplate.update("update users set password=? where user_id=?", new BCryptPasswordEncoder().encode(newPassword), usersList.get(0).getUserId());

            return new UserResponse("password changed successfully");
        }
        throw new CustomException("Please provide password at least one uppercase letter,one lowercase letter,one number and " +
                "one special character with minimum length 6");
    }
    @Override
    public String generateOtp() {
        return new DecimalFormat("0000").format(new Random().nextInt(9999));
    }
}



