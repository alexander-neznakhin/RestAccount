package com.example.rest_account.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
/**
 * Уровень изоляции транзакций: "Read uncommited".
 * Защита от "Потеряного обновления" -- оптимистичные блокировки (версионность). 
 * Объекты в таблицах - "immutable". 
 */
class DbImpl implements Db<Entity> {
 
    private static DbImpl instance;
    private final Map<Class<? extends Entity>,Table<Entity>> tableMap = new ConcurrentHashMap<>();
    private volatile boolean underCommit = false;
    private final AtomicLong readersCounter = new AtomicLong();

    public static DbImpl getInstance() { return instance == null ? instance = new DbImpl() : instance; }
    private DbImpl() {}

    private Table<Entity> getTable(Class<? extends Entity> clazz) {

        Table<Entity> table = tableMap.get(clazz);
        if (table == null) {
            tableMap.putIfAbsent(clazz, new TableImpl());
            table = tableMap.get(clazz);
        }
        return table;
    }

    public Long nextId(Class<? extends Entity> clazz) { return getTable(clazz).nextId(); }

    public void create(Entity entity) {
        getTable(entity.getClass()).create(entity);
    }
    public Entity read(Class<? extends Entity> clazz, Long id) {
        return getTable(clazz).read(id);
    }
    public Entity update(Entity entity) {
        return getTable(entity.getClass()).update(entity);
    }
    public Entity delete(Entity entity) {
        return getTable(entity.getClass()).delete(entity);
    }

    public void setUnderCommit(boolean underCommit) { this.underCommit = underCommit; };
    public boolean underCommit() { return underCommit; };

    public void readersInc() { readersCounter.incrementAndGet(); }
    public void readersDec() { readersCounter.decrementAndGet(); }
    public boolean underReading() { return readersCounter.get() == 0L; }
}