package com.example.foursquare.service.system;

public interface SystemInterface {

    boolean verifyDuplicateEmail(String  email);

    boolean verifyLatLong(double latitude, double longitude);
}
