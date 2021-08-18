package com.example.demo.src.restaurant.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Reviews {
    private String type;
    private int userIdx;
    private String userName;
    private int reviewIdx;
    private int reviews;
    private int followers;
    private String content;
    private int heart;
    private int comments;
    private String date;
    //private List<String> images;

    public Reviews() {}
}
