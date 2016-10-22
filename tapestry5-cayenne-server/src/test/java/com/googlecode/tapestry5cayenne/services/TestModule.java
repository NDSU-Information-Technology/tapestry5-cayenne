/*
 * Created on Apr 5, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.tapestry5.ioc.MappedConfiguration;

import com.googlecode.tapestry5cayenne.model.Painting;

public class TestModule {
    //allows us to contribute the alias mode for tests.
    public void contributeApplicationDefaults(MappedConfiguration<String, String> conf) {
        conf.add("tapestry.alias-mode","production");
    }
    
    @SuppressWarnings("unchecked")
    public void contributeDefaultDataTypeAnalyzer(MappedConfiguration<Class,String> conf) {
        conf.add(Painting.class,"painting");
    }
    
}
