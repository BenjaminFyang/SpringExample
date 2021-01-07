package io.seata.sample.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class StorageService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void deduct(String commodityCode, int count) {
        jdbcTemplate.update("update storage_tbl set count = count - ? where commodity_code = ?", new Object[]{count, commodityCode});
    }
}
