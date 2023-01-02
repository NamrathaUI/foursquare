package com.example.foursquare.service.system;

import com.example.foursquare.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SystemService implements SystemInterface{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public boolean verifyDuplicateEmail(String email) {
        return jdbcTemplate.query("select * from users where email=? and is_deleted='false'",
                new BeanPropertyRowMapper<>(Users.class), email).isEmpty();
    }


}
