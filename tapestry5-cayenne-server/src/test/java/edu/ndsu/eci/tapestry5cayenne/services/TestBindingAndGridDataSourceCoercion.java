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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.commons.services.TypeCoercer;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.BindingFactory;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.TestUtils;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.model.Painting;
import edu.ndsu.eci.tapestry5cayenne.services.ObjEntityBindingFactory;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneModule;

@Test
public class TestBindingAndGridDataSourceCoercion extends Assert {

  private Registry registry;
  private List<Artist> data;
  private ObjectContext context;

  @BeforeClass
  void setupDB() throws Exception {
    TestUtils.setupdb();
    registry = TestUtils.setupRegistry("App0", TapestryCayenneModule.class);
    context = registry.getService(ObjectContextProvider.class).newContext();
    data = TestUtils.basicData(context);
    new Ordering(Artist.NAME.getName(), SortOrder.ASCENDING).orderList(data);
  }

  @SuppressWarnings("unchecked")
  public void testBindingFactory() {
    BindingFactory fact = registry.getService("EJBQLBindingFactory", BindingFactory.class);
    ComponentResources mockResources = mockResources();
    Binding binding = fact.newBinding("testbinding", mockResources, null, "select a from Artist a order by a.name",
        null);
    assertEquals(binding.getBindingType(), EJBQLQuery.class);
    Object o = binding.get();
    assertTrue(o instanceof EJBQLQuery);
    List<Artist> ret = context.performQuery((EJBQLQuery) o);
    assertArtists(ret);
    EasyMock.verify(mockResources);
  }

  @SuppressWarnings("unchecked")
  public void testQueryToListAndGridDataSourceCoercions() {
    TypeCoercer coercer = registry.getService(TypeCoercer.class);
    Binding b = registry.getService("EJBQLBindingFactory", BindingFactory.class).newBinding("test", mockResources(),
        null, "select a from Artist a order by a.name", null);

    EJBQLQuery q = (EJBQLQuery) b.get();

    List<Artist> ret = coercer.coerce(q, List.class);

    assertArtists(ret);

    GridDataSource ds = coercer.coerce(q, GridDataSource.class);

    assertEquals(ds.getAvailableRows(), data.size());
    for (int i = 0; i < data.size(); i++) {
      assertEquals(((Artist) ds.getRowValue(i)).getObjectId(), data.get(i).getObjectId());
    }

  }

  public void testObjEntityBinding() {
    BindingFactory bf = registry.autobuild(ObjEntityBindingFactory.class);
    Binding b = bf.newBinding("test", mockResources(), null, "Artist", null);

    ObjEntity ent = (ObjEntity) b.get();

    assertEquals(ent.getName(), "Artist");

  }

  public void testPersistentClassToGridDataSourceCoercion() {
    TypeCoercer coercer = registry.getService(TypeCoercer.class);

    GridDataSource ds = coercer.coerce(Painting.class, GridDataSource.class);
    List<SortConstraint> constraints = Collections.emptyList();

    ds.prepare(0, ds.getAvailableRows() - 1, constraints);
    final List<Painting> paints = new ArrayList<Painting>();
    CollectionUtils.collect(data, new Transformer() {

      public Object transform(Object input) {
        paints.addAll(((Artist) input).getPaintingList());
        return null;
      }

    });
    new Ordering(Painting.TITLE.getName(), SortOrder.ASCENDING).orderList(paints);
    assertEquals(ds.getAvailableRows(), paints.size());
    for (int i = 0; i < paints.size(); i++) {
      assertEquals(((Painting) ds.getRowValue(i)).getObjectId(), paints.get(i).getObjectId());
    }
  }

  private ComponentResources mockResources() {
    ComponentResources mockResources = EasyMock.createMock(ComponentResources.class);
    EasyMock.expect(mockResources.getCompleteId()).andReturn("TestComp");
    EasyMock.replay(mockResources);
    return mockResources;
  }

  private void assertArtists(List<Artist> actual) {
    assertEquals(actual.size(), data.size());
    for (int i = 0; i < data.size(); i++) {
      assertEquals(actual.get(i).getObjectId(), data.get(i).getObjectId());
    }
  }

}
