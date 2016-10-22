package com.googlecode.tapestry5cayenne.internal;

import static org.testng.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.googlecode.tapestry5cayenne.internal.util.MethodWrapper;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.annotations.Label;

@Test(groups="all")
public class AnnotationFinderTest {
    
    @DataProvider(name="methoddata")
    Object[][] methodData() throws Exception {
        return new Object[][] {
                {AnnotatedBean.class,Cayenne.class,new MethodWrapper(null)},
                {AnnotatedBean.class,Label.class,new MethodWrapper(AnnotatedBean.class.getMethod("getTheLabel"))},
        };
    }

    @Test(dataProvider="methoddata")
    public void testMethodForAnnotation(Class<?> type, Class<? extends Annotation> query, MethodWrapper result) {
        assertEquals(AnnotationFinder.methodForAnnotation(query,type),result.getMethod());
    }
    
    @DataProvider(name="fielddata")
    Object[][] fieldData() throws Exception {
        return new Object[][] {
                {AnnotatedBean.class,Cayenne.class,AnnotatedBean.class.getDeclaredField("theLabel")},
                {AnnotatedBean.class,Label.class,null}
        };
    }
    
    @Test(dataProvider="fielddata")
    public void testFieldForAnnotation(
            Class<?> type, 
            Class<? extends Annotation> query, 
            Field result) {
        assertEquals(AnnotationFinder.fieldForAnnotation(query,type),result);
    }
}

@Marker(Cayenne.class)
class AnnotatedBean {
    
    @Cayenne
    private String theLabel="";
    
    @SuppressWarnings("unused")
    private String unannotatedField="";
    
    @Label
    public String getTheLabel() {
        return theLabel;
    }
    
    public String getUnannotatedMethod() {
        return "";
    }
}