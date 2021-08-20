package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRestDetailRes {
    private GetRestaurantInfo restaurant;
    private GetMyRestaurantInfo myinfo;
    private GetOpenInfo openInfo;
    private GetMenu menus;
    private List<GetKeyword> keywords = new ArrayList<>();
    private GetReviewList reviews;

    public GetRestDetailRes() {}

}
