package com.example.rest_account.db;

public interface Transaction<T> extends Crud<T>{
 
    Transaction<T> begin();
    Transaction<T> commit();
    Transaction<T> rallBack();
    
    boolean isActive();
}