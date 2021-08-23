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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ReviewService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final ReviewProvider reviewProvider;
    private final JwtService jwtService;


    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider, JwtService jwtService) {
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;

    }


    //POST
    public int postReview(int userIdx, int restaurantIdx, int rateType, String content) throws BaseException {

        try{
            int result = reviewDao.postReview(userIdx, restaurantIdx, rateType, content);
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    //POST
    public int postReviewImages(int reviewIdx, String url) throws BaseException {

        try{
            int result =reviewDao.postReviewImages(reviewIdx, url);
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //PATCH
    public int patchItemStatus(int reviewIdx, int userIdx) throws BaseException {

        try{
            int result =reviewDao.patchItemStatus(reviewIdx, userIdx);
            return result;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
