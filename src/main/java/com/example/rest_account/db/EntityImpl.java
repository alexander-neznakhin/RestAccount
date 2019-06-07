package com.example.rest_account.db;

public class EntityImpl implements Entity {

    private Long id;
    private Long version;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setVersion(Long version) { this.version = version; }
    public Long getVersion() { return version; }

    public EntityImpl () {
        version = 0L;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Entity)) {
            return false;
        }

        Entity entity = (Entity) o;
        return (entity.getId() == null && id == null || entity.getId() != null && entity.getId().equals(id)) &&
                (entity.getVersion() == null && version == null || entity.getVersion() != null && entity.getVersion().equals(version));
    }

    @Override
    public int hashCode() {

        int result = 17;
        result = 31 * result + ((int)(id == null ? 0L : id.hashCode()));
        result = 31 * result + ((int)(version == null ? 0L : version.hashCode()));
        
        return result;
    }

    public Entity clone() {

        Entity clone = new EntityImpl();
        clone.setId(id);
        clone.setVersion(version);

        return clone;
    }
}