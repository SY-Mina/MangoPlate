package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantInfo {
    private int restaurantIdx;
    private String name;
    private List<String> images = new ArrayList<>();
    private float rating;
    private String location;
    private int views;
    private int reviews;
    private int wish;
//    private String myWish;
//    private String myBeen;

    public GetRestaurantInfo() {}

}
