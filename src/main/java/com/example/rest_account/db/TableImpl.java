package com.example.rest_account.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class TableImpl implements Table<Entity> {

    private final AtomicLong counter = new AtomicLong();
    private final Map<Long,Entity> table = new ConcurrentHashMap<>();

    public Long nextId() { return counter.incrementAndGet(); }

    public void create(Entity entity) {
        if (entity.getId() == null) { entity.setId(counter.incrementAndGet()); }
        assert table.putIfAbsent(entity.getId(), entity.clone()) == null;
    }    
    public Entity read(Long id) { 
        Entity entity = table.get(id);
        return entity == null ? null : entity.clone(); 
    }

    private Entity checkedExistent(Entity entity) {
        Entity existent = table.get(entity.getId());
        if (existent == null || ! entity.getVersion().equals(existent.getVersion())) {
            throw new RuntimeException("Entity id '" + entity.getId() + "' doesn't exist or has deprecated version!");            
        }
        return existent;
    }

    public Entity update(Entity entity) {
        Entity existent = checkedExistent(entity);
        entity.setVersion(entity.getVersion() + 1L);
        if (! table.replace(entity.getId(), existent, entity.clone())) {            
            entity.setVersion(entity.getVersion() - 1L);
            throw new RuntimeException("Entity id '" + entity.getId() + "' updated by another thread!");            
        }
        return existent;
    }
    public Entity delete(Entity entity) {
        Entity existent = checkedExistent(entity);
        if (! table.remove(entity.getId(), existent)) { 
            throw new RuntimeException("Entity id '" + entity.getId() + "' updated by another thread!"); 
        }
        return existent;
    }
}