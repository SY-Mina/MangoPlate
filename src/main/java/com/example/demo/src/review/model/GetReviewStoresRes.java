package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewStoresRes {
    private String type;
    private int restaurantIdx;
    private String store;
    private String location;
    

    public GetReviewStoresRes() {}
}
