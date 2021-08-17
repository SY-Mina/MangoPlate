package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetOpenInfo {
    private String updatedAt;
    private String openTime;
    private String breakTime;
    private String lastOrder;
    private String holiday;
    private String price;

}
