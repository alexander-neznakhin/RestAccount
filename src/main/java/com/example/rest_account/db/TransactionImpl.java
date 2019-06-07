package com.example.rest_account.db;

import java.util.HashMap;
import java.util.Map;
/**
 * Уровень изоляции транзакций: "Read commited".
 */
public class TransactionImpl implements Transaction<Entity> {
     
    private final Db<Entity> db;    
    private final Map<Long,Entity> createMap = new HashMap<>();
    private final Map<Long,Entity> updateMap = new HashMap<>();
    private final Map<Long,Entity> deleteMap = new HashMap<>();
    private boolean isActive = true;

    TransactionImpl(Db<Entity> db) { this.db = db; }

    public void create(Entity entity) { 
        assert isActive && entity.getId() == null; 
        entity.setId(db.nextId(entity.getClass()));
        createMap.put(entity.getId(), entity);
    }

    public Entity read(Class<? extends Entity> clazz, Long id) {        
        assert isActive;
        Entity entity = createMap.get(id);
        return entity != null ? entity :
            (entity = updateMap.get(id)) != null ? entity : 
            deleteMap.containsKey(id) ? null :
            db.read(clazz, id);
    }

    public Entity update(Entity entity) {
        assert isActive && entity.getId() != null && !deleteMap.containsKey(entity.getId());
        Entity existent = createMap.replace(entity.getId(), entity); 
        if (existent == null) { 
           updateMap.put(entity.getId(), entity); 
        }
        return existent;
    }

    public Entity delete(Entity entity) {
        assert isActive && entity.getId() != null && ! deleteMap.containsKey(entity.getId());
        Entity existent = createMap.remove(entity.getId());
        if (existent == null) { 
           updateMap.remove(entity.getId());
           deleteMap.put(entity.getId(), entity); 
        }
        return existent;
    }

    public boolean isActive() { return isActive; }


    private void clear() {

        createMap.clear();
        updateMap.clear();
        deleteMap.clear();
    }

    public Transaction<Entity> begin() {
        clear();
        isActive = true;
        return this;
    }

    public Transaction<Entity> commit() {
        TransactionEngine.commit(db, createMap, updateMap, deleteMap);
        isActive = false;
        return this;
    }

    public Transaction<Entity> rallBack() {
        TransactionEngine.rollBack(db, createMap, updateMap, deleteMap);
        isActive = false;
        return this;
    }
}