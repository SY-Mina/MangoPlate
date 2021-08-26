package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFollowerRes {
    private int userIdx;
    private String userName;
    private String profImg;
    private int reviews;
    private int followers;
    private String isfollowing;

    public GetFollowerRes() {}
}
