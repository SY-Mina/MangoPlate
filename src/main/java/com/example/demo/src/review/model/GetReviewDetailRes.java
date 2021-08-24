package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewDetailRes {
    private String type;
    private int reviewIdx;
    private int restaurantIdx;
    private String store;
    private String location;
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
    private List<GetReviewComment> commentList = new ArrayList<>();

    public GetReviewDetailRes() {}
}






