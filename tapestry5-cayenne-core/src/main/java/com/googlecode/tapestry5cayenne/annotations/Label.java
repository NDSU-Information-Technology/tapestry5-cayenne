package com.googlecode.tapestry5cayenne.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a particular method as the "label" for the object type.
 * The label is used when rendering selects for objects, as a short description for the object, etc.
 * @author robertz
 * toString could be used for this purpose (and t5cayenne will fall back to using toString if no @Label'ed method is found)
 * but it is often desirable to /not/ override cayenne's default toString implementation; the default implementation is useful
 * for debugging purposes, whereas label is explicitly meant for the purpose of displaying the entity to a user.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Label {
}
