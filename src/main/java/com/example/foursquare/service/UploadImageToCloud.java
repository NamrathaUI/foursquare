package com.example.foursquare.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Singleton;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
@Service
public class UploadImageToCloud implements ImageUploadInterface{
    private final Cloudinary cloudinary;
    public UploadImageToCloud() {
        cloudinary = Singleton.getCloudinary();
        cloudinary.config.cloudName = "dbwyfnr2a";
        cloudinary.config.apiSecret = "QEDhtucjJDNi9pmkDW6rKsGOypA";
        cloudinary.config.apiKey = "821198275669225";
    }



    @Override
    public Map uploadImage(Object photo, Map options) throws IOException {
        return cloudinary.uploader().upload(photo, options);
    }
}