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
        String getReviewImagesQuery = "select url\n" +
                "from ReviewImage join Review\n" +
                "where Review.restaurantIdx = ? and ReviewImage.reviewIdx=Review.idx;";
        List<String> images = this.jdbcTemplate.query(getReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("url")), restaurantIdx);

        return images;
    }

    public GetRestaurantInfo getRestaurantInfo(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select Restaurant.idx as restaurantIdx, name, rating, location,\n" +
                        "       (select COUNT(View.idx)\n" +
                        "       from View\n" +
                        "       where Restaurant.idx=View.idx) as views,\n" +
                        "       (select COUNT(Review.idx)\n" +
                        "           from Review\n" +
                        "           where Restaurant.idx=Review.restaurantIdx) as reviews,\n" +
                        "       (select COUNT(Wish.idx)\n" +
                        "           from Wish\n" +
                        "           where Restaurant.idx=Wish.restaurantIdx) as wish\n" +
                        "from Restaurant\n" +
                        "where Restaurant.idx=?;",
                (rs, rowNum) -> new GetRestaurantInfo(
                        rs.getInt("restaurantIdx"),
                        rs.getString("name"),
                        getRestaurantImages(restaurantIdx),
                        rs.getFloat("rating"),
                        rs.getString("location"),
                        rs.getInt("views"),
                        rs.getInt("reviews"),
                        rs.getInt("wish")),
                restaurantIdx);
    }

    public GetMyRestaurantInfo getMyRestaurantInfo(int restaurantIdx, int userIdx) {
        return this.jdbcTemplate.queryForObject("select (case\n" +
                        "    when (select exists(select Wish.idx from Wish where Wish.userIdx=?) = 0)\n" +
                        "    then 'F'\n" +
                        "    when (select exists(select Wish.idx from Wish where Wish.userIdx=?) = 1)\n" +
                        "    then 'T' end) as myWish,\n" +
                        "    (case\n" +
                        "        when (select COUNT(Went.idx)\n" +
                        "           from Went\n" +
                        "           where Went.restaurantIdx=Restaurant.idx and Went.userIdx=?) = 0\n" +
                        "        then '0'\n" +
                        "        else concat('x', (select COUNT(Went.idx)\n" +
                        "           from Went\n" +
                        "           where Went.restaurantIdx=Restaurant.idx and Went.userIdx=?)) end) as myBeen\n" +
                        "from Restaurant\n" +
                        "where Restaurant.idx=?;",
                (rs, rowNum) -> new GetMyRestaurantInfo(
                        rs.getString("myWish"),
                        rs.getString("myBeen")),
                userIdx, userIdx, userIdx, userIdx, restaurantIdx);
    }

    public GetOpenInfo getOpenInfo(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select date_format(Restaurant.updatedAt, '%Y-%m-%d') as updatedAt,\n" +
                        "       (select concat(start, '-', end) from BreakTime\n" +
                        "       where BreakTime.restaurantIdx=Restaurant.idx) as breakTime,\n" +
                        "       lastOrder, holiday,\n" +
                        "       (case\n" +
                        "           when (select avg(price) from Menu where Menu.restaurantIdx=Restaurant.idx) < 10000\n" +
                        "               then '만원 미만'\n" +
                        "           when (select avg(price) from Menu where Menu.restaurantIdx=Restaurant.idx)\n" +
                        "                then '만원-2만원'\n" +
                        "           when (select avg(price) from Menu where Menu.restaurantIdx=Restaurant.idx)\n" +
                        "                then '2만원-3만원'\n" +
                        "           when (select avg(price) from Menu where Menu.restaurantIdx=Restaurant.idx)\n" +
                        "                then '3만원-4만원'\n" +
                        "           else '4만원 이상' end) as price,\n" +
                        "       (select name\n" +
                        "           from RestaurantType where Restaurant.type=RestaurantType.idx) as type,\n" +
                        "       parking, website\n" +
                        "from Restaurant\n" +
                        "where Restaurant.idx=?;\n",
                (rs, rowNum) -> new GetOpenInfo(
                        rs.getString("updatedAt"),
                        getOpenTime(restaurantIdx),
                        rs.getString("breakTime"),
                        rs.getString("lastOrder"),
                        rs.getString("holiday"),
                        rs.getString("price"),
                        rs.getString("type"),
                        rs.getString("parking"),
                        rs.getString("website")),
                restaurantIdx);
    }

    public List<String> getOpenTime(int restaurantIdx) {
        String getReviewImagesQuery = "select concat(weekday, ' ', open, '-', close) as openTime\n" +
                "from OpenTime\n" +
                "where OpenTime.restaurantIdx=?;";
        List<String> images = this.jdbcTemplate.query(getReviewImagesQuery,
                (rs,rowNum) -> new String(rs.getString("openTime")), restaurantIdx);

        return images;
    }

    public GetMenu getMenu(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select date_format(min(updatedAt), '%Y-%m-%d') as updatedAt\n" +
                        "from Menu\n" +
                        "where Menu.restaurantIdx=?;",
                (rs, rowNum) -> new GetMenu(
                        rs.getString("updatedAt"),
                        getMenus(restaurantIdx)),
                restaurantIdx);
    }

    public List<MyMenu> getMenus(int restaurantIdx) {

        return this.jdbcTemplate.query("select name as menuName, format(price, 0) as price\n" +
                        "from Menu\n" +
                        "where Menu.restaurantIdx=?;",
                (rs, rowNum) -> new MyMenu(
                        rs.getString("menuName"),
                        rs.getString("price")), restaurantIdx
        );
    }

    public List<GetNearStoreRes> getNearStore(String location) {

        return this.jdbcTemplate.query("select idx as restaurantIdx, name, rating,\n" +
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
                        "where region=? order by rating desc limit 4;",
                (rs, rowNum) -> new GetNearStoreRes(
                        rs.getInt("restaurantIdx"),
                        rs.getString("name"),
                        rs.getString("profImg"),
                        rs.getFloat("rating"),
                        rs.getString("location"),
                        rs.getInt("views"),
                        rs.getInt("reviews")), location
        );
    }

    public List<GetKeyword> getKeyword(int restaurantIdx) {

        return this.jdbcTemplate.query("select content as keyword\n" +
                        "from Keyword\n" +
                        "where Keyword.restaurantIdx=?;",
                (rs, rowNum) -> new GetKeyword(
                        rs.getString("keyword")), restaurantIdx
        );
    }

    public GetReviewList getReviews(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select count(Review.idx) as total,\n" +
                        "       (select count(Review.idx) from Review\n" +
                        "        where Review.rateType=1) as good,\n" +
                        "       (select count(Review.idx) from Review\n" +
                        "        where Review.rateType=2) as soso,\n" +
                        "       (select count(Review.idx) from Review\n" +
                        "        where Review.rateType=3) as bad\n" +
                        "from Review\n" +
                        "where restaurantIdx=?;",
                (rs, rowNum) -> new GetReviewList(
                        rs.getInt("total"),
                        rs.getInt("good"),
                        rs.getInt("soso"),
                        rs.getInt("bad"),
                        getReviewList(restaurantIdx)),
                restaurantIdx);
    }

    public List<Reviews> getReviewList(int restaurantIdx) {

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
                        "where Review.restaurantIdx=? limit 3;",
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

    public String getLocation(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select region\n" +
                        "from Restaurant\n" +
                        "where idx=?;",
                (rs, rowNum) -> new String(
                        rs.getString("region")),
                restaurantIdx);
    }

    public int checkHeart(int userIdx, int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Wish where userIdx = ? and restaurantIdx = ?)",
                int.class,
                userIdx, restaurantIdx);
    }

    public int postHeart(int userIdx, int restaurantIdx) {
        this.jdbcTemplate.update("insert into Wish (userIdx, restaurantIdx) VALUE (?,?)",
                new Object[]{userIdx, restaurantIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int patchHeart (String status, int userIdx, int restaurantIdx) {
        this.jdbcTemplate.update("update Wish set status = ? where Wish.userIdx=? and Wish.restaurantIdx=?",
                new Object[]{status, userIdx, restaurantIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public String checkStatusHeart(int userIdx, int itemIdx) {
        return this.jdbcTemplate.queryForObject("select status from Wish where userIdx = ? and restaurantIdx = ?;" ,
                (rs, rowNum) -> new String(
                        rs.getString("status")),
                userIdx, itemIdx);
    }

    public int postWent(int userIdx, int restaurantIdx, String status, String content) {
        this.jdbcTemplate.update("insert into Went (userIdx, restaurantIdx, public, content) VALUE (?,?,?,?)",
                new Object[]{userIdx, restaurantIdx, status, content}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }
}