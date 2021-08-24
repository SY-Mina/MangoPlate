package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewComment {
    private int userIdx;
    private String userName;
    private String profImg;
    private String holic21;
    private String mention;
    private String content;
    private String date;

    public GetReviewComment() {}
}






