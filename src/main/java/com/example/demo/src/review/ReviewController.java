package com.example.demo.src.review;

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
import com.example.demo.src.review.model.*;
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
@RequestMapping("/app/reviews")
public class ReviewController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReviewProvider reviewProvider;
    @Autowired
    private final ReviewService reviewService;
    @Autowired
    private final JwtService jwtService;




    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService, JwtService jwtService){
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.jwtService = jwtService;
    }

    /**
     * 소식에서 리뷰 조회 API
     * [GET] /reviews
     * @return BaseResponse<List<GetReviewsRes>>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("") // (GET) 127.0.0.1:9000/app/reviews
    public BaseResponse<List<GetReviewsRes>> getReviews(@RequestBody GetReviewsReq getReviewReq) {
        if (getReviewReq.getType().size() == 0) {
            return new BaseResponse<>(GET_REVIEW_TYPE_EMPTY);
        }
        try{
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                List<GetReviewsRes> getReviews = reviewProvider.getReviews(getReviewReq.getType(), userIdx);
                return new BaseResponse<>(getReviews);
            }
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 식당 가봤어요 API
     * [POST] /reviews
     * @return BaseResponse<String>
     */
    // Path-variable
//    @ResponseBody
//    @PostMapping("") // (GET) 127.0.0.1:9000/app/stores/went
//    public BaseResponse<String> postHeart(@RequestBody PostReviewReq postReviewReq) throws BaseException {
//        try{
//            if (restaurantProvider.checkItemExist(postReviewReq.getRestaurantIdx()) == 0) {
//                return new BaseResponse<>(GET_ITEM_EMPTY);
//            }
//            if (jwtService.getJwt()==null) {
//                return new BaseResponse<>(EMPTY_JWT);
//            }
//            if (postWentReq.getContent().length() > 50) {
//                return new BaseResponse<>(POST_STORES_INVALID);
//            }
//            else {
//                int userIdx = jwtService.getUserIdx();
//                int restaurantIdx = postReviewReq.getRestaurantIdx();
//
//                restaurantService.postWent(userIdx, restaurantIdx, postReviewReq.getRateType(), postReviewReq.getImages());
//                String result ="T";
//                return new BaseResponse<>(result);
//            }
//
//
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

}
