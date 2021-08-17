package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantRes {
    private int restaurantIdx;
    private String name;
    private int rating;
    private String location;
    private int views;
    private int reviews;

}
