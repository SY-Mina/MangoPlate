package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetEatdealRes {
    private int eatdealIdx;
    private int restaurantIdx;
    private String store;
    private String region;
    private String menu;
    private String price;
    private String discount;
    private String percentage;
    private String description;

    public GetEatdealRes() {}
}






