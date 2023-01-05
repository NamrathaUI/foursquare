package com.example.foursquare.requestModel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRequest {
    private String sortBy;
    private float radius;
    private int priceRange;
    private List<FeatureRequest> featureRequestList=new ArrayList<>();
}
