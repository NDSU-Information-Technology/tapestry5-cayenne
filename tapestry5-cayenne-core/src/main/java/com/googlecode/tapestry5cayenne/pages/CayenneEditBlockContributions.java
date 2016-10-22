package com.googlecode.tapestry5cayenne.pages;

import org.apache.tapestry5.annotations.Component;

import com.googlecode.tapestry5cayenne.components.ToOneEditor;

/**
 * Contains all of the custom "bean editor" blocks used for editing cayenne objects.
 * @author robertz
 *
 */
public class CayenneEditBlockContributions {
    
    @SuppressWarnings("unused")
    @Component
    private ToOneEditor _toOne;
    
}
