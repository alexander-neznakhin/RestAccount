package com.example.rest_account.db;
/**
 * Защита от "Грязного чтения" (уровень изоляции "Read commited") за счет хранения измененний в транзакции.
 */
public class SessionImpl implements Session {

    private final Db<Entity> db = DbImpl.getInstance();
    private Transaction<Entity> transaction;

    public Session beginTransaction() {
        transaction = transaction == null ? new TransactionImpl(db) : transaction.begin(); 
        return this;
    }
    public Transaction<Entity> getTransaction() {
        return transaction;
    }

    private boolean transacted() {
        return transaction != null && transaction.isActive();
    }
    private Crud<Entity> getCrud() {
        return transacted() ? transaction : db;
    } 

    public Session save(Entity entity) {
        Crud<Entity> crud = getCrud();
        if (entity.getId() == null) {
            crud.create(entity);
        }
        else {
            crud.update(entity);
        }
        return this;
    }
    public Entity select(Class<? extends Entity> clazz, Long id) {
        Crud<Entity> crud;
        db.readersInc();
        try { 
            while (db.underCommit()) { 
                db.readersDec();
                db.notifyAll(); 
                db.wait(); 
                db.readersInc();
            }
            crud = getCrud();
        } 
        catch (InterruptedException e) { 
            throw new RuntimeException(e);
        }
        finally {
            db.readersDec();
            db.notifyAll();
        }
        return crud.read(clazz, id);
    }
    public Session delete(Entity entity) {
        Crud<Entity> crud = getCrud();
        crud.delete(entity);
        return this;
    }
}