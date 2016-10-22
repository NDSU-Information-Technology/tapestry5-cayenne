package com.googlecode.tapestry5cayenne.integration.app0.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanDisplay;
import org.apache.tapestry5.corelib.components.BeanEditForm;

import com.googlecode.tapestry5cayenne.model.Painting;

public class TestToOneControl {
    
    @Persist
    @Property
    @SuppressWarnings("unused")
    private Painting _painting;
    
    @SuppressWarnings("unused")
    @Component(parameters={
            "object=painting"
    })
    private BeanEditForm _form;
    
    @SuppressWarnings("unused")
    @Component(parameters={
           "object=painting" 
    })
    private BeanDisplay _display;
    
}
