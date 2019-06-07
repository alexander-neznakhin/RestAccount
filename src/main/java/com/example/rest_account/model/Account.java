package com.example.rest_account.model;

import java.math.BigDecimal;

import com.example.rest_account.db.Entity;

public interface Account extends Entity {

    void setName(String name);
    String getName();

    void setBalance(BigDecimal balance);
    BigDecimal getBalance();
}