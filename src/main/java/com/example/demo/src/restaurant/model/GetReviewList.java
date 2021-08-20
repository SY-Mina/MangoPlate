package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewList {
    private int total;
    private int good;
    private int soso;
    private int bad;
    List<Reviews> reviewList = new ArrayList<>();

    public GetReviewList() {}
}
