package com.googlecode.tapestry5cayenne.internal.util;

import java.lang.reflect.Method;

/**
 * Apparently, somewhere between testng 5.8 and 5.12, testng decided that @Test methods with a parameter of
 * Method get the test method being run currently injected into them. Stupid. @BeforeMethod, @Before, @After, @AfterMethod,
 * and @DataProvider, yes, I can see the value there. Note that this isn't documented behavior and is quite possibly a bug in
 * testng.  In any event, provide a wrapper class so we can make our assertions correctly.
 */
public final class MethodWrapper {
    private Method m;

    public MethodWrapper(Method m) {
        this.m = m;
    }

    public Method getMethod() {
        return m;
    }
}
