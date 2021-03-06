package com.example.demo.src.restaurant;

import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.model.GetUserRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
@RequestMapping("/app/stores")
public class RestaurantController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RestaurantProvider restaurantProvider;
    @Autowired
    private final RestaurantService restaurantService;
    @Autowired
    private final JwtService jwtService;




    public RestaurantController(RestaurantProvider restaurantProvider, RestaurantService restaurantService, JwtService jwtService){
        this.restaurantProvider = restaurantProvider;
        this.restaurantService = restaurantService;
        this.jwtService = jwtService;
    }


    /**
     * ?????? ???????????? ?????? API
     * [GET] /stores
     * @return BaseResponse<GetRestaurantRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/stores
    public BaseResponse<List<GetRestaurantRes>> getRestaurants() {

        try{
            List<GetRestaurantRes> getRestaurantRes = restaurantProvider.getRestaurants();
            return new BaseResponse<>(getRestaurantRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * ????????? ?????? ?????? ?????? API
     * [GET] /stores/:restaurantIdx/reviews
     * @return BaseResponse<GetReviewsRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("{restaurantIdx}/reviews") // (GET) 127.0.0.1:9000/app/stores/:restaurantIdx/reviews
    public BaseResponse<GetReviewsRes> getReviews(@PathVariable("restaurantIdx") int restaurantIdx) {
        if (restaurantProvider.checkItemExist(restaurantIdx) == 0) {
            return new BaseResponse<>(GET_ITEM_EMPTY);
        }
        // Get Users
        try{
            GetReviewsRes getReviews = restaurantProvider.getReviews(restaurantIdx);
            return new BaseResponse<>(getReviews);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * ?????? ?????? ?????? API
     * [GET] /stores/:restaurantIdx
     * @return BaseResponse<GetReviewsRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("{restaurantIdx}") // (GET) 127.0.0.1:9000/app/stores/:restaurantIdx
    public BaseResponse<GetRestDetailRes> getRestDetail(@PathVariable("restaurantIdx") int restaurantIdx) {
        if (restaurantProvider.checkItemExist(restaurantIdx) == 0) {
            return new BaseResponse<>(GET_ITEM_EMPTY);
        }
        // Get Users
        try{
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();
                System.out.println("userIdx: " + userIdx);
                GetRestDetailRes getRestDetail = restaurantProvider.getRestDetail(restaurantIdx, userIdx);
                return new BaseResponse<>(getRestDetail);
            }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * ?????? ?????? ?????? API
     * [GET] /stores/:restaurantIdx
     * @return BaseResponse<GetReviewsRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("{restaurantIdx}/near") // (GET) 127.0.0.1:9000/app/stores/:restaurantIdx
    public BaseResponse<List<GetNearStoreRes>> getNearStore(@PathVariable("restaurantIdx") int restaurantIdx) {
        if (restaurantProvider.checkItemExist(restaurantIdx) == 0) {
            return new BaseResponse<>(GET_ITEM_EMPTY);
        }
        try{
            List<GetNearStoreRes> getNearStore = restaurantProvider.getNearStore(restaurantIdx);
            return new BaseResponse<>(getNearStore);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * ?????? ???????????? API
     * [POST] /stores/wish
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/wish") // (GET) 127.0.0.1:9000/app/stores/wish
    public BaseResponse<String> postHeart(@RequestBody PostHeartReq postHeartReq) throws BaseException {
        try{
            if (restaurantProvider.checkItemExist(postHeartReq.getRestaurantIdx()) == 0) {
                return new BaseResponse<>(GET_ITEM_EMPTY);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                // ?????? ?????? ??????.
                if (restaurantProvider.checkHeart(userIdx, postHeartReq.getRestaurantIdx())==0) {
                    restaurantService.postHeart(userIdx, postHeartReq.getRestaurantIdx());
                    String result ="T";
                    return new BaseResponse<>(result);
                }
                else {
                    // ?????? T->F
                    if (restaurantProvider.checkStatusHeart(userIdx, postHeartReq.getRestaurantIdx()).equals("T")) {
                        restaurantService.patchHeart("F", userIdx, postHeartReq.getRestaurantIdx());

                        String result ="F";
                        return new BaseResponse<>(result);
                    }
                    // ?????? F->T
                    else {
                        restaurantService.patchHeart("T", userIdx, postHeartReq.getRestaurantIdx());

                        String result ="T";
                        return new BaseResponse<>(result);
                    }

                }

            }
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ???????????? API
     * [POST] /stores/went
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/went") // (GET) 127.0.0.1:9000/app/stores/went
    public BaseResponse<String> postHeart(@RequestBody PostWentReq postWentReq) throws BaseException {
        try{
                if (restaurantProvider.checkItemExist(postWentReq.getRestaurantIdx()) == 0) {
                    return new BaseResponse<>(GET_ITEM_EMPTY);
                }
                if (jwtService.getJwt()==null) {
                    return new BaseResponse<>(EMPTY_JWT);
                }
                if (postWentReq.getContent().length() > 50) {
                    return new BaseResponse<>(POST_STORES_INVALID);
                }
                else {
                    int userIdx = jwtService.getUserIdx();
                    int restaurantIdx = postWentReq.getRestaurantIdx();

                    restaurantService.postWent(userIdx, restaurantIdx, postWentReq.getPublicStatus(), postWentReq.getContent());
                    String result ="T";
                    return new BaseResponse<>(result);
                }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * ?????? ???????????? ?????? API
     * [POST] /stores/went/:wentIdx/status
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PatchMapping("/went/{wentIdx}/status") // (GET) 127.0.0.1:9000/app/stores/went
    public BaseResponse<String> patchWentStatus(@PathVariable("wentIdx")int wentIdx) throws BaseException {
        try{
            if (restaurantProvider.checkWentExist(wentIdx) == 0) {
                return new BaseResponse<>(GET_WENT_EMPTY);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                if (restaurantProvider.checkStatus(userIdx, wentIdx)==0) {
                    return new BaseResponse<>(PATCH_USER_INVALID_STATUS);
                }
                restaurantService.patchWentStatus( wentIdx);
                String result ="T";
                return new BaseResponse<>(result);
            }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * ?????? ???????????? ?????? API
     * [POST] /stores/went/:wentIdx
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PatchMapping("/went/{wentIdx}") // (GET) 127.0.0.1:9000/app/stores/went
    public BaseResponse<String> patchWent(@PathVariable("wentIdx")int wentIdx, @RequestBody PatchWentReq patchWentReq) throws BaseException {
        try{
            if (restaurantProvider.checkWentExist(wentIdx) == 0) {
                return new BaseResponse<>(GET_WENT_EMPTY);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            if (patchWentReq.getContent().length()>50) {
                return new BaseResponse<>(POST_STORES_INVALID);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                if (restaurantProvider.checkStatus(userIdx, wentIdx)==0) {
                    return new BaseResponse<>(PATCH_USER_INVALID);
                }
                if (patchWentReq.getContent().length()<1) {
                    if (patchWentReq.getPublicStatus().length()<1) {
                        String result ="???????????? ????????????.";
                        return new BaseResponse<>(result);
                    }
                    else {
                        restaurantService.patchWentPublic(wentIdx, patchWentReq.getPublicStatus());
                    }
                }
                else {
                    if (patchWentReq.getPublicStatus().length()<1) {
                        restaurantService.patchWentContent(wentIdx, patchWentReq.getContent());
                    }
                    else {
                        restaurantService.patchWent(wentIdx, patchWentReq.getContent(),patchWentReq.getPublicStatus());
                    }
                }

                String result ="T";
                return new BaseResponse<>(result);
            }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ???????????? API
     * [GET] /reviews/stores/eatdeal
     * @return BaseResponse<List<GetReviewStoresRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/search") // (GET) 127.0.0.1:9000/app/reviews
    public BaseResponse<List<GetStoresSearchRes>> getStoreSearch(@RequestParam("keyword") String keyword) {

        try{
            List<GetStoresSearchRes> getStoreSearch = restaurantProvider.getStoreSearch(keyword);
            return new BaseResponse<>(getStoreSearch);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


}
