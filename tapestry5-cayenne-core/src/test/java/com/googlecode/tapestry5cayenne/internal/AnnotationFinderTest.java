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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.googlecode.tapestry5cayenne.internal.util.MethodWrapper;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.annotations.Label;

@Test(groups = "all")
public class AnnotationFinderTest {

  @DataProvider(name = "methoddata")
  Object[][] methodData() throws Exception {
    return new Object[][] { { AnnotatedBean.class, Cayenne.class, new MethodWrapper(null) },
        { AnnotatedBean.class, Label.class, new MethodWrapper(AnnotatedBean.class.getMethod("getTheLabel")) }, };
  }

  @Test(dataProvider = "methoddata")
  public void testMethodForAnnotation(Class<?> type, Class<? extends Annotation> query, MethodWrapper result) {
    assertEquals(AnnotationFinder.methodForAnnotation(query, type), result.getMethod());
  }

  @DataProvider(name = "fielddata")
  Object[][] fieldData() throws Exception {
    return new Object[][] { { AnnotatedBean.class, Cayenne.class, AnnotatedBean.class.getDeclaredField("theLabel") },
        { AnnotatedBean.class, Label.class, null } };
  }

  @Test(dataProvider = "fielddata")
  public void testFieldForAnnotation(Class<?> type, Class<? extends Annotation> query, Field result) {
    assertEquals(AnnotationFinder.fieldForAnnotation(query, type), result);
  }
}

@Marker(Cayenne.class)
class AnnotatedBean {

  @Cayenne
  private String theLabel = "";

  @SuppressWarnings("unused")
  private String unannotatedField = "";

  @Label
  public String getTheLabel() {
    return theLabel;
  }

  public String getUnannotatedMethod() {
    return "";
  }
}