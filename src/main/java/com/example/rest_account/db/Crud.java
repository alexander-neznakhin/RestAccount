package com.example.rest_account.db;

interface Crud<T> {
    
    void create(T entity);
    T read(Class<? extends T> clazz, Long id);
    T update(T entity);
    T delete(Entity entity);
}