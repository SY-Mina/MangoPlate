package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoresSearchRes {
    private int restaurantIdx;
    private String name;
    private String profImg;
    private float rating;
    private String location;
    private int views;
    private int reviews;

    public GetStoresSearchRes() {}

}
