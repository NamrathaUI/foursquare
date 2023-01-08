package com.example.foursquare.requestModel;

import lombok.Data;


@Data
public class SearchRequest {
    private String option;
    private String sortBy;
    private Float radius;
    private Integer priceRange;
    private Double latitude;
    private Double longitude;
    private FeatureRequest featureRequestList;
}
