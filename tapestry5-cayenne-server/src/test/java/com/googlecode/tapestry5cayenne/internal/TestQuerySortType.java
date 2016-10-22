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
package com.googlecode.tapestry5cayenne.internal;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.internal.QuerySortType;

@Test(sequential = true, groups = "all")
public class TestQuerySortType {

  List<SimpleTestBean> _values;

  @BeforeMethod
  void setup() {
    _values = new ArrayList<SimpleTestBean>();
    for (int i = 0; i < 10; i++) {
      SimpleTestBean b = new SimpleTestBean();
      BeanValue bv = new BeanValue();
      bv.value = i;
      b.value = bv;
      _values.add(b);
    }
    Collections.shuffle(_values);
  }

  public void testMethodResultNonComparable() throws Exception {
    Method m = SimpleTestBean.class.getMethod("value");
    QuerySortType.METHOD.sort(_values, null, m);
    assertValues();
  }

  public void testMethodResultComparable() throws Exception {
    Method m = SimpleTestBean.class.getMethod("comparableValue");
    QuerySortType.METHOD.sort(_values, null, m);
    assertValues();
  }

  public void testOrdering() {
    Ordering o = new Ordering("theInt", SortOrder.ASCENDING);
    QuerySortType.ORDERING.sort(_values, o, null);
    assertValues();
  }

  public void testComparable() {
    QuerySortType.COMPARABLE.sort(_values, null, null);
    assertValues();
  }

  private void assertValues() {
    for (int i = 0; i < 10; i++) {
      assertEquals(_values.get(i).getTheInt().intValue(), i);
    }
  }
}
