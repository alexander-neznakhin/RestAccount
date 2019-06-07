package com.example.rest_account.service;

import java.math.BigDecimal;
import com.example.rest_account.model.Account;

public interface AccountService {

    Account createAccount(String name);
    void chargeAccount(Long id, BigDecimal sum);
    void transfer(Long fromId, Long toId, BigDecimal sum);
}