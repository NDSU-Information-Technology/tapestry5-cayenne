package com.googlecode.tapestry5cayenne.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Utility class for finding annotated field, methods, etc.
 * @author robertz
 *
 */
public class AnnotationFinder {

    /**
     * Finds the first method annotated with the query annotation for the provided type.
     * @param query The annotation to search for
     * @param type The type to search
     * @return The first method annotated with query, or null if no matching methods are found
     * Query and Type must not be null.
     */
    public static Method methodForAnnotation(Class<? extends Annotation> query, Class<?> type) {
        for(Method m : type.getMethods()) {
            if (m.getAnnotation(query) != null) {
                return m;
            }
        }
        return null;
    }

    /**
     * Finds the first field annotated with the query annotation for the provided type
     * @param query The annotation to search for
     * @param type The type to search
     * @return The first field annotated with query, or null if no matching fields are found.
     */
    public static Field fieldForAnnotation(Class<? extends Annotation> query, Class<?> type) {
        for(Field f : type.getDeclaredFields()) {
            if (f.getAnnotation(query) != null) {
                return f;
            }
        }
        return null;
    }

}
