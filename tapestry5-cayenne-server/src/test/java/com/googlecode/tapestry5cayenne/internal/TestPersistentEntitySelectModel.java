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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.OptionModel;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.PersistentEntitySelectModel;
import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

@Test(groups = "all")
public class TestPersistentEntitySelectModel extends Assert {

  private ObjectContext _context;
  private PersistentManager _manager;
  private List<Artist> _data;

  @BeforeClass
  void setup() throws Exception {
    TestUtils.setupdb();
    _context = BaseContext.getThreadObjectContext();
    _data = TestUtils.basicData(_context);
    _manager = EasyMock.createMock(PersistentManager.class);
    List<Artist> copy = new ArrayList<Artist>(_data);
    new Ordering("name", SortOrder.ASCENDING).orderList(copy);
    EasyMock.expect(_manager.listAll(Artist.class)).andReturn(copy);
    EasyMock.replay(_manager);
  }

  @AfterClass
  void verify() {
    EasyMock.verify(_manager);
  }

  @SuppressWarnings("unchecked")
  public void construction() {
    PersistentEntitySelectModel model = new PersistentEntitySelectModel(Artist.class, _manager);
    assertNull(model.getOptionGroups());
    assertEquals(model.getOptions().size(), _data.size());
    Ordering o = new Ordering("name", SortOrder.ASCENDING);
    o.orderList(_data);
    Iterator<OptionModel> it = model.getOptions().iterator();
    for (Artist a : _data) {
      assertEquals(it.next().getValue(), a);
    }
  }
}
