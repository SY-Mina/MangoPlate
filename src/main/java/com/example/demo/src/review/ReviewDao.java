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
                "       (select region from Restaurant where Restaurant.idx=restaurantIdx) as location,content,\n" +
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
                "where Review.rateType=? and Review.status='T';";
         return this.jdbcTemplate.query(getReviewImagesQuery,
                    (rs,rowNum) -> new GetReviewsRes(
                            rs.getString("type"),
                            rs.getInt("reviewIdx"),
                            rs.getInt("restaurantIdx"),
                            rs.getString("store"),
                            rs.getString("location"),
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

    public int postReview(int userIdx, int restaurantIdx, int rateType, String content) {
        this.jdbcTemplate.update("insert into Review (userIdx, restaurantIdx, rateType, content) VALUE (?,?,?,?)",
                new Object[]{userIdx, restaurantIdx, rateType, content}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int checkItemExist(int restaurantIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Restaurant where idx=?)",
                int.class,
                restaurantIdx);
    }

    public int checkReviewExist(int reviewIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Review where idx=?)",
                int.class,
                reviewIdx);
    }

    public int postReviewImages(int reviewIdx, String url) {
        this.jdbcTemplate.update("insert into ReviewImage (reviewIdx, url) VALUE (?,?)",
                new Object[]{reviewIdx, url}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int checkStatus (int reviewIdx, int userIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Review where idx = ? and userIdx = ?)",
                int.class,
                reviewIdx, userIdx);
    }

    public int patchItemStatus (int reviewIdx,int userIdx) {
        this.jdbcTemplate.update("update Review set status = 'F' where Review.idx = ? and Review.userIdx=?",
                new Object[]{reviewIdx, userIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int patchReviewRate (int reviewIdx,int rateType) {
        this.jdbcTemplate.update("update Review set rateType = ? where Review.idx = ?",
                new Object[]{rateType, reviewIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int patchReviewContent (int reviewIdx,String content) {
        this.jdbcTemplate.update("update Review set content = ? where Review.idx = ?",
                new Object[]{content, reviewIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int patchReviewContentRate (int reviewIdx,String content, int rateType) {
        this.jdbcTemplate.update("update Review set content = ?, rateType=? where Review.idx = ?",
                new Object[]{content, rateType, reviewIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int checkHeart(int userIdx, int reviewIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Heart where userIdx = ? and reviewIdx = ?)",
                int.class,
                userIdx, reviewIdx);
    }

    public String checkStatusHeart(int userIdx, int reviewIdx) {
        return this.jdbcTemplate.queryForObject("select status from Heart where userIdx = ? and reviewIdx = ?;" ,
                (rs, rowNum) -> new String(
                        rs.getString("status")),
                userIdx, reviewIdx);
    }

    public int postHeart(int userIdx, int reviewIdx) {
        this.jdbcTemplate.update("insert into Heart (userIdx, reviewIdx) VALUE (?,?)",
                new Object[]{userIdx, reviewIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int patchHeart (String status, int userIdx, int reviewIdx) {
        this.jdbcTemplate.update("update Heart set status = ? where Heart.userIdx=? and Heart.reviewIdx=?",
                new Object[]{status, userIdx, reviewIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }


    public int postCommentMention(int userIdx, int reviewIdx, int mentionIdx, String content) {
        this.jdbcTemplate.update("insert into ReviewComment (userIdx, reviewIdx, mentionIdx, content) VALUE (?,?,?,?)",
                new Object[]{userIdx, reviewIdx, mentionIdx, content}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }


    public int postComment(int userIdx, int reviewIdx, String content) {
        this.jdbcTemplate.update("insert into ReviewComment (userIdx, reviewIdx, content) VALUE (?,?,?)",
                new Object[]{userIdx, reviewIdx, content}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int checkReviewMention(int reviewIdx, int mentionIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Review where idx=? and userIdx=?);",
                int.class,
                reviewIdx, mentionIdx);
    }

    public int checkCommentMention(int reviewIdx, int mentionIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from ReviewComment where reviewIdx=? and userIdx=?);",
                int.class,
                reviewIdx, mentionIdx);
    }
}