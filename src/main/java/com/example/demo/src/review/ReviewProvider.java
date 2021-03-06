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

    public int checkItemExist(int restaurantIdx){return reviewDao.checkItemExist(restaurantIdx);}


    public int checkReviewExist(int reviewIdx){return reviewDao.checkReviewExist(reviewIdx);}

    public int checkStatus(int reviewIdx, int userIdx) {
        return reviewDao.checkStatus(reviewIdx, userIdx);
    }

    public int checkHeart(int userIdx, int reviewIdx) {
        return reviewDao.checkHeart(userIdx, reviewIdx);
    }

    public String checkStatusHeart(int userIdx, int reviewIdx) {
        return reviewDao.checkStatusHeart(userIdx, reviewIdx);}


    public int checkMention(int reviewIdx, int mentionIdx) {
        //멘션은 작성자랑 댓글 쓴 사람만 할 수 있음
        //작성자인지 확인
            if (reviewDao.checkReviewMention(reviewIdx, mentionIdx)==1) {
                return 1;
            }
            //댓글 쓴 사람인지 확인
            else {
                if (reviewDao.checkCommentMention(reviewIdx, mentionIdx)==1) {
                    return 1;
                }
                else return 0;
            }
        }


    public GetReviewDetailRes getReview(int reviewIdx, int userIdx) throws BaseException {

            GetReviewDetailRes getReviewDetail = reviewDao.getReview(reviewIdx, userIdx);
            return getReviewDetail;

    }

    public List<GetReviewStoresRes> getReviewStoresNull() throws BaseException{

        List<GetReviewStoresRes> getReviews = reviewDao.getReviewStoresNull();

        return getReviews;

    }

    public List<GetReviewStoresRes> getReviewStores(String keyword) throws BaseException{

        List<GetReviewStoresRes> getReviews = reviewDao.getReviewStores(keyword);

        return getReviews;

    }

    public List<GetEatdealRes> getEatdeal() throws BaseException{

        List<GetEatdealRes> getEatdeal = reviewDao.getEatdeal();

        return getEatdeal;

    }
}
