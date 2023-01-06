package com.example.foursquare.requestModel;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRequest {
    private String sortBy;
    private Float radius;
    private Integer priceRange;
    private FeatureRequest featureRequestList;
}
