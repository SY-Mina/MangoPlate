package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select User.idx as userIdx, userName,\n" +
                "    profImg, User.email, User.phoneNum,\n" +
                "        (select COUNT(Follow.idx)\n" +
                "           from Follow\n" +
                "           where Follow.followIdx=User.idx) as follower,\n" +
                "        (select COUNT(Follow.idx)\n" +
                "           from Follow\n" +
                "           where Follow.userIdx=User.idx) as following,\n" +
                "        (select COUNT(Review.idx)\n" +
                "            from Review\n" +
                "            where Review.userIdx=User.idx) as reviews,\n" +
                "        (select COUNT(Went.idx)\n" +
                "            from Went\n" +
                "            where Went.userIdx=User.idx) as went,\n" +
                "        (select COUNT(Review.idx)\n" +
                "            from Review inner join ReviewImage\n" +
                "            where Review.userIdx=User.idx and ReviewImage.reviewIdx=Review.idx) as photos,\n" +
                "        (select COUNT(Wish.idx)\n" +
                "            from Wish\n" +
                "            where Wish.userIdx=User.idx) as wish\n" +
                "from User\n" +
                "where User.idx=3;";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("profImg"),
                        rs.getString("email"),
                        rs.getString("phoneNum"),
                        rs.getInt("follower"),
                        rs.getInt("following"),
                        rs.getInt("reviews"),
                        rs.getInt("went"),
                        rs.getInt("photos"),
                        rs.getInt("wish"))
                );
    }

//    public List<GetUserRes> getUsersByEmail(String email){
//        String getUsersByEmailQuery = "select * from User where email =?";
//        String getUsersByEmailParams = email;
//        return this.jdbcTemplate.query(getUsersByEmailQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("userName"),
//                        rs.getString("ID"),
//                        rs.getString("Email"),
//                        rs.getString("password")),
//                getUsersByEmailParams);
//    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select User.idx as userIdx, userName,\n" +
                "    profImg, User.email, User.phoneNum,\n" +
                "        (select COUNT(Follow.idx)\n" +
                "           from Follow\n" +
                "           where Follow.followIdx=User.idx) as follower,\n" +
                "        (select COUNT(Follow.idx)\n" +
                "           from Follow\n" +
                "           where Follow.userIdx=User.idx) as following,\n" +
                "        (select COUNT(Review.idx)\n" +
                "            from Review\n" +
                "            where Review.userIdx=User.idx) as reviews,\n" +
                "        (select COUNT(Went.idx)\n" +
                "            from Went\n" +
                "            where Went.userIdx=User.idx) as went,\n" +
                "        (select COUNT(Review.idx)\n" +
                "            from Review inner join ReviewImage\n" +
                "            where Review.userIdx=User.idx and ReviewImage.reviewIdx=Review.idx) as photos,\n" +
                "        (select COUNT(Wish.idx)\n" +
                "            from Wish\n" +
                "            where Wish.userIdx=User.idx) as wish\n" +
                "from User\n" +
                "where User.idx=?;";
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("profImg"),
                        rs.getString("email"),
                        rs.getString("phoneNum"),
                        rs.getInt("follower"),
                        rs.getInt("following"),
                        rs.getInt("reviews"),
                        rs.getInt("went"),
                        rs.getInt("photos"),
                        rs.getInt("wish")),
                userIdx);
    }


    public List<RecommendUser> getRecommendUsers(int userIdx){
        String getUsersQuery = "select distinct User.idx as userIdx, userName, profImg,\n" +
                "        holic21,\n" +
                "       (select count(Review.idx) from Review\n" +
                "        where Review.userIdx=User.idx) as reviews,\n" +
                "       (select count(Follow.idx) from Follow\n" +
                "        where Follow.userIdx=User.idx) as follower,\n" +
                "        (case (exists(select Follow.idx\n" +
                "           from Follow\n" +
                "           where Follow.followIdx=? and Follow.userIdx=User.idx))\n" +
                "            when 1 then 'T'\n" +
                "            else 'F' end) as followed\n" +
                "from User inner join Follow\n" +
                "where User.idx not in (?);";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new RecommendUser(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("profImg"),
                        rs.getString("holic21"),
                        rs.getInt("reviews"),
                        rs.getInt("follower"),
                        rs.getString("followed")),
                userIdx, userIdx);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (userName, password, email, phoneNum) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserName(), postUserReq.getPassword(), postUserReq.getEmail(), postUserReq.getPhoneNum()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int checkUserExist(int userIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from User where idx=?);",
                int.class, userIdx);
    }

    public int modifyUserName(String userName, int userIdx){
        String modifyUserNameQuery = "update User set userName = ? where idx = ? ";
        Object[] modifyUserNameParams = new Object[]{userName, userIdx};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select idx, userName,password,email from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("idx"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("email")
                ),
                getPwdParams
                );

    }

    public int checkKakaoUserExist (String nickname, String email) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from User where userName=? and email=?);",
                int.class, nickname, email);
    }

    public int getKakaoUser(String nickname, String email){

        return this.jdbcTemplate.queryForObject("select idx from User where userName = ? and email=?",
                int.class, nickname, email);

    }

    public int createKakaoUser(String nickname, String email){
        String kakaoPwd = "kakao";
        this.jdbcTemplate.update("insert into User (userName, email, password) VALUES (?,?,?)",
                new Object[]{nickname, email, kakaoPwd}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int checkFollow(int userIdx, int followIdx) {
        return this.jdbcTemplate.queryForObject("select exists(select idx from Follow where userIdx = ? and followIdx = ?)",
                int.class,
                userIdx, followIdx);
    }
    public int postFollow(int userIdx, int followIdx) {
        this.jdbcTemplate.update("insert into Follow (userIdx, followIdx) VALUE (?,?)",
                new Object[]{userIdx, followIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public String checkStatusFollow(int userIdx, int followIdx) {
        return this.jdbcTemplate.queryForObject("select status from Follow where userIdx = ? and followIdx = ?;" ,
                (rs, rowNum) -> new String(
                        rs.getString("status")),
                userIdx, followIdx);
    }

    public int patchFollow (String status, int userIdx, int followIdx) {
        this.jdbcTemplate.update("update Follow set status = ? where Follow.userIdx=? and Follow.followIdx=?",
                new Object[]{status, userIdx, followIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int patchProfImg(int userIdx,String profImg) {
        this.jdbcTemplate.update("update User set profImg = ? where User.idx=?",
                new Object[]{profImg, userIdx}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }
}
