package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;

/**
 * Component binding that represents an EJBQLQuery.
 * eg: 
 * <t:grid source="ejbq:select a from Artist a"/>
 * @author robertz
 *
 */
public class EJBQLBinding extends AbstractBinding {
    
    private final EJBQLQuery query;
    private final String toString;

    public EJBQLBinding(final Location location, EJBQLQuery query, String toString) {
        super(location);
        this.query = query;
        this.toString = toString;
    }

    /* the query doesn't change, but... the query /results/ do. So we're not invariant */
    public boolean isInvariant() {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Class getBindingType() {
        return EJBQLQuery.class;
    }

    public Object get() {
        return query;
    }
    
    public String toString() {
        return toString;
    }
    
}
