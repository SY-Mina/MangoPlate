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
public class PatchReviewReq {
    private List<MultipartFile> image = new ArrayList<>();
    private int rateType;
    private String content;

    public PatchReviewReq() {}
}
