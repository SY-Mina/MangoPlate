package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    private int restaurantIdx;
    private int rateType;
    private List<String> images = new ArrayList<>();

    public PostReviewReq() {}
}
