package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.services.BindingFactory;

import com.googlecode.tapestry5cayenne.internal.EJBQLBinding;

/**
 * EJBQLQuery binding factor, so you can do things like:
 * <t:grid source="select a from Artist a order by a.lastName, a.firstName"/>
 * @author robertz
 *
 */
public class EJBQLBindingFactory implements BindingFactory {

    public Binding newBinding(String description, ComponentResources container,
            ComponentResources component, String expression, Location location) {
        String toString = String.format("EJBQLBinding[%s %s(%s)]",description,
                container.getCompleteId(),
                expression);
        EJBQLQuery query;
        try {
            query = new EJBQLQuery(expression);
        } catch (Exception e) {
            throw new TapestryException("Unable to convert " + expression + " into an EJBQLQuery",e);
        }
        return new EJBQLBinding(location,query,toString);
    }

}
