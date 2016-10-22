package com.googlecode.tapestry5cayenne.integration;

import org.apache.tapestry5.test.AbstractIntegrationTestSuite;
import org.apache.tapestry5.test.SeleniumTestCase;
import org.testng.annotations.Test;

@Test(sequential=true,groups="integration")
public class TapestryCayenneIntegrationTest extends AbstractIntegrationTestSuite {
    
    public TapestryCayenneIntegrationTest() {
        super("src/test/app0");
    }
    
    public void test_commit_after() {
        open("/commitaftertestpage");
        waitForPageToLoad();
        assertTextPresent("Dali");
        clickAndWait("link=Commit Ok");
        assertTextPresent("Commitokname");
        clickAndWait("link=Runtime Exception");
        assertTextPresent("Commitokname");
        clickAndWait("link=Checked Exception");
        assertTextPresent("savesokwithcheckedexceptionname");
    }
    
    public void test_inject_objectcontext() {
        open("/injectobjectcontexttestpage");
        waitForPageToLoad();
        assertTextPresent("Injecting the current context really /does/ give you the current context.");
        assertTextPresent("Injecting with no annotation is the same as injecting with the octype current.");
        assertTextPresent("Injecting with octype child yields child of current context.");
        assertTextPresent("Injecting with octype new yields a new context, not child of current context.");
        
        String newContextHash = getText("id=newContextProp").trim();
        
        clickAndWait("link=Invalidate Session");
        
        assertTextPresent("Injecting the current context really /does/ give you the current context.");
        assertTextPresent("Injecting with no annotation is the same as injecting with the octype current.");
        assertTextPresent("Injecting with octype child yields child of current context.");
        assertTextPresent("Injecting with octype new yields a new context, not child of current context.");
        
        String newContextHash2 = getText("id=newContextProp").trim();
        assertFalse(newContextHash.equals(newContextHash2));
        
    }
}
