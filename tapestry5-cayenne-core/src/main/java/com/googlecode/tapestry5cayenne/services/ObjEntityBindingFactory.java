package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.services.BindingFactory;

import com.googlecode.tapestry5cayenne.internal.ObjEntityBinding;

/**
 * BindingFactory for ObjEntity, so you can do things like:
 * <t:grid source="ent:User"/>
 * @author robertz
 *
 */
public class ObjEntityBindingFactory implements BindingFactory {
    
    private final ObjectContextProvider provider;
    
    public ObjEntityBindingFactory(final ObjectContextProvider p) {
        provider = p;
    }

    public Binding newBinding(String description, ComponentResources container,
            ComponentResources component, String expression, Location location) {
        String toString = String.format("ObjEntityBinding[%s %s(%s)]",description,
                container.getCompleteId(),
                expression);
        ObjEntity objEnt = provider.currentContext().getEntityResolver().getObjEntity(expression);
        return new ObjEntityBinding(location,objEnt,toString);
    }

}
