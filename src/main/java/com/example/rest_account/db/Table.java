package com.example.rest_account.db;

interface Table<T> {

    Long nextId();
    
    void create(T entity);
    T read(Long id);
    T update(T entity);
    T delete(T entity);
}