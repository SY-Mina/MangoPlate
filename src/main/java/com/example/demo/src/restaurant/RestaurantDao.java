package com.example.demo.src.restaurant;


import com.example.demo.src.restaurant.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RestaurantDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetRestaurantRes> getRestaurants(){
        String getRestaurantQuery = "select idx as restaurantIdx, name, rating, location,\n" +
                "       (select COUNT(View.idx)\n" +
                "       from View\n" +
                "       where Restaurant.idx=View.idx) as views,\n" +
                "       (select COUNT(Review.idx)\n" +
                "           from Review\n" +
                "           where Restaurant.idx=Review.restaurantIdx) as reviews\n" +
                "\n" +
                "from Restaurant\n" +
                "order by rating desc;";
        return this.jdbcTemplate.query(getRestaurantQuery,
                (rs,rowNum) -> new GetRestaurantRes(
                        rs.getInt("restaurantIdx"),
                        rs.getString("name"),
                        rs.getInt("rating"),
                        rs.getString("location"),
                        rs.getInt("views"),
                        rs.getInt("reviews"))
        );
    }


}
