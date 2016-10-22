package com.googlecode.tapestry5cayenne.internal;

import org.apache.tapestry5.OptionModel;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * An implementation of OptionModel that uses the results of invoking a method as the label.
 * 
 */
public class MethodLabelOptionModel implements OptionModel {
    
    private final String _label;
    private final Object _value;
    
    /**
     * @param value The object represented by the option model.
     * @param label The method to invoke on the object, representing its label.
     */
    public MethodLabelOptionModel(Object value, Method label) {
        _value = value;
        _label = Labeler.labelForObject(value,label);
    }

    public Map<String, String> getAttributes() {
        return null;
    }

    public String getLabel() {
        return _label;
    }

    public Object getValue() {
        return _value;
    }

    public boolean isDisabled() {
        return false;
    }
}
