package fr.bodysplash.mongolink.domain.mapper;

import com.mongodb.DBObject;

public interface Mapper {

    void save(Object instance, DBObject into);

    void populate(Object instance, DBObject from);
}
