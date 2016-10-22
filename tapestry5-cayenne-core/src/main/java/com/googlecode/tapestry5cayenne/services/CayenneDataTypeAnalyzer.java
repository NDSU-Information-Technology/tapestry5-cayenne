package com.googlecode.tapestry5cayenne.services;

import java.sql.Types;
import java.util.Collection;
import java.util.Map;

import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.tapestry5.beaneditor.DataType;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.services.PropertyAdapter;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.apache.tapestry5.services.Environment;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.internal.BeanModelTypeHolder;

/**
 * DataTypeAnalyzer to handle cayenne properties. In particular, 
 * recognizes and assigns a datatype to toOne and toMany relationships.
 * @author robertz
 *
 */
@Marker(Cayenne.class)
public class CayenneDataTypeAnalyzer implements DataTypeAnalyzer {
    
    private final ObjectContextProvider _provider;
    private final Environment _environment;
    private final DataTypeAnalyzer _defaultAnalyzer;
    
    public CayenneDataTypeAnalyzer(
            final ObjectContextProvider provider, 
            final Environment environment,
            final @InjectService("DefaultDataTypeAnalyzer") DataTypeAnalyzer analyzer) 
    {
        _provider = provider;
        _environment = environment;
        _defaultAnalyzer = analyzer;
    }
    
    public String identifyDataType(PropertyAdapter adapter)
    {
        EntityResolver er = _provider.currentContext().getEntityResolver();
        Class<?> type = _environment.peek(BeanModelTypeHolder.class).getType();
        ObjEntity ent = er.lookupObjEntity(type);

        String dt = _defaultAnalyzer.identifyDataType(adapter);

        //fixes googlecode issue #39: unable to use POJO w/ String properties w/ t5cayenne.
        if (ent == null)
            return dt;

        if (dt != null && !dt.equals("")) {
            return checkLongText(ent,adapter,dt);
        }
        
        //if we add any more "special cases" for datatypes, we should refactor this into a chain of command
        //that passes in the object entity and the adapter. Then we can easily contribute new types.
        //cleans up the code, too. But for only two main cases...
        return checkRelationship(ent,adapter);
        
    }
    
    private String checkRelationship(ObjEntity ent,PropertyAdapter adapter) {
        ObjRelationship rel = (ObjRelationship) ent.getRelationship(adapter.getName());

        if (rel == null) { 
            return null;
        }

        if (rel.isToMany()) {
            Class<?> clazz;
            try {
              clazz = Class.forName(rel.getCollectionType());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (Collection.class.isAssignableFrom(clazz)) {
                return "to_many_collection";
            } else if (Map.class.isAssignableFrom(clazz)) {
                return "to_many_map";
            } else {
                throw new UnsupportedOperationException(rel.getCollectionType());
            }
        }

        return "to_one";
    }
    
    private String checkLongText(ObjEntity ent, PropertyAdapter adapter, String currentType) {
        //only called if currentType isn't null, so...
        //first check to make sure we don't have an explicitly declared data type.
        if (adapter.getAnnotation(DataType.class) != null) {
            return currentType;
        }
        
        //only really care, currently, if currentType is text.
        if (!currentType.equals("text")) {
            return currentType;
        }
        
        ObjAttribute att = (ObjAttribute) ent.getAttribute(adapter.getName());
        if (att == null) return currentType;
        
        DbAttribute dbatt = att.getDbAttribute();
        switch (dbatt.getType()) {
            case Types.LONGVARCHAR:
                return "longtext";
            case Types.CHAR:
            case Types.VARCHAR:
                if (dbatt.getMaxLength() > 64) {
                    return "longtext";
                }
            default:
                return currentType;
        }
    }
}
