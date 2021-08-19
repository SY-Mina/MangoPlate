package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class GetMenu {
    private String updatedAt;
    private String menuName;
    private String price;
    private String List<String> menuImg = new ArrayList<>();

    public GetMenu() {}
}
