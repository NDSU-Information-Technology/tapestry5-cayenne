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
package edu.ndsu.eci.tapestry5cayenne.internal;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.internal.MethodLabelOptionModel;
import edu.ndsu.eci.tapestry5cayenne.internal.util.MethodWrapper;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;

@Test(groups = "all")
public class TestLabelOptionModel extends Assert {

  @SuppressWarnings("unused")
  @DataProvider(name = "options")
  private Object[][] options() throws Exception {
    Artist a = new Artist();
    a.setName("Picasso");
    MethodWrapper mname = new MethodWrapper(Artist.class.getMethod("getName"));
    MethodWrapper mnull = new MethodWrapper(null);
    return new Object[][] { { null, mnull, "" }, { a, mnull, a.toString() }, { a, mname, "Picasso" },
        { null, mname, "" }, };
  }

  @Test(dataProvider = "options")
  public void test(Object obj, MethodWrapper m, String label) {
    MethodLabelOptionModel model = new MethodLabelOptionModel(obj, m.getMethod());
    assertEquals(model.getLabel(), label);
    assertEquals(model.getValue(), obj);
    assertEquals(model.isDisabled(), false);
    assertNull(model.getAttributes());
  }

}
