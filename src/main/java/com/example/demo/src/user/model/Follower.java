package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Follower {
    private int userIdx;
    private String userName;
    private String profImg;
    private int reviews;
    private int followers;

    public Follower() {}
}
