package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostWentReq {
    private int restaurantIdx;
    public String publicStatus;
    public String content;

    public PostWentReq() {}
}
