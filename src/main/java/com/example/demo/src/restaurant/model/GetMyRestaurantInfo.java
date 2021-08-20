package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMyRestaurantInfo {
    private String myWish;
    private String myBeen;

    public GetMyRestaurantInfo() {}

}