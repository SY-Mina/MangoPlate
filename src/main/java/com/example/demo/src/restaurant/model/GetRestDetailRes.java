package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRestDetailRes {
    private GetRestaurantInfo restaurant;
    private String myWish;
    private String myBeen;
    private GetOpenInfo openInfo;
    private GetMenu menus;
    private GetKeyword keywords;
    private GetReviews reviews;

}
