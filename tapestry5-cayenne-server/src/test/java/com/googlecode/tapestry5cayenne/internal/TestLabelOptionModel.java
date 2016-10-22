package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;

import com.googlecode.tapestry5cayenne.internal.util.MethodWrapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.internal.MethodLabelOptionModel;
import com.googlecode.tapestry5cayenne.model.Artist;

@Test(groups="all")
public class TestLabelOptionModel extends Assert {
    
    @SuppressWarnings("unused")
    @DataProvider(name="options")
    private Object[][] options() throws Exception {
        Artist a = new Artist();
        a.setName("Picasso");
        MethodWrapper mname = new MethodWrapper(Artist.class.getMethod("getName"));
        MethodWrapper mnull = new MethodWrapper(null);
        return new Object[][] {
                {null,mnull,""},
                {a,mnull,a.toString()},
                {a,mname,"Picasso"},
                {null,mname,""},
        };
    }
    
    @Test(dataProvider="options")
    public void test(Object obj, MethodWrapper m, String label) {
        MethodLabelOptionModel model = new MethodLabelOptionModel(obj,m.getMethod());
        assertEquals(model.getLabel(),label);
        assertEquals(model.getValue(),obj);
        assertEquals(model.isDisabled(),false);
        assertNull(model.getAttributes());
    }
    
}
