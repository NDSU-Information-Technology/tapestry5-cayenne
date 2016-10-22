package com.googlecode.tapestry5cayenne.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.googlecode.tapestry5cayenne.ContextType;

/**
 * Marker annotation for use in conjunction with tapestry's Inject annotation
 * for injecting contexts.  When specifying:
 * <code>
 * @Inject
 * private ObjectContext oc;
 * </code>
 * t5cayenne will normally inject the "current" context (normally associated with the user's session).
 * This behavior can be modified with OCType annotation:
 * <code>
 * @Inject
 * @OCType(ContextType.NEW)
 * private ObjectContext oc;
 * </code> 
 * In this case, a newly-created ObjectContext would be used.
 * See {@link ContextType} for valid values and their meanings.
 * @author robertz
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OCType {
    
    ContextType value() default ContextType.CURRENT;
}
