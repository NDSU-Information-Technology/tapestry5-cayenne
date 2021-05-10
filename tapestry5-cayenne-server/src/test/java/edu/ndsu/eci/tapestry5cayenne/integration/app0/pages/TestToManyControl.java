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
package edu.ndsu.eci.tapestry5cayenne.integration.app0.pages;

import java.util.List;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.corelib.components.BeanDisplay;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;

public class TestToManyControl {

  @Inject
  private ObjectContextProvider _provider;

  @SuppressWarnings("unused")
  @Persist
  @Property
  private Artist _artist;

  @Property
  private List<Artist> _artistList;

  @Component(parameters = { "object=artist", "model=artistModel" })
  @SuppressWarnings("unused")
  private BeanDisplay _display;

  @SuppressWarnings("unused")
  @Component(parameters = { "source=artistList", "model=artistModel" })
  private Grid _gridDisplay;

  @Inject
  private BeanModelSource source;

  @Inject
  private Messages messages;

  public BeanModel<Artist> getArtistModel() {
    BeanModel<Artist> ret = source.createDisplayModel(Artist.class, messages);
    ret.include(Artist.PAINTING_LIST.getName(), Artist.PAINTINGS_BY_TITLE.getName());
    return ret;
  }

  @SetupRender
  @SuppressWarnings("unchecked")
  void init() {
    _artistList = _provider.currentContext().performQuery(new EJBQLQuery("select a from Artist a order by a.name"));
    if (!_artistList.isEmpty()) {
      _artist = (Artist) _artistList.get(0);
    }
  }

}
