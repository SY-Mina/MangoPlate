package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    private List<MultipartFile> image = new ArrayList<>();
    private int restaurantIdx;
    private int rateType;
    private String content;
    //private List<MultipartFile> images = new ArrayList<>();

    public PostReviewReq() {}
}
