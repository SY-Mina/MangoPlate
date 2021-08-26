package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostStoreReq {
    private String name;
    private String location;
    private String region;
    private int type;

    public PostStoreReq() {}
}
