package com.example.demo.src.review.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewsReq {
    private List<Integer> type = new ArrayList<>();

    public GetReviewsReq() {}
}
