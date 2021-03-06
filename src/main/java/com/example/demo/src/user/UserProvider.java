package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetFollowerRes> getFollowers(int myIdx, int userIdx) throws BaseException{

            List<GetFollowerRes> followers = userDao.getFollowers(myIdx, userIdx);

            return followers;

    }

    public GetUserRes getUser(int userIdx) throws BaseException {
        GetUserRes getUserRes = userDao.getUser(userIdx);
        return getUserRes;
    }

    public GetRecommendUsersRes getRecommendUsers(int userIdx) throws BaseException {

            GetRecommendUsersRes getRecommendUsers = new GetRecommendUsersRes();

            List<RecommendUser> users = userDao.getRecommendUsers(userIdx);
            getRecommendUsers.setUser(users);

            return getRecommendUsers;

    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)){
            int userIdx = userDao.getPwd(postLoginReq).getIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

    public int checkUserExists(int userIdx) {return userDao.checkUserExist(userIdx);}

    public int checkFollow(int userIdx, int followIdx) {return userDao.checkFollow(userIdx, followIdx);}



    public String checkStatusFollow(int userIdx, int followIdx) {return userDao.checkStatusFollow(userIdx, followIdx);}
}
