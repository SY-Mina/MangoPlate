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
        String getUsersQuery = "select User.idx as userIdx, userName,\\n\" +\n" +
                "                \"    profImg,\\n\" +\n" +
                "                \"        (select COUNT(Follow.idx)\\n\" +\n" +
                "                \"           from Follow\\n\" +\n" +
                "                \"           where Follow.followIdx=User.idx) as follower,\\n\" +\n" +
                "                \"        (select COUNT(Follow.idx)\\n\" +\n" +
                "                \"           from Follow\\n\" +\n" +
                "                \"           where Follow.userIdx=User.idx) as following,\\n\" +\n" +
                "                \"        (select COUNT(Review.idx)\\n\" +\n" +
                "                \"            from Review\\n\" +\n" +
                "                \"            where Review.userIdx=User.idx) as reviews,\\n\" +\n" +
                "                \"        (select COUNT(Went.idx)\\n\" +\n" +
                "                \"            from Went\\n\" +\n" +
                "                \"            where Went.userIdx=User.idx) as went,\\n\" +\n" +
                "                \"        (select COUNT(Review.idx)\\n\" +\n" +
                "                \"            from Review inner join ReviewImage\\n\" +\n" +
                "                \"            where Review.userIdx=User.idx and ReviewImage.reviewIdx=Review.idx) as photos,\\n\" +\n" +
                "                \"        (select COUNT(Wish.idx)\\n\" +\n" +
                "                \"            from Wish\\n\" +\n" +
                "                \"            where Wish.userIdx=User.idx) as wish\\n\" +\n" +
                "                \"from User\\n\" +\n" +
                "                \"where User.idx=?;";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("profImg"),
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
                "    profImg,\n" +
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
                        rs.getInt("follower"),
                        rs.getInt("following"),
                        rs.getInt("reviews"),
                        rs.getInt("went"),
                        rs.getInt("photos"),
                        rs.getInt("wish")),
                userIdx);
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

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update UserInfo set userName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

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



}
