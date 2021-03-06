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
import com.example.demo.utils.S3Uploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private final S3Uploader s3Uploader;


    public ReviewController(ReviewProvider reviewProvider, ReviewService reviewService, JwtService jwtService,S3Uploader s3Uploader){
        this.reviewProvider = reviewProvider;
        this.reviewService = reviewService;
        this.jwtService = jwtService;
        this.s3Uploader = s3Uploader;
    }

    /**
     * ???????????? ?????? ?????? API
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
     * ?????? ?????? ????????? API
     * [POST] /reviews/my
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/my") // (GET) 127.0.0.1:9000/app/reviews/my
    public BaseResponse<String> postReview(@ModelAttribute PostReviewReq postReviewReq) throws BaseException, IOException {
        try{
            System.out.println(postReviewReq.getRestaurantIdx());
            if (reviewProvider.checkItemExist(postReviewReq.getRestaurantIdx()) == 0) {
                return new BaseResponse<>(GET_ITEM_EMPTY);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            if (postReviewReq.getRateType() != 1 & postReviewReq.getRateType() !=2 & postReviewReq.getRateType() !=3) {
                return new BaseResponse<>(POST_REVIEW_INVALID_RATETYPE);
            }
            if (postReviewReq.getContent().length() > 1000) {
                return new BaseResponse<>(POST_REVIEW_INVALID_CONTENT);
            }
            else {
                int userIdx = jwtService.getUserIdx();
                int restaurantIdx = postReviewReq.getRestaurantIdx();

                int reviewIdx = reviewService.postReview(userIdx, restaurantIdx, postReviewReq.getRateType(),
                        postReviewReq.getContent());
                System.out.println(reviewIdx);

                List<MultipartFile> images = postReviewReq.getImage();
                int size = images.size();
                for (int i=0; i<size; i++) {
                    String url = s3Uploader.upload(images.get(i), "review", reviewIdx);
                    System.out.println(url);
                    int idx = reviewService.postReviewImages(reviewIdx, url);
                }

                String result ="T";
                return new BaseResponse<>(result);
            }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ?????? API
     * [PATCH] /reviews/:reviewIdx/status
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PatchMapping("/{reviewIdx}/status") // (GET) 127.0.0.1:9000/app/reviews/:reviewIdx/status
    public BaseResponse<String> patchItemStatus (@PathVariable("reviewIdx") int reviewIdx) throws BaseException {
        try{
            if (reviewProvider.checkReviewExist(reviewIdx) == 0) {
                return new BaseResponse<>(GET_REVIEW_EMPTY);
            }

            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                if (reviewProvider.checkStatus(reviewIdx, userIdx)==0) {
                    return new BaseResponse<>(PATCH_USER_INVALID_STATUS);
                }
                reviewService.patchItemStatus(reviewIdx, userIdx);

                String result ="Success";
                return new BaseResponse<>(result);
            }

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ?????? API
     * [PATCH] /reviews/:reviewIdx
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PatchMapping("/{reviewIdx}") // (GET) 127.0.0.1:9000/app/reviews/:reviewIdx/status
    public BaseResponse<String> patchItemStatus (@PathVariable("reviewIdx") int reviewIdx,@ModelAttribute PatchReviewReq patchReviewReq) throws BaseException, IOException {
        try{
            if (reviewProvider.checkReviewExist(reviewIdx) == 0) {
                return new BaseResponse<>(GET_REVIEW_EMPTY);
            }
            if (patchReviewReq.getContent().length() > 1000) {
                return new BaseResponse<>(POST_REVIEW_INVALID_CONTENT);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                if (reviewProvider.checkStatus(reviewIdx, userIdx)==0) {
                    return new BaseResponse<>(PATCH_USER_INVALID);
                }
                if (patchReviewReq.getContent().length()<1) {
                    if (patchReviewReq.getRateType() == 1 | patchReviewReq.getRateType()==2
                            | patchReviewReq.getRateType() == 3) {
                        reviewService.patchReview(reviewIdx, patchReviewReq.getRateType());
                    }
                    else {

                        String result ="???????????? ????????????.";
                        return new BaseResponse<>(result);
                    }
                }
                else {
                    if (patchReviewReq.getRateType() == 1 | patchReviewReq.getRateType()==2
                        | patchReviewReq.getRateType() == 3) {
                        reviewService.patchReview(reviewIdx, patchReviewReq.getContent(), patchReviewReq.getRateType());

                    }
                    else {
                        reviewService.patchReview(reviewIdx, patchReviewReq.getContent());
                    }
                }
                List<MultipartFile> images = patchReviewReq.getImage();
                int size = images.size();
                for (int i=0; i<size; i++) {
                    String url = s3Uploader.upload(images.get(i), "review", reviewIdx);
                    System.out.println(url);
                    int idx = reviewService.postReviewImages(reviewIdx, url);
                }

                String result ="Success";
                return new BaseResponse<>(result);
            }

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ??????????????? API
     * [POST] /reviews/:reviewIdx/heart
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/{reviewIdx}/heart") // (GET) 127.0.0.1:9000/app/reviews/my
    public BaseResponse<String> postHeart(@PathVariable("reviewIdx") int reviewIdx) throws BaseException {
        try{
            if (reviewProvider.checkReviewExist(reviewIdx) == 0) {
                return new BaseResponse<>(GET_REVIEW_EMPTY);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                // ?????? ?????? ??????.
                if (reviewProvider.checkHeart(userIdx, reviewIdx)==0) {
                    reviewService.postHeart(userIdx, reviewIdx);
                    String result ="T";
                    return new BaseResponse<>(result);
                }
                else {
                    // ?????? T->F
                    if (reviewProvider.checkStatusHeart(userIdx, reviewIdx).equals("T")) {
                        reviewService.patchHeart("F", userIdx, reviewIdx);

                        String result ="F";
                        return new BaseResponse<>(result);
                    }
                    // ?????? F->T
                    else {
                        reviewService.patchHeart("T", userIdx, reviewIdx);

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
     * [POST] /reviews/:reviewIdx/comment
     * @return BaseResponse<String>
     */
    // Path-variable
    @ResponseBody
    @PostMapping("/{reviewIdx}/comment") // (GET) 127.0.0.1:9000/app/reviews/:reviewIdx/comment
    public BaseResponse<String> postComment(@PathVariable("reviewIdx") int reviewIdx, @RequestBody PostCommentReq postCommentReq) throws BaseException {
        try{
            if (reviewProvider.checkReviewExist(reviewIdx) == 0) {
                return new BaseResponse<>(GET_REVIEW_EMPTY);
            }
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            if (reviewProvider.checkMention(reviewIdx, postCommentReq.getMentionIdx())==0) {
                return new BaseResponse<>(GET_MENTION_INVALID);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                if (postCommentReq.getMentionIdx() == 0) {
                    reviewService.postComment(userIdx, reviewIdx, postCommentReq.getContent());
                }
                else {
                    reviewService.postComment(userIdx, reviewIdx, postCommentReq.getMentionIdx(), postCommentReq.getContent());
                }


                String result ="Success";
                return new BaseResponse<>(result);
            }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ???????????? ?????? ?????? API
     * [GET] /reviews/:reviewIdx
     * @return BaseResponse<List<GetReviewsRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{reviewIdx}") // (GET) 127.0.0.1:9000/app/reviews
    public BaseResponse<GetReviewDetailRes> getReview(@PathVariable("reviewIdx") int reviewIdx) {

        try{
            if (jwtService.getJwt()==null) {
                return new BaseResponse<>(EMPTY_JWT);
            }
            if (reviewProvider.checkReviewExist(reviewIdx) == 0) {
                return new BaseResponse<>(GET_REVIEW_EMPTY);
            }
            else {
                int userIdx = jwtService.getUserIdx();

                GetReviewDetailRes getReview = reviewProvider.getReview(reviewIdx, userIdx);
                return new BaseResponse<>(getReview);
            }
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * ???????????? ?????? ?????? ?????? API
     * [GET] /reviews/stores?keyword=
     * @return BaseResponse<List<GetReviewStoresRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/stores") // (GET) 127.0.0.1:9000/app/reviews
    public BaseResponse<List<GetReviewStoresRes>> getReviewStores(@RequestParam("keyword") String keyword) {

        try{
            if (keyword.length()<1) {
                List<GetReviewStoresRes> getReviewStores = reviewProvider.getReviewStoresNull();
                return new BaseResponse<>(getReviewStores);
            }
            else {
                List<GetReviewStoresRes> getReviewStores = reviewProvider.getReviewStores(keyword);
                if (getReviewStores.size()==0) {
                    return new BaseResponse<>(GET_STORES_SEARCH_EMPTY);
                }
                return new BaseResponse<>(getReviewStores);
            }


        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * EAT??? ?????? API
     * [GET] /reviews/stores/eatdeal
     * @return BaseResponse<List<GetReviewStoresRes>>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/stores/eatdeal") // (GET) 127.0.0.1:9000/app/reviews
    public BaseResponse<List<GetEatdealRes>> getEatdeal() {

        try{
            List<GetEatdealRes> getEatdeal = reviewProvider.getEatdeal();
            return new BaseResponse<>(getEatdeal);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
}
