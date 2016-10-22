package com.googlecode.tapestry5cayenne.internal;

public class SimpleTestBean implements Comparable<SimpleTestBean> {
    
    BeanValue value;
    public Integer getTheInt() {
        return value.value;
    }
    
    public BeanValue value() {
        return value;
    }
    
    public Integer comparableValue() {
        return getTheInt();
    }
    
    public int compareTo(SimpleTestBean o) {
        return getTheInt().compareTo(o.getTheInt());
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
}

class BeanValue {
    Integer value;
    @Override
    public String toString() {
        return value.toString();
    }
}
