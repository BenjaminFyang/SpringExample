package io.seata.sample.controller;

import io.seata.sample.service.BusinessService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class BusinessController {

    @Resource
    private BusinessService businessService;

    /**
     * 购买下单，模拟全局事务提交
     *
     * @return the String
     */
    @RequestMapping(value = "/purchase/commit", produces = "application/json")
    public String purchaseCommit() {
        try {
            businessService.purchase("U100000", "C100000", 30);
        } catch (Exception exx) {
            return exx.getMessage();
        }
        return "全局事务提交";
    }

    /**
     * 购买下单，模拟全局事务回滚
     * 账户或库存不足
     *
     * @return the String
     */
    @RequestMapping("/purchase/rollback")
    public String purchaseRollback() {
        try {
            businessService.purchase("U100000", "C100000", 99999);
        } catch (Exception exx) {
            return exx.getMessage();
        }
        return "全局事务提交";
    }
}
