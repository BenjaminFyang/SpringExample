package io.seata.sample.controller;

import io.seata.sample.service.AccountService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
public class AccountController {

    @Resource
    private AccountService accountService;

    @RequestMapping(value = "/reduce", produces = "application/json")
    public Boolean debit(String userId, int money) {
        accountService.reduce(userId, money);
        return true;
    }
}
