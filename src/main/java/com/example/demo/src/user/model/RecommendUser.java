package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecommendUser {
    private int userIdx;
    private String userName;
    private String profImg;
    private String holic21;
    private int reviews;
    private int follower;
    private String followed;
}
