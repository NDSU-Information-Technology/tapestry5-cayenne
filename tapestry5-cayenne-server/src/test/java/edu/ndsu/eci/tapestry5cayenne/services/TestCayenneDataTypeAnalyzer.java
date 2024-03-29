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
package edu.ndsu.eci.tapestry5cayenne.services;

import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.tapestry5.beaneditor.DataType;
import org.apache.tapestry5.commons.services.DataTypeAnalyzer;
import org.apache.tapestry5.commons.services.PropertyAdapter;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.Environment;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.TestUtils;
import edu.ndsu.eci.tapestry5cayenne.internal.BeanModelTypeHolder;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.model.Painting;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneModule;

@Test(groups = "all")
public class TestCayenneDataTypeAnalyzer extends Assert {

  private Registry _reg;
  private DataTypeAnalyzer _analyzer;

  @BeforeClass
  void setup() throws Exception {
    TestUtils.setupdb();
    _reg = TestUtils.setupRegistry("App0", TapestryCayenneModule.class, TestModule.class);
    _analyzer = _reg.getService("CayenneDataTypeAnalyzer", DataTypeAnalyzer.class);
  }

  public void test_for_nonobjentity_types() {
    _reg.getService(Environment.class).push(BeanModelTypeHolder.class, new BeanModelTypeHolder(SomePOJO.class));
    try {
      PropertyAdapter adaptor = EasyMock.createMock(PropertyAdapter.class);
      EasyMock.expect(adaptor.getType()).andReturn(List.class);
      EasyMock.replay(adaptor);
      assertNull(_analyzer.identifyDataType(adaptor));
      EasyMock.verify(adaptor);
    } catch (NullPointerException e) {
      fail("Should not have thrown a NPE analyzing the property: artist in bean class SomePOJO.", e);
    }
  }

  public void test_longtext_types() {
    _reg.getService(Environment.class).push(BeanModelTypeHolder.class, new BeanModelTypeHolder(Painting.class));
    try {
      PropertyAdapter adaptor = EasyMock.createMock(PropertyAdapter.class);
      EasyMock.expect(adaptor.getName()).andReturn("title");
      EasyMock.expect(adaptor.getType()).andReturn(String.class);
      EasyMock.expect(adaptor.getAnnotation(DataType.class)).andReturn(null);
      EasyMock.replay(adaptor);
      String type = _analyzer.identifyDataType(adaptor);
      assertEquals(type, "longtext");
      EasyMock.verify(adaptor);
    } catch (Exception e) {
      failOnException(e);
    }
  }

  public void test_longtext_type_with_datatypeannotation() {
    _reg.getService(Environment.class).push(BeanModelTypeHolder.class, new BeanModelTypeHolder(Painting.class));
    try {
      PropertyAdapter adaptor = EasyMock.createMock(PropertyAdapter.class);
      EasyMock.expect(adaptor.getType()).andReturn(String.class);
      EasyMock.expect(adaptor.getAnnotation(DataType.class)).andReturn(new DataType() {
        public String value() {
          return "text";
        }

        public Class<? extends Annotation> annotationType() {
          return DataType.class;
        }

      });
      EasyMock.replay(adaptor);
      String type = _analyzer.identifyDataType(adaptor);
      assertEquals(type, "text");
      EasyMock.verify(adaptor);
    } catch (Exception e) {
      failOnException(e);
    }
  }

  /*
   * google code ticket #39: basically, when you have a POJO with a String
   * property + t5cayenne, you get NPE.
   */
  public void test_text_datatype_on_pojo() {
    _reg.getService(Environment.class).push(BeanModelTypeHolder.class, new BeanModelTypeHolder(SomePOJO.class));
    try {
      PropertyAdapter adaptor = EasyMock.createMock(PropertyAdapter.class);
      EasyMock.expect(adaptor.getType()).andReturn(String.class);
      EasyMock.replay(adaptor);
      assertEquals("text", _analyzer.identifyDataType(adaptor));
      EasyMock.verify(adaptor);
    } catch (Exception e) {
      failOnException(e);
    }
  }

  private void failOnException(Exception e) {
    fail("Should not have been an exception identifying the data type", e);
  }

}

class SomePOJO {
  private String name;

  public Artist getArtist() {
    return new Artist();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
