package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.services.ClassTransformation;
import org.apache.tapestry5.services.InjectionProvider;

import com.googlecode.tapestry5cayenne.ContextType;
import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.annotations.OCType;
import com.googlecode.tapestry5cayenne.internal.ObjectContextWrapper;
import org.apache.tapestry5.services.TransformField;

/**
 * Provides an InjectionProvider so pages and components can @Inject an ObjectContext directly.
 * Wanted to do this as an ObjectProvider, but it has too many dependencies on "late" binding services
 * that result in cyclic dependencies. InjectionProvider isn't so central to tapestry ioc, so it's
 * easier to use and accomplishes the same goal (except that it only works for page and component classes, not services).
 * @author robertz
 *
 */
public class ObjectContextInjectionProvider implements InjectionProvider {
    
    private final ObjectContextProvider provider;
    private final PerthreadManager threadManager;
    private final PropertyShadowBuilder shadowBuilder;
    
    /**
     * @param provider the ObjectContextProvider which is ultimately used to grab the appropriate context.
     */
    //putting @Cayenne on the parameter is the difference between T5-ioc
    //dying a gruesome death (recursive dependencies), and starting happily.
    public ObjectContextInjectionProvider(
            @Cayenne final ObjectContextProvider provider,
            PerthreadManager manager,
            PropertyShadowBuilder shadowBuilder) {
        this.provider = provider;
        this.threadManager = manager;
        this.shadowBuilder = shadowBuilder;
    }
    
    public boolean provideInjection(String fieldName, Class fieldType,
            ObjectLocator locator, ClassTransformation transformation,
            MutableComponentModel componentModel) {
        if (!(ObjectContext.class.isAssignableFrom(fieldType))) {
            return false;
        }
        TransformField field = transformation.getField(fieldName);
        OCType t = field.getAnnotation(OCType.class);
        ContextType ctype = t==null?ContextType.CURRENT:t.value();
        ObjectContext toInject;
        switch(ctype) {
            case NEW:
                toInject = shadowBuilder.build(
                        new ObjectContextWrapper(threadManager,provider),
                        "newContext",
                        ObjectContext.class);
                break;
            case CHILD:
                toInject = shadowBuilder.build(
                        new ObjectContextWrapper(threadManager,provider),
                        "childContext",
                        ObjectContext.class);
                break;
            default:
                toInject = shadowBuilder.build(
                        new ObjectContextWrapper(threadManager,provider),
                        "currentContext",
                        ObjectContext.class);
        }
        field.inject(toInject);
        return true;
    }

}
