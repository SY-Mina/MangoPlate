package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMenu {
    private String updatedAt;
    private List<MyMenu> menu = new ArrayList<>();

    //private String List<String> menuImg = new ArrayList<>();

    public GetMenu() {}
}
