package com.example.rest_account.service;

import java.math.BigDecimal;

import com.example.rest_account.db.Session;
import com.example.rest_account.model.Account;
import com.example.rest_account.model.AccountImpl;

public class AccountServiceImpl implements AccountService {

    private final Session session;

    public AccountServiceImpl(Session session) {
        this.session = session;
    }

    public Account createAccount(String name) {
        Account account = new AccountImpl();
        account.setBalance(BigDecimal.ZERO);
        session.save(account);
        return account;
    }

    public void chargeAccount(Long id, BigDecimal sum) {
        try {
            session.beginTransaction(); 

            Account account = (Account) session.select(AccountImpl.class, id);
            if (account == null) {
                throw new RuntimeException("Account id:" + id + " not found");
            }
            BigDecimal balance = account.getBalance();
            BigDecimal newBalance = balance.add(sum);
            account.setBalance(newBalance);
 
            session.save(account);
            session.getTransaction().commit();
        }
        catch (Throwable e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rallBack();
            }
            throw e;
        }
    }

    public void transfer(Long fromId, Long toId, BigDecimal sum) {
        try {
            if (sum.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Transfer sum above zero!");
            }

            session.beginTransaction(); 

            Account accountFrom = (Account) session.select(AccountImpl.class, fromId);
            if (accountFrom == null) {
                throw new RuntimeException("Account id:" + fromId + " not found");
            }
            Account accountTo = (Account) session.select(AccountImpl.class, toId);
            if (accountTo == null) {
                throw new RuntimeException("Account id:" + toId + " not found");
            }

            BigDecimal fromBalance = accountFrom.getBalance();
            BigDecimal toBalance = accountTo.getBalance();
            BigDecimal newFromBalance = fromBalance.subtract(sum);
            BigDecimal newToBalance = toBalance.add(sum);

            if (newFromBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Unsufficient balance!");
            }

            accountFrom.setBalance(newFromBalance);
            accountTo.setBalance(newToBalance);
 
            session.save(accountFrom);
            session.save(accountTo);
            session.getTransaction().commit();
        }
        catch (Throwable e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rallBack();
            }
            throw e;
        }
    }
}