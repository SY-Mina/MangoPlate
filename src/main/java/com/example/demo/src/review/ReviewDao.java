package com.example.demo.src.review;


import com.example.demo.src.review.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetReviewsRes> getReviews(int userIdx, int type) {

        String getReviewImagesQuery = "select (select type from RateType where Review.rateType=RateType.idx) as type,\n" +
                "       Review.idx as reviewIdx, restaurantIdx,\n" +
                "       (select name from Restaurant where Restaurant.idx=restaurantIdx) as store,\n" +
                "       content,\n" +
                "       concat('좋아요 ',(select COUNT(Heart.idx)\n" +
                "       from Heart where Heart.reviewIdx=Review.idx), '개') as heart,\n" +
                "       concat('댓글 ', (select COUNT(ReviewComment.idx)\n" +
                "           from ReviewComment where ReviewComment.reviewIdx=Review.idx), '개') as comments,\n" +
                "       (case\n" +
                "           when DATEDIFF(now(), Review.updatedAt)>=30\n" +
                "           then concat(TIMESTAMPDIFF(MONTH , Review.updatedAt, now()), '달 전')\n" +
                "           when DATEDIFF(now(), Review.updatedAt)>=1\n" +
                "           then concat(DATEDIFF(now(), Review.updatedAt), '일 전')\n" +
                "           when TIMESTAMPDIFF(Hour, Review.updatedAt, now())>=1\n" +
                "           then concat(TIMESTAMPDIFF(Hour, Review.updatedAt, now()), '시간 전')\n" +
                "           when TIMESTAMPDIFF(minute, Review.updatedAt, now())>=1\n" +
                "           then concat(TIMESTAMPDIFF(minute, Review.updatedAt, now()), '분 전')\n" +
                "           else concat(TIMESTAMPDIFF(second , Review.updatedAt, now()), '초 전')\n" +
                "           end) as date,\n" +
                "       userIdx, (select userName from User where User.idx=userIdx) as userName,\n" +
                "       (select count(Review.idx) from Review\n" +
                "        where Review.userIdx=U.idx) as reviews,\n" +
                "       (select count(Follow.idx) from Follow\n" +
                "        where Follow.userIdx=U.idx) as followers,\n" +
                "       (select (case\n" +
                "            when (select exists(select Wish.idx from Wish\n" +
                "                where Wish.userIdx=? and Wish.restaurantIdx=Review.restaurantIdx) = 0)\n" +
                "            then 'F'\n" +
                "            when (select exists(select Wish.idx from Wish\n" +
                "                where Wish.userIdx=? and Wish.restaurantIdx=Review.restaurantIdx) = 1)\n" +
                "            then 'T' end)) as myWish,\n" +
                "       (select (case\n" +
                "            when (select exists(select Heart.idx from Heart\n" +
                "                where Heart.userIdx=? and Heart.reviewIdx=Review.idx) = 0)\n" +
                "            then 'F'\n" +
                "            when (select exists(select Heart.idx from Heart\n" +
                "                where Heart.userIdx=? and Heart.reviewIdx=Review.idx) = 1)\n" +
                "            then 'T' end)) as myHeart\n" +
                "from Review inner join User U on Review.userIdx = U.idx\n" +
                "where Review.rateType=?;";
         return this.jdbcTemplate.query(getReviewImagesQuery,
                    (rs,rowNum) -> new GetReviewsRes(
                            rs.getString("type"),
                            rs.getInt("reviewIdx"),
                            rs.getInt("restaurantIdx"),
                            rs.getString("store"),
                            rs.getString("content"),
                            rs.getString("heart"),
                            rs.getString("comments"),
                            rs.getString("date"),
                            getReviewImages(rs.getInt("reviewIdx")),
                            rs.getInt("userIdx"),
                            rs.getString("userName"),
                            rs.getInt("reviews"),
                            rs.getInt("followers"),
                            rs.getString("myWish"),
                            rs.getString("myHeart")),
                    userIdx, userIdx, userIdx, userIdx, type);
    }



    public List<String> getReviewImages(int reviewIdx) {
        String getReviewImagesQuery = "select url\n" +
                "from ReviewImage\n" +
                "where ReviewImage.reviewIdx=?";
        List<String> images = this.jdbcTemplate.query(getReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("url")), reviewIdx);

        return images;
    }

}