package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReviewInfo {
    private String type;
    private int reviewIdx;
    private int restaurantIdx;
    private String store;
    private String content;
    private int comments;
    private String date;
    private List<String> images;

    public ReviewInfo() {}
}
