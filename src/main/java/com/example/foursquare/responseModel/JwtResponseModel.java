package com.example.foursquare.responseModel;

import java.io.Serializable;

public class JwtResponseModel implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    private long userId;

    public JwtResponseModel(String token, String name, long userId,String email) {
        this.token = token;
        this.name = name;
        this.userId = userId;
        this.email=email;
    }

    public String getToken() {
        return token;
    }
}
