package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.services.PerThreadValue;
import org.apache.tapestry5.ioc.services.PerthreadManager;

import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

/**
 * Object that wraps ObjectContextProvider for use, in conjunction with PropertyShadowBuilder, 
 * in injected fields in pages and components.
 * @author robertz
 *
 */
/*
 * Tapestry injects an object into pages, components, and services, but that injection
 * is a one-time deal when the page/component/service is initialized.
 * If we want to be able to inject a thread-bound ObjectContext, we have to provide a wrapper.
 * We'll use the shadow property builder to build actually retrieve the object for us at runtime,
 * but it requires the use of a "service" to build from, and a property to retrieve.
 * ObjectContextProvider doesn't uses javabeans properties for its methods, so we can't use it
 * directly.  We also don't /want/ to use it directly in the case of child context or
 * new context, b/c we don't want to create a new context for every method invocation.
 * Rather, we want to create a new context for the /first/ method invocation, and then return
 * that context for subsequent invocations for the same request.
 */
public class ObjectContextWrapper {
    
    /**
     * Key for the base request attribute key.
     * Note that we have to uniquify this to guarantee that there is no collision between
     * different @Inject'ed fields.
     */
    private static final String REQUEST_BOUND_CONTEXT="tapestry5cayenne.thread.context";
    
    private final ObjectContextProvider provider;
    
    private final PerThreadValue<ObjectContext> threadContext;
    
    public ObjectContextWrapper(PerthreadManager threadManager,ObjectContextProvider provider) {
        this.provider = provider;
        threadContext = threadManager.createValue();
    }
    
    /**
     * @return a new object context for this thread.
     * Notes:
     *  1) Only a single ObjectContext will be created per thread.  It is created "on demand".
     *  2) The context is guaranteed to be different from the session-bound context
     *  3) The context will only live for the duration of the thread.
     */
    public ObjectContext getNewContext() {
        ObjectContext context = (ObjectContext) threadContext.get();
        if (context == null) {
            context = provider.newContext();
            threadContext.set(context);
        }
        return context;
    }
    
    /**
     * @return the current ObjectContext (normally session-bound).
     */
    public ObjectContext getCurrentContext() {
        return provider.currentContext();
    }
    
    /**
     * 
     * @return a (new) child ObjectContext of the thread-bound (normally session-bound) ObjectContext.
     * As for getNewContext, only one child context is created per thread, and it only lasts the duration of the thread.
     */
    public ObjectContext getChildContext() {
        ObjectContext context = (ObjectContext) threadContext.get();
        if (context == null) {
            context = provider.currentContext().createChildContext();
            threadContext.set(context);
        }
        return context;
    }
}
