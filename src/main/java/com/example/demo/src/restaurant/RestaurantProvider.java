package com.example.demo.src.restaurant;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.restaurant.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class RestaurantProvider {

    private final RestaurantDao restaurantDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public RestaurantProvider(RestaurantDao restaurantDao, JwtService jwtService) {
        this.restaurantDao = restaurantDao;
        this.jwtService = jwtService;
    }

    public List<GetRestaurantRes> getRestaurants() throws BaseException{
        try{
            List<GetRestaurantRes> getRestaurantRes = restaurantDao.getRestaurants();
            return getRestaurantRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetReviewsRes getReviews(int restaurantIdx) throws BaseException{

            GetReviewsRes getReviews = new GetReviewsRes();

            GetReviewsDetail getReviewsDetail = restaurantDao.getReviewsDetail(restaurantIdx);
            List<Reviews> reviewsList = restaurantDao.getReviewLists(restaurantIdx);

            getReviews.setReviewsDetail(getReviewsDetail);
            getReviews.setReviews(reviewsList);

            return getReviews;

    }

    public GetRestDetailRes getRestDetail(int restaurantIdx, int userIdx) throws BaseException{

        GetRestDetailRes getRestDetail = new GetRestDetailRes();

        GetRestaurantInfo getRestaurantInfo = restaurantDao.getRestaurantInfo(restaurantIdx);
        GetMyRestaurantInfo getMyRestaurantInfo = restaurantDao.getMyRestaurantInfo(restaurantIdx, userIdx);
        GetOpenInfo getOpenInfo = restaurantDao.getOpenInfo(restaurantIdx);
        GetMenu getMenu = restaurantDao.getMenu(restaurantIdx);
        List<GetKeyword> keywords = restaurantDao.getKeyword(restaurantIdx);
        GetReviewList reviews = restaurantDao.getReviews(restaurantIdx);

        getRestDetail.setRestaurant(getRestaurantInfo);
        getRestDetail.setMyinfo(getMyRestaurantInfo);
        getRestDetail.setOpenInfo(getOpenInfo);
        getRestDetail.setMenus(getMenu);
        getRestDetail.setKeywords(keywords);
        getRestDetail.setReviews(reviews);

        return getRestDetail;

    }

    public int checkItemExist(int restaurantIdx){return restaurantDao.checkItemExist(restaurantIdx);}
}
