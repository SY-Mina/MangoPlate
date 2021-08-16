package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;
    private String userName;
    private String profImg;
    private String email;
    private String phoneNum;
    private int follower;
    private int following;
    private int reviews;
    private int went;
    private int photos;
    private int wish;
}
