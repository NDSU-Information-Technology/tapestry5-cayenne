/*
 * Created on Mar 18, 2008
 * 
 * 
 */
package com.googlecode.tapestry5cayenne.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import org.apache.tapestry5.ioc.services.ServiceOverride;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {
    
    @SuppressWarnings("unchecked")
    public static void bind(ServiceBinder binder) 
    {
        binder.bind(ObjectContextProvider.class, DataContextProviderImpl.class)
            .withMarker(Cayenne.class).withId("DataContext");
    }
    
    /**
     * Alias the DataContext-based object context provider to ObjectContextProvider.
     * @param conf
     * @param provider
     */
    @SuppressWarnings("unchecked")
    @Contribute(ServiceOverride.class)
    public static void contributeServiceOverride(MappedConfiguration<Class, Object> conf,
            @Cayenne ObjectContextProvider provider) {
        conf.add(ObjectContextProvider.class, provider);
    }
}
