package com.example.demo.src.review;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ReviewProvider {

    private final ReviewDao reviewDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ReviewProvider(ReviewDao reviewDao, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.jwtService = jwtService;
    }


    public List<GetReviewsRes> getReviews(List<Integer> type, int userIdx) throws BaseException{

        List<GetReviewsRes> getReviews = new ArrayList<>();

            int size = type.size();
            for (int i=0; i<size; i++) {
                int total = reviewDao.getReviews(userIdx, type.get(i)).size();
                for (int j=0; j<total; j++) {
                    getReviews.add(reviewDao.getReviews(userIdx, type.get(i)).get(j));
                }

            }

            return getReviews;
        
    }

}
