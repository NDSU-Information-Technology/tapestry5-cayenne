package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;

import com.googlecode.tapestry5cayenne.annotations.Label;

/**
 * Utility class that determines the appropriate "label" (user-presentable string representation) for an entity.
 * @author robertz
 *
 */
public class Labeler {
    
    /**
     * @param obj The object to find the label for
     * @return The label for the object, or an html space if the object is null.
     */
    public static String htmlLabelForObject(Object obj) {
        String lbl = labelForObject(obj);
        if (lbl.equals("")) {
            return "&nbsp;";
        }
        return lbl;
    }
    
    /**
     * 
     * @param obj The obj to find the label for
     * @param label The label method to use for the object
     * @return html space if the label string is empty, otherwise the label string.
     * As with labelForObject(Object,Method), this version allows for increased efficieny
     * when dealing with collections of objects.
     */
    public static String htmlLabelForObject(Object obj, Method label) {
        String lbl = labelForObject(obj,label);
        if (lbl.equals("")) { 
            return "&nbsp;";
        }
        return lbl;
    }
    
    /**
     * @param obj The object to find the label for
     * @return The label for the object, or the empty string if the object is null.
     */
    public static String labelForObject(Object obj) {
        if (obj == null) {
            return "";
        }
        Method m = AnnotationFinder.methodForAnnotation(Label.class, obj.getClass());
        return labelForObject(obj,m);
    }
    
    /**
     * Invokes the label 
     * @param obj The object to find the label for
     * @param label The "label" method
     * @return The empty string if obj, label or the resulting of calling label on obj is null, otherwise the result of invoking label on obj (or obj.toString)
     * This is boilerplate code, but ensures that object "labels" are handled consistently.
     * Furthermore, this version of the labelForObject allows for greater efficiency when dealing with collections since a user
     * may find the label method ahead of time rather than having to find
     * the label method for each item in a collection.
     */
    public static String labelForObject(Object obj, Method label) {
        if (obj == null) {
            return "";
        }
        if (label == null) {
            return obj.toString();
        }
        try {
            Object val = label.invoke(obj);
            return (val==null?"":val.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
}
