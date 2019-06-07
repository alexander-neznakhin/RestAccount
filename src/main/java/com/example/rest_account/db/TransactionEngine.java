package com.example.rest_account.db;

import java.util.HashMap;
import java.util.Map;

class TransactionEngine {

    static void commit(Db<Entity> db, 
            Map<Long,Entity> createMap, 
            Map<Long,Entity> updateMap, 
            Map<Long,Entity> deleteMap ) {

        Map<Long,Entity> rollBackCreateMap = null;
        Map<Long,Entity> rollBackUpdateMap = null;
        Map<Long,Entity> rollBackDeleteMap = null;

        synchronized(db) {
            db.setUnderCommit(true);
            try {
                while (db.underReading()) { db.wait(); }
                rollBackCreateMap = commitCreate(db, createMap);
                rollBackUpdateMap = commitUpdate(db, updateMap);
                rollBackDeleteMap = commitDelete(db, deleteMap);
            }
            catch (Throwable e) {
                if (rollBackCreateMap != null) { rollBackCreate(db, rollBackCreateMap); }
                if (rollBackUpdateMap != null) { rollBackUpdate(db, rollBackUpdateMap); }
                if (rollBackDeleteMap != null) { rollBackDelete(db, rollBackDeleteMap); }
                throw new RuntimeException(e);
            }
            finally {
                db.setUnderCommit(false);
                db.notifyAll();
            }
        }
        db.notifyAll();
    } 

    static void rollBack(Db<Entity> db, 
            Map<Long,Entity> createMap, 
            Map<Long,Entity> updateMap, 
            Map<Long,Entity> deleteMap ) {} 

    private static void rollBackCreate(Db<Entity> db, Map<Long,Entity> rollBackMap) {
        for (Map.Entry<Long, Entity> entry : rollBackMap.entrySet()) {
            db.delete(entry.getValue());
        }    
    }
    private static Map<Long,Entity> commitCreate(Db<Entity> db, Map<Long,Entity> createMap) {
        Map<Long,Entity>rollBackMap = new HashMap<>();
        try {
            for (Map.Entry<Long,Entity> entry : createMap.entrySet()) {
                db.create(entry.getValue());
                rollBackMap.put(entry.getKey(), entry.getValue());
            }    
        }
        catch (Throwable e) {
            rollBackCreate(db, rollBackMap);
            throw e;
        }
        return rollBackMap;
    }

    private static void rollBackUpdate(Db<Entity> db, Map<Long,Entity> rollBackMap) {
        for (Map.Entry<Long,Entity> entry : rollBackMap.entrySet()) {
            db.update(entry.getValue());
        }    
    }
    private static Map<Long,Entity> commitUpdate(Db<Entity> db, Map<Long,Entity> updateMap) {
        Map<Long,Entity> rollBackMap = new HashMap<>();
        try {
            for (Map.Entry<Long,Entity> entry : updateMap.entrySet()) {
                Entity existent = db.update(entry.getValue());
                rollBackMap.put(entry.getKey(), existent);
            }    
        }
        catch (Throwable e) {
            rollBackUpdate(db, rollBackMap);
            throw e;
        }
        return rollBackMap;
    }

    private static void rollBackDelete(Db<Entity> db, Map<Long,Entity> rollBackMap) {
        for (Map.Entry<Long, Entity> entry : rollBackMap.entrySet()) {
            db.create(entry.getValue());
        }    
    }
    private static Map<Long,Entity> commitDelete(Db<Entity> db, Map<Long, Entity> deleteMap) {
        Map<Long,Entity> rollBackMap = new HashMap<>();
        try {
            for (Map.Entry<Long, Entity> entry : deleteMap.entrySet()) {
                db.delete(entry.getValue());
                rollBackMap.put(entry.getKey(), entry.getValue());
            }    
        }
        catch (Throwable e) {
            rollBackDelete(db, rollBackMap);
            throw e;
        }
        return rollBackMap;
    }
}
     