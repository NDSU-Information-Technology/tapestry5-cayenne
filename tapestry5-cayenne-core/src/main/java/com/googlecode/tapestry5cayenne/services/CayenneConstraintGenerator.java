package com.googlecode.tapestry5cayenne.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.map.DbAttribute;
import org.apache.cayenne.map.DbRelationship;
import org.apache.cayenne.map.EntityResolver;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.map.ObjRelationship;
import org.apache.tapestry5.ioc.AnnotationProvider;
import org.apache.tapestry5.services.BeanEditContext;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.ValidationConstraintGenerator;

/**
 * ValidationConstraintGenerator that uses information available from the
 * cayenne mapping file to determine the proper set of constraints to apply.
 * Currently, "mandatory" (for both attributes and foreign keys) and
 * maxlength (for string-based properties: things that wind up as a CharSequence in the ObjEntity)
 * are supported.
 * @author robertz
 *
 */
public class CayenneConstraintGenerator implements
        ValidationConstraintGenerator {
    
    private final ObjectContextProvider provider;
    private Environment environment;
    
    public CayenneConstraintGenerator(
            final Environment environment,
            final ObjectContextProvider provider) {
        this.provider = provider;
        this.environment = environment;
    }
    
    @SuppressWarnings("unchecked")
    public List<String> buildConstraints(Class propertyType, AnnotationProvider provider) {
        BeanEditContext bec = environment.peek(BeanEditContext.class);
        PropertyEditContext pec = environment.peek(PropertyEditContext.class);
        if (bec == null || pec == null) {//not much we can do...
            return null;
        }
        
        Class<?> beanType = bec.getBeanClass();
        EntityResolver er = this.provider.currentContext().getEntityResolver();
        ObjEntity oent = er.lookupObjEntity(beanType);
        if (oent == null) {
            return null;
        }
        
        String propId = pec.getPropertyId();
        ObjRelationship relationship = relationship(propId,oent);
        DbAttribute dbatt = extractDbAttribute(propId, oent,relationship);
        
        if (dbatt == null) {
            return null;
        }
        List<String> ret = new ArrayList<String>();
        if (isPropRequired(dbatt, relationship)) {
            ret.add("required");
        }
        //little tricky here. If it's a string, we add a maxlength validator, otherwise, we ignore it, for now.
        if (dbatt.getMaxLength() > 0 && CharSequence.class.isAssignableFrom(propertyType)) {
            ret.add(String.format("maxlength=%d",dbatt.getMaxLength()));
        }
        if (ret.isEmpty()) {
            return null;
        }
        return ret;
    }
    
    private boolean isPropRequired(DbAttribute dbatt, ObjRelationship rel) {
        if (!dbatt.isMandatory()) {
            return false;
        }
        if (rel == null) {
            return true;
        }
       
        //ok, it's mandatory.
        //if it's not a pk, then it's really mandatory.
        if (!dbatt.isPrimaryKey()) {
            return true;
        }
        //ok, it /is/ a pk... if source is indepenent of target, then 
        //this is the "master" side of a to-dep-pk-type situation. 
        //Otherwise, the child side of such a situation. Parent side: not mandatory.
        //child side: mandatory.
        return !rel.isSourceIndependentFromTargetChange();
    }
    
    private ObjRelationship relationship(String propId, ObjEntity oent) {
        return (ObjRelationship) oent.getRelationship(propId);
    }
    
    private DbAttribute extractDbAttribute(String propId, ObjEntity oent,ObjRelationship rel) {
        if (rel == null) {
	    if (oent.getAttribute(propId) == null) {
		return null;
	    } else {
		return ((ObjAttribute) oent.getAttribute(propId)).getDbAttribute();
	    }
        }
        
        if (rel.isToMany() || rel.getDbRelationships().isEmpty()) {
	        return null;
        }
            
        DbRelationship dbrel = rel.getDbRelationships().get(0);
        //assume only one... for now.
        if (dbrel.getSourceAttributes().isEmpty()) {
            return null;
        }
        return dbrel.getSourceAttributes().iterator().next();
    }

}
