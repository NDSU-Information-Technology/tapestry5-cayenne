package com.googlecode.tapestry5cayenne.internal;

/**
 * Bean for use with the environmental service.
 * Used to allow communication between the CayenneBeanModelSource, and 
 * DataTypeAnalyzers, particularly the CayenneDataTypeAnalyzer.
 * @author robertz
 *
 */
public class BeanModelTypeHolder {
    
    private final Class<?> _type;
    
    public BeanModelTypeHolder(final Class<?> type) {
        _type = type;
    }
    
    /**
     * @return the type of object for which a bean model is being built
     */
    public Class<?> getType() { 
        return _type;
    }
}
