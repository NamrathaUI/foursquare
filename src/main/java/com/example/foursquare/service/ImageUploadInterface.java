package com.example.foursquare.service;

import java.io.IOException;
import java.util.Map;

public interface ImageUploadInterface {

    Map uploadImage(Object photo, Map options) throws IOException;
}