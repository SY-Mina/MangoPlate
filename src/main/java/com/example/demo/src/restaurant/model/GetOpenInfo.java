package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetOpenInfo {
    private String updatedAt;
    private List<String> openTime = new ArrayList<>();
    private String breakTime;
    private String lastOrder;
    private String holiday;
    private String price;
    private String type;
    private String parking;
    private String website;

    public GetOpenInfo() {}

}
