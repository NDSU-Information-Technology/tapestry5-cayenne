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
package com.googlecode.tapestry5cayenne.internal.util;

import java.lang.reflect.Method;

/**
 * Apparently, somewhere between testng 5.8 and 5.12, testng decided that @Test
 * methods with a parameter of Method get the test method being run currently
 * injected into them. Stupid. @BeforeMethod, @Before, @After, @AfterMethod,
 * and @DataProvider, yes, I can see the value there. Note that this isn't
 * documented behavior and is quite possibly a bug in testng. In any event,
 * provide a wrapper class so we can make our assertions correctly.
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
