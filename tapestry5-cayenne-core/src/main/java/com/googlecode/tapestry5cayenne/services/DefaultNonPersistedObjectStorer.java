package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.Persistent;
import org.apache.commons.collections.map.LRUMap;
import org.apache.tapestry5.ioc.annotations.Symbol;

import java.util.Map;

/**
 * Simple implementation of NonPersistedObjectStorer.
 * Keeps an application-wide (LRU) map of transient objects.
 * Keys off the hashcode of the object.
 * @author robertz
 */
public class DefaultNonPersistedObjectStorer implements NonPersistedObjectStorer {
    
    private final Map<String,Persistent> _objs;
    
    @SuppressWarnings("unchecked")
    public DefaultNonPersistedObjectStorer(
            @Symbol(TapestryCayenneCoreModule.UNPERSISTED_OBJECT_LIMIT)
            int limit) {
        _objs = new LRUMap(limit);
    }

    public String store(Persistent dao) {
        String key = Integer.toString(dao.hashCode());
        _objs.put(key, dao);
        return key;
    }

    public Persistent retrieve(String key,String objEntityName) {
        return _objs.get(key);
    }
    
}
