/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;

/**
 * Defines an interface through which Cayenne a new or existing ObjectContext may be obtained.
 *
 * @author Robert Zeigler
 */
public interface ObjectContextProvider {
    
    /**
     * @return An existing data context, associated with the current request, if possible.
     */
    ObjectContext currentContext();
    
    /**
     * @return a new ObjectContext
     */
    ObjectContext newContext();

}
