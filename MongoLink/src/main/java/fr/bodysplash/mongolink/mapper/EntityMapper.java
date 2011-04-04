package fr.bodysplash.mongolink.mapper;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class EntityMapper<T> extends Mapper<T> {

    EntityMapper(Class<T> persistentType) {
        super(persistentType);
    }

    @Override
    protected void doPopulate(T instance, DBObject from) {
        populateId(instance, from);
    }

    public void populateId(Object element, DBObject dbObject) {
        idMapper.populateFrom(element, dbObject);
    }

    @Override
    protected void doSave(Object element, BasicDBObject object) {
        idMapper.saveTo(element, object);
    }

    private boolean hasId() {
        return idMapper != null;
    }

    public void setId(IdMapper idMapper) {
        idMapper.setMapper(this);
        this.idMapper = idMapper;
    }

    public Object getDbId(String id) {
        return idMapper.getDbValue(id);
    }

    private IdMapper idMapper;
}
