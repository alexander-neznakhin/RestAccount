package com.example.rest_account.db;

interface Db<T> extends Crud<T> {

    Long nextId(Class<? extends T> clazz);

    void setUnderCommit(boolean underCommit);
    boolean underCommit();

    void readersInc();
    void readersDec();
    boolean underReading();
}