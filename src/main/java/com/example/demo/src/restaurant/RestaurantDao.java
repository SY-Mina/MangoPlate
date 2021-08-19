package com.example.demo.src.restaurant;


import com.example.demo.src.restaurant.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RestaurantDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetRestaurantRes> getRestaurants() {
        String getRestaurantQuery = "select idx as restaurantIdx, name, rating,\n" +
                "       (select url from ReviewImage inner join Review\n" +
                "           where ReviewImage.reviewIdx=Review.idx\n" +
                "             and Review.restaurantIdx=Restaurant.idx limit 1) as profImg,\n" +
                "       region as location,\n" +
                "       (select COUNT(View.idx)\n" +
                "       from View\n" +
                "       where Restaurant.idx=View.idx) as views,\n" +
                "       (select COUNT(Review.idx)\n" +
                "           from Review\n" +
                "           where Restaurant.idx=Review.restaurantIdx) as reviews\n" +
                "from Restaurant\n" +
                "order by rating desc;";

        return this.jdbcTemplate.query(getRestaurantQuery,
                (rs, rowNum) -> new GetRestaurantRes(
                        rs.getInt("restaurantIdx"),
                        rs.getString("name"),
                        rs.getString("profImg"),
                        rs.getFloat("rating"),
                        rs.getString("location"),
                        rs.getInt("views"),
                        rs.getInt("reviews"))
        );
    }

    public int checkItemExist(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Restaurant where idx=?)",
                int.class,
                restaurantIdx);
    }


    public GetReviewsDetail getReviewsDetail(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select distinct name,\n" +
                        "       (select COUNT(Review.idx)\n" +
                        "       from Review\n" +
                        "           where Restaurant.idx=restaurantIdx) as total,\n" +
                        "        (select COUNT(Review.idx)\n" +
                        "       from Review\n" +
                        "           where Restaurant.idx=restaurantIdx and rateType=1) as good,\n" +
                        "        (select COUNT(Review.idx)\n" +
                        "       from Review\n" +
                        "           where Restaurant.idx=restaurantIdx and rateType=2) as soso,\n" +
                        "        (select COUNT(Review.idx)\n" +
                        "       from Review\n" +
                        "           where Restaurant.idx=restaurantIdx and rateType=3) as bad\n" +
                        "from Restaurant inner join Review\n" +
                        "where Restaurant.idx=?;",
                (rs, rowNum) -> new GetReviewsDetail(
                        rs.getString("name"),
                        rs.getInt("total"),
                        rs.getInt("good"),
                        rs.getInt("soso"),
                        rs.getInt("bad")),
                restaurantIdx);
    }

    public List<Reviews> getReviewLists(int restaurantIdx) {

        return this.jdbcTemplate.query("select (case rateType\n" +
                        "    when 1 then '맛있다!'\n" +
                        "    when 2 then '괜찮다.'\n" +
                        "    else '별로' end) as type,\n" +
                        "    U.idx as userIdx, userName, Review.idx as reviewIdx,\n" +
                        "       (select count(Review.idx) from Review\n" +
                        "        where Review.userIdx=U.idx) as reviews,\n" +
                        "    (select count(Follow.idx) from Follow\n" +
                        "        where Follow.userIdx=U.idx) as followers,\n" +
                        "    content,\n" +
                        "       (select count(Heart.idx) from Heart\n" +
                        "           where Heart.userIdx=U.idx) as heart,\n" +
                        "       (select count(ReviewComment.idx) from ReviewComment\n" +
                        "           where ReviewComment.userIdx=U.idx) as comments,\n" +
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
                        "           end) as date\n" +
                        "from Review join User U on Review.userIdx = U.idx\n" +
                        "where Review.restaurantIdx=?;",
                (rs, rowNum) -> new Reviews(
                        rs.getString("type"),
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getInt("reviewIdx"),
                        rs.getInt("reviews"),
                        rs.getInt("followers"),
                        rs.getString("content"),
                        rs.getInt("heart"),
                        rs.getInt("comments"),
                        rs.getString("date"),
                        getReviewImages(rs.getInt("reviewIdx"))), restaurantIdx
                );
    }

    public List<String> getReviewImages(int reviewIdx) {
        String getReviewImagesQuery = "select url\n" +
                "from ReviewImage\n" +
                "where ReviewImage.reviewIdx=?";
        List<String> images = this.jdbcTemplate.query(getReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("url")), reviewIdx);

        return images;
    }

    public List<String> getRestaurantImages(int restaurantIdx) {
        String getReviewImagesQuery = "";
        List<String> images = this.jdbcTemplate.query(getReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("url")), reviewIdx);

        return images;
    }

    public GetRestaurantInfo getRestaurantInfo(int restaurantIdx, int userIdx) {
        return this.jdbcTemplate.queryForObject("",
                (rs, rowNum) -> new GetReviewsDetail(
                        rs.getInt("restaurantIdx"),
                        rs.getString("name"),
                        getRestaurantImages(restaurantIdx),
                        rs.getFloat("rating"),
                        rs.getString("location"),
                        rs.getInt("views"),
                        rs.getInt("reviews"),
                        rs.getInt("wish"),
                        rs.getString("myWish"),
                        rs.getString("myBeen")),
                restaurantIdx);
    }
}