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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class RestaurantService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestaurantDao restaurantDao;
    private final RestaurantProvider restaurantProvider;
    private final JwtService jwtService;


    @Autowired
    public RestaurantService(RestaurantDao restaurantDao, RestaurantProvider restaurantProvider, JwtService jwtService) {
        this.restaurantDao = restaurantDao;
        this.restaurantProvider = restaurantProvider;
        this.jwtService = jwtService;

    }

    //POST
    public void postHeart(int userIdx, int restaurantIdx) throws BaseException {

        try{
            restaurantDao.postHeart(userIdx, restaurantIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void patchHeart(String status, int userIdx, int restaurantIdx) throws BaseException {

        try{
            restaurantDao.patchHeart(status, userIdx, restaurantIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //POST
    public void postWent(int userIdx, int restaurantIdx, String status, String content) throws BaseException {

        try{
            restaurantDao.postWent(userIdx, restaurantIdx, status, content);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void patchWentStatus(int wentIdx) throws BaseException {

        try{
            restaurantDao.patchWentStatus( wentIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
    public void patchWentPublic(int wentIdx, String publicStatus) throws BaseException {


            restaurantDao.patchWentPublic( wentIdx, publicStatus);


    }
    public void patchWentContent(int wentIdx, String content) throws BaseException {


            restaurantDao.patchWentContent( wentIdx,content);


    }
    public void patchWent(int wentIdx, String content, String publicStatus) throws BaseException {


            restaurantDao.patchWent( wentIdx,content, publicStatus);


    }
}
