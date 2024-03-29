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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.Registry;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.TestUtils;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.model.Bid;
import edu.ndsu.eci.tapestry5cayenne.model.Painting;
import edu.ndsu.eci.tapestry5cayenne.model.StringPKEntity;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneModule;

@Test(groups = "all")
public class TestCayenneBeanModelSource extends Assert {

  private Registry _reg;
  private BeanModelSource _source;

  @BeforeClass
  void setup() throws Exception {
    TestUtils.setupdb();
    _reg = TestUtils.setupRegistry("App0", TapestryCayenneModule.class, TestModule.class);
    _source = _reg.getService("CayenneBeanModelSource", BeanModelSource.class);
  }

  @AfterTest
  void shutdown_reg() {
    if (_reg != null) {
      _reg.shutdown();
    }
  }

  @DataProvider(name = "property_tests")
  Object[][] propertyTests() {
    Map<String, String> stringPKProps = new HashMap<String, String>();
    stringPKProps.put(StringPKEntity.ID.getName(), "text");
    stringPKProps.put(StringPKEntity.INT_PROP1.getName(), "number");
    stringPKProps.put(StringPKEntity.STRING_PROP1.getName(), "longtext");
    stringPKProps.put(StringPKEntity.STRING_PROP2.getName(), "text");
    Map<String, String> artistProps = new HashMap<String, String>();
    // name is a longvarchar, so, longtext.
    artistProps.put(Artist.NAME.getName(), "longtext");
    artistProps.put(Artist.CURRENT_BID.getName(), "to_one");
    artistProps.put(Artist.DETAILS.getName(), "to_one");
    Map<String, String> artistPropsWithRelationship = new HashMap<String, String>(artistProps);
    artistPropsWithRelationship.put("paintingList", "to_many_collection");
    artistPropsWithRelationship.put("paintingsByTitle", "to_many_map");
    artistPropsWithRelationship.put("numPaintings", "number");
    artistPropsWithRelationship.put("currentBid", "to_one");
    artistPropsWithRelationship.put("acceptedBids", "to_many_collection");
    Map<String, String> paintingProps = new HashMap<String, String>();
    paintingProps.put(Painting.ARTIST.getName(), "to_one");
    paintingProps.put(Painting.PRICE.getName(), "number");
    paintingProps.put(Painting.TITLE.getName(), "longtext");
    Map<String, String> bidProps = new HashMap<String, String>();
    bidProps.put(Bid.AMOUNT.getName(), "number");
    bidProps.put(Bid.PAINTING.getName(), "painting");
    bidProps.put(Bid.BIDDER.getName(), "to_one");

    Map<String, String> bidPropsWithRelationship = new HashMap<String, String>(bidProps);
    bidPropsWithRelationship.put(Bid.ACCEPTING_ARTISTS.getName(), "to_many_collection");
    return new Object[][] { { StringPKEntity.class, true, stringPKProps },
        { StringPKEntity.class, false, stringPKProps }, { Artist.class, true, artistProps },
        { Artist.class, false, artistPropsWithRelationship, }, { Painting.class, true, paintingProps,

        }, { Painting.class, false, paintingProps,

        }, { Bid.class, true, bidProps, }, { Bid.class, false, bidPropsWithRelationship, } };
  }

  @Test(dataProvider = "property_tests")
  public void test_properties(Class<?> type, boolean filterReadonly, Map<String, String> props) {
    // ensure all properties specified are included.
    Messages msgs = createMock(Messages.class);
    expect(msgs.contains((String) anyObject())).andReturn(false).anyTimes();
    replay(msgs);

    BeanModel<?> model = filterReadonly ? _source.createEditModel(type, msgs) : _source.createDisplayModel(type, msgs);
    List<String> names = model.getPropertyNames();
    for (String key : props.keySet()) {
      System.out.println(key);
      if (!names.contains(key)) {
        System.out.println("MISSSSINGGGGG " + key + " " + type);
      }
      assertTrue(names.contains(key), "Model missing property " + key);
      assertEquals(model.get(key).getDataType(), props.get(key), "Property has wrong datatype");
    }
    for (String name : names) {
      assertTrue(props.containsKey(name), "Model contained extraneous property: " + name);
    }
  }
}
