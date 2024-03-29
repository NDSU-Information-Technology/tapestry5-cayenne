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
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.commons.AnnotationProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.BeanEditContext;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.PropertyEditContext;
import org.apache.tapestry5.services.ValidationConstraintGenerator;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.TestUtils;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.model.ArtistDetails;
import edu.ndsu.eci.tapestry5cayenne.model.Bid;
import edu.ndsu.eci.tapestry5cayenne.model.Painting;
import edu.ndsu.eci.tapestry5cayenne.model.StringPKEntity;
import edu.ndsu.eci.tapestry5cayenne.services.CayenneConstraintGenerator;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneModule;

@Test
public class TestCayenneConstraintGenerator extends Assert {

  private ValidationConstraintGenerator gen;
  private PropertyEditContext propCtxt;
  private Registry registry;
  private BeanEditContext bec;

  @BeforeClass
  void setup() throws Exception {
    TestUtils.setupdb();
    registry = TestUtils.setupRegistry("app0", TapestryCayenneModule.class);
  }

  @BeforeMethod
  void initMocks() {
    propCtxt = EasyMock.createMock(PropertyEditContext.class);
    bec = EasyMock.createMock(BeanEditContext.class);
    Environment e = registry.getService(Environment.class);
    e.push(PropertyEditContext.class, propCtxt);
    e.push(BeanEditContext.class, bec);

    gen = registry.autobuild(CayenneConstraintGenerator.class);
  }

  void replay() {
    EasyMock.replay(propCtxt, bec);
  }

  @AfterMethod
  void verify() {
    EasyMock.verify(propCtxt, bec);
  }

  public void test_non_DO_returns_null() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(String.class);
    replay();
    assertNull(gen.buildConstraints(byte[].class, new NullAnnotationProvider()));
  }

  public void test_noconstraints_returns_null() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(StringPKEntity.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(StringPKEntity.INT_PROP1.getName());
    replay();
    assertNull(gen.buildConstraints(int.class, new NullAnnotationProvider()));
  }

  public void test_mandatory_property_returns_required() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(Bid.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(Bid.AMOUNT.getName());
    replay();
    assertEquals(gen.buildConstraints(String.class, new NullAnnotationProvider()), Arrays.asList("required"));
  }

  public void test_mandatory_relationship_returns_required() {

    EasyMock.expect(bec.getBeanClass()).andStubReturn(Bid.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(Bid.PAINTING.getName());
    replay();
    assertEquals(gen.buildConstraints(Painting.class, new NullAnnotationProvider()), Arrays.asList("required"));
  }

  public void test_tomany_relationship_returns_nothing() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(Artist.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(Artist.PAINTING_LIST.getName());
    replay();
    assertNull(gen.buildConstraints(List.class, new NullAnnotationProvider()));
  }

  public void test_maxlength_property() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(StringPKEntity.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(StringPKEntity.STRING_PROP2.getName());
    replay();
    assertEquals(gen.buildConstraints(String.class, new NullAnnotationProvider()), Arrays.asList("maxlength=64"));
  }

  public void test_maxlength_and_required() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(Painting.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(Painting.TITLE.getName());
    replay();

    assertEquals(gen.buildConstraints(String.class, new NullAnnotationProvider()),
        Arrays.asList("required", "maxlength=1024"));
  }

  public void test_toonerelationship_usingpkside_notrequired() {
    EasyMock.expect(bec.getBeanClass()).andStubReturn(Artist.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(Artist.CURRENT_BID.getName());
    replay();

    assertNull(gen.buildConstraints(List.class, new NullAnnotationProvider()));
  }

  public void test_toonerelationship_pkisfk_isrequired() {
    // in the case of todeppk, one object is a 'child' of the other object: it's
    // pk is a fk, as well.
    // which makes the property absolutely mandatory, or the obj won't have a
    // pk! Test to make sure this is correctly handled.
    EasyMock.expect(bec.getBeanClass()).andStubReturn(ArtistDetails.class);
    EasyMock.expect(propCtxt.getPropertyId()).andReturn(ArtistDetails.ARTIST.getName());
    replay();

    assertEquals(gen.buildConstraints(Artist.class, new NullAnnotationProvider()), Arrays.asList("required"));
  }
}

class NullAnnotationProvider implements AnnotationProvider {
  public <T extends Annotation> T getAnnotation(Class<T> arg0) {
    return null;
  }
}