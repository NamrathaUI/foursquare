package com.example.foursquare.jwtutils;

import com.example.foursquare.MyUserDetails;

import com.example.foursquare.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<Users> users = jdbcTemplate.query("select * from users where email=?", new BeanPropertyRowMapper<>(Users.class), email);
        if (users != null) {
            return new MyUserDetails((Users) users.get(0));
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}
