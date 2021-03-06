package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
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
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(String userName, int userIdx) throws BaseException {

            int result = userDao.modifyUserName(userName, userIdx);

    }

    public int checkKakaoUserExist(String nickname, String email){
        int exist = userDao.checkKakaoUserExist(nickname, email);
        return exist;
    }

    public String loginKakaoUser(String nickname, String email) throws BaseException {
        try {
            int userIdx = userDao.getKakaoUser(nickname, email);
            String jwt = jwtService.createJwt(userIdx);
            return jwt;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PostLoginRes createKakaoUser(String nickname, String email) throws BaseException {

        try {
            int userIdx = userDao.createKakaoUser(nickname, email);
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }


    }

    public int postFollow(int userIdx, int followIdx) throws BaseException{
        try {
            int result = userDao.postFollow(userIdx, followIdx);
            return result;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //PATCH
    public void patchFollow(String status, int userIdx, int followIdx) throws BaseException {

        try{
            userDao.patchFollow(status, userIdx, followIdx);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int patchProfImg(int userIdx, String profImg) throws BaseException{
        try {
            int result = userDao.patchProfImg(userIdx, profImg);
            return result;
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
