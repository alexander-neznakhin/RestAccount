package com.example.rest_account.db;

public interface Entity extends Cloneable {

    Long getId();
    void setId(Long id);

    Long getVersion();
    void setVersion(Long version);

    Entity clone();
}