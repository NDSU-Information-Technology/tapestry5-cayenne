/*
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

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.tapestry5cayenne.annotations.Cayenne;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Index {

  @Inject
  @Cayenne
  private ObjectContextProvider _provider;

  public int getCurrentCacheSize() {
    return ((DataContext)BaseContext.getThreadObjectContext()).getObjectStore().getDataRowCache().size();
  }

  public int getMaxCacheSize() {
    return ((DataContext)BaseContext.getThreadObjectContext()).getObjectStore().getDataRowCache().maximumSize();
  }

  public List<ObjEntity> getObjEntities() {
    Collection<ObjEntity> c = _provider.currentContext().getEntityResolver().getObjEntities();
    return new ArrayList<ObjEntity>(c);
  }
}
