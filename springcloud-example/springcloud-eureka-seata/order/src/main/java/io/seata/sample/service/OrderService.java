package io.seata.sample.service;

import io.seata.sample.feign.UserFeignClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class OrderService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void create(String userId, String commodityCode, Integer count) {

        int orderMoney = count * 100;
        jdbcTemplate.update("insert order_tbl(user_id,commodity_code,count,money) values(?,?,?,?)",
                new Object[]{userId, commodityCode, count, orderMoney});

        userFeignClient.reduce(userId, orderMoney);

    }
}
