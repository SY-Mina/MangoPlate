package com.example.demo.src.user;

import com.example.demo.config.BaseResponseStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }



    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users?userIdx= && Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
//    @ResponseBody
//    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
//    public BaseResponse<List<GetUserRes>> getUsers() {
//        try {
//            // Get Users
//            List<GetUserRes> getUsersRes = userProvider.getUsers();
//            return new BaseResponse<>(getUsersRes);
//        }
//        catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 내 정보 조회 API
     * [GET] /users
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<GetUserRes> getUser() {
        // Get Users
        try{
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();
                System.out.println("userIdx: " + userIdx);
                GetUserRes getUserRes = userProvider.getUser(userIdx);
                return new BaseResponse<>(getUserRes);
            }

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 유저 정보 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("{userIdx}") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try{
            int exists = userProvider.checkUserExists(userIdx);
            if (exists==0) {
                return new BaseResponse<>(GET_USER_INVALID);
            }
            else {
                GetUserRes getUserRes = userProvider.getUser(userIdx);
                return new BaseResponse<>(getUserRes);
            }

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getPhoneNum().length()!=11) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(postUserReq.getUserName().length()>20) {
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if (postLoginReq.getEmail().length()<1) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if (postLoginReq.getPassword().length()<6 && postLoginReq.getPassword().length()>20) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
//    @ResponseBody
//    @PatchMapping("/{userIdx}")
//    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq user){
//        try {
//            //jwt에서 idx 추출.
//            int userIdxByJwt = jwtService.getUserIdx();
//            //userIdx와 접근한 유저가 같은지 확인
//            if(userIdx != userIdxByJwt){
//                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
//            }
//            //같다면 유저네임 변경
//            PatchUserReq patchUserReq = new PatchUserReq(user.getUserIdx(), user.getUserName());
//            userService.modifyUserName(patchUserReq);
//
//            String result = "Success";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 추천 유저 조회 API
     * [GET] /users/search
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/search") // (GET) 127.0.0.1:9000/app/users/search
    public BaseResponse<GetRecommendUsersRes> getRecommendUsers() {
        // Get Users
        try{
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();
                System.out.println("userIdx: " + userIdx);
                GetRecommendUsersRes getRecommendUsersRes = userProvider.getRecommendUsers(userIdx);
                return new BaseResponse<>(getRecommendUsersRes);
            }

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 카카오 로그인 API
     * [GET] /users/login/kakao
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/login/kakao")
    public BaseResponse<String> loginKakao(String code){
        //code: 토큰 받기 요청을 위한 인증 코드

        System.out.println(code);
        //test
        //POST로 카카오에 요청
        RestTemplate rt = new RestTemplate();
        //Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //Body
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "c542e13c92062485564e91d141795de5");
        params.add("redirect_uri", "https://minaserver.shop/app/users/login/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoRequest =
                new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST, kakaoRequest, String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoUser oAuthToken = new KakaoUser();
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), KakaoUser.class);
        } catch (JsonMappingException e){
            e.printStackTrace();
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        System.out.println(oAuthToken.getToken_type() + " " + oAuthToken.getAccess_token());

        // 받은 토큰값에 따라 사용자 정보 가져오기
        RestTemplate rt2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+oAuthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
                new HttpEntity<>(headers2);

        // Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );
        System.out.println(response2.getBody());

        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo();
        try {
            kakaoUserInfo = objectMapper2.readValue(response2.getBody(), KakaoUserInfo.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 카카오 로그인한 유저가 존재한다면
        if (userService.checkKakaoUserExist(kakaoUserInfo.getProperties().getNickname(),
                kakaoUserInfo.getKakao_account().getEmail())==1) {
            try {
                System.out.println("존재");
                String jwt = userService.loginKakaoUser(kakaoUserInfo.getProperties().getNickname(),
                        kakaoUserInfo.getKakao_account().getEmail());
                return new BaseResponse<>(jwt);
            } catch (BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }

        }
        else {
            try{
                System.out.println("로그인 없음");
                userService.createKakaoUser(kakaoUserInfo.getProperties().getNickname(),
                        kakaoUserInfo.getKakao_account().getEmail());
                String result = kakaoUserInfo.getProperties().getNickname() + " 로그인 완료";
                return new BaseResponse<>(result);

            } catch (BaseException exception){
                return new BaseResponse<>((exception.getStatus()));
            }

        }

    }
}
