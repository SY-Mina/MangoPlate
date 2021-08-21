package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
    private int userIdx;
    private String userName;
    private int reviews;
    private int followers;
    public UserInfo() {}
}
