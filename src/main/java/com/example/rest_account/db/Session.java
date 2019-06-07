package com.example.rest_account.db;

public interface Session {
    
    Session beginTransaction();
    Transaction<Entity> getTransaction();

    Session save(Entity entity);
    Entity select(Class<? extends Entity> clazz, Long id);
    Session delete(Entity entity);
}