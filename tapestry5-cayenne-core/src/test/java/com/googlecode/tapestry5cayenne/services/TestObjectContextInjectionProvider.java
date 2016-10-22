package com.googlecode.tapestry5cayenne.services;

import java.lang.annotation.Annotation;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ioc.services.PerThreadValue;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.services.ClassTransformation;
import org.apache.tapestry5.services.DataTypeAnalyzer;
import org.apache.tapestry5.services.TransformField;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.ContextType;
import com.googlecode.tapestry5cayenne.annotations.OCType;
import com.googlecode.tapestry5cayenne.internal.ObjectContextWrapper;

@Test(groups="all")
public class TestObjectContextInjectionProvider extends Assert {
    
    private ObjectContextInjectionProvider p;
    private ObjectContextProvider provider;
    private PerthreadManager threadManager;
    private PropertyShadowBuilder builder;
    private ClassTransformation transformation;
    
    @BeforeMethod
    public void setup() {
        provider = EasyMock.createMock(ObjectContextProvider.class);
        threadManager = EasyMock.createMock(PerthreadManager.class);
        EasyMock.expect(threadManager.createValue()).andReturn(new PerThreadValue<Object>() {
            public boolean exists() { return false; }
            public Object get() { return null; }
            public Object get(Object o) { return null; }
            public Object set(Object o) { return null; }
        }).times(0,1);
        builder = EasyMock.createMock(PropertyShadowBuilder.class);
        p = new ObjectContextInjectionProvider(provider,threadManager,builder);
        transformation = EasyMock.createMock(ClassTransformation.class);
    }
    
    @AfterMethod
    void verify() {
        EasyMock.verify(transformation,provider,threadManager,builder);
    }
    
    void replay() {
        EasyMock.replay(transformation,provider,threadManager,builder);
    }
    
    public void testProvide_NonOCClass() {
        replay();
        assertFalse(p.provideInjection("analyzer",DataTypeAnalyzer.class, null,transformation, null));
    }
    
    public void testProvide_OCClass_NoAnnotation_ReturnsCurrentContext() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        TransformField tf = trainContextField(null, mock);

        EasyMock.replay(mock);

        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("currentContext"), EasyMock.eq(ObjectContext.class)))
                
                .andReturn(mock);

        replay();
                
        assertTrue(p.provideInjection("context",ObjectContext.class, null,transformation, null));
        EasyMock.verify(tf);
    }
    
    public void testProvide_AnnotationCurrent_ReturnsCurrent() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        TransformField tf = trainContextField(ContextType.CURRENT, mock);

        EasyMock.replay(mock);
        
        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("currentContext"), EasyMock.eq(ObjectContext.class)))
                .andReturn(mock);
        
        replay();

        assertTrue(p.provideInjection("context",ObjectContext.class,null,transformation,null));
        EasyMock.verify(tf);
    }
    
    public void testProvide_AnnotationNew_ReturnsNew() {
        ObjectContext mock = EasyMock.createMock(ObjectContext.class);
        EasyMock.replay(mock);

        TransformField tf = trainContextField(ContextType.NEW, mock);

        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("newContext"), EasyMock.eq(ObjectContext.class)))
            .andReturn(mock);
        
        replay();
        
        assertTrue(p.provideInjection("context",ObjectContext.class,null,transformation,null));
        EasyMock.verify(tf);
    }

    public void testProvide_AnnotationChild_childsupported_returnschild() {
        MockDataContext mdc = new MockDataContext();
        TransformField tf = trainContextField(ContextType.CHILD, mdc);

        EasyMock.expect(builder.build(
                EasyMock.isA(ObjectContextWrapper.class), 
                EasyMock.eq("childContext"), EasyMock.eq(ObjectContext.class)))
                .andReturn(mdc);
        

        replay();
        
        assertTrue(p.provideInjection("context",ObjectContext.class,null,transformation,null));
        EasyMock.verify(tf);
    }

    private TransformField trainContextField(final ContextType type, ObjectContext context) {
        TransformField tf = EasyMock.createMock(TransformField.class);
        if (type == null) {
            EasyMock.expect(tf.getAnnotation(OCType.class)).andReturn(null);
        } else {
            EasyMock.expect(tf.getAnnotation(OCType.class)).andReturn(
                    new OCType() {
                        public ContextType value() {
                            return type;
                        }
                        public Class<? extends Annotation> annotationType() {
                            return OCType.class;
                        }
                    }
            );
        }
        tf.inject(context);
        EasyMock.replay(tf);
        EasyMock.expect(transformation.getField("context")).andReturn(tf);
        return tf;
    }
}

@SuppressWarnings("serial")
class MockDataContext extends DataContext {
    boolean createChildCalled=false;
    
    MockDataContext() {}
    
    @Override
    public ObjectContext createChildContext() {
        createChildCalled=true;
        return this;
    }
}
