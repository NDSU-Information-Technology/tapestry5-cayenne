package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;

/**
 * Binding for ObjEntity.  Use as:
 * ent:XXX where XXX is the entity name. For example:
 * ent:User
 * @author robertz
 *
 */
public class ObjEntityBinding extends AbstractBinding {
    
    private final ObjEntity entity;
    private final String toString;
    
    public ObjEntityBinding(Location location, ObjEntity entity, String toString) {
        super(location);
        this.entity = entity;
        this.toString = toString;
    }

    public Object get() {
        return entity;
    }
    
    public String toString() {
        return toString;
    }
}
