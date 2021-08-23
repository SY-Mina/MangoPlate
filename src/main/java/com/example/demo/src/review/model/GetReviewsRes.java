package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewsRes {
    private String type;
    private int reviewIdx;
    private int restaurantIdx;
    private String store;
    private String content;
    private String heart;
    private String comments;
    private String date;
    private List<String> images;
    private int userIdx;
    private String userName;
    private int reviews;
    private int followers;
    private String myWish;
    private String myHeart;

    public GetReviewsRes() {}
}