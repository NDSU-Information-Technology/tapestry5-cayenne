/*  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.googlecode.tapestry5cayenne.integration;

import org.apache.tapestry5.test.SeleniumTestCase;
import org.testng.annotations.Test;

@Test(singleThreaded = true, groups = "integration")
public class TapestryCayenneIntegrationTest extends SeleniumTestCase {

  public TapestryCayenneIntegrationTest() {
//    super("src/test/app0");
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
