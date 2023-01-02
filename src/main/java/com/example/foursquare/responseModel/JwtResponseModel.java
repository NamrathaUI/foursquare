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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    private  long userId;

    public JwtResponseModel(String token, String name,long userId) {
        this.token = token;
        this.name = name;
        this.userId=userId;
    }

    public String getToken() {
        return token;
    }
}
