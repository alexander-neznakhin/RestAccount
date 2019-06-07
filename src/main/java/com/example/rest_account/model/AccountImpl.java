package com.example.rest_account.model;

import java.math.BigDecimal;

import com.example.rest_account.db.EntityImpl;

public class AccountImpl extends EntityImpl implements Account {

    private String name;
    private BigDecimal balance;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getBalance() { return balance; }
}