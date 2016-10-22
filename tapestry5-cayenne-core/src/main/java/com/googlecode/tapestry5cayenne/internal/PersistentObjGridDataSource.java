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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SortOrder;
import org.apache.cayenne.query.SelectQuery;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

import com.googlecode.tapestry5cayenne.annotations.Label;

/**
 * Provides a memory-efficient implementation of GridDataSource based on
 * fetching object entities. The coercion from ObjEntity => GridDataSource makes
 * use of this class.
 */
public class PersistentObjGridDataSource implements GridDataSource {

  private final ObjEntity type;
  private SelectQuery query;
  private final Query countQuery;
  private List<?> currentPage;
  private int startIndex;

  public PersistentObjGridDataSource(ObjEntity ent) {
    this.type = ent;
    countQuery = initCountQuery(type);
  }

  public PersistentObjGridDataSource(Class<?> type) {
    this(context().getEntityResolver().lookupObjEntity(type));
  }

  protected Query initCountQuery(ObjEntity type) {
    ObjEntity oent = context().getEntityResolver().lookupObjEntity(type);
    String ejbql = String.format("select count(t0) from %s t0", oent.getName());
    return new EJBQLQuery(ejbql);
  }

  public int getAvailableRows() {
    return runCountQuery();
  }

  @SuppressWarnings("unchecked")
  protected int runCountQuery() {
    List results = context().performQuery(countQuery);
    return ((Long) results.get(0)).intValue();
  }

  @SuppressWarnings("unchecked")
  public Class getRowType() {
    return type.getJavaClass();
  }

  public Object getRowValue(int index) {
    // index is in terms of the total "list" of objects, across all pages.
    // so we have to rebase that to the current page.
    return currentPage.get(index - startIndex);
  }

  public void prepare(int startIndex, int endIndex, List<SortConstraint> sortConstraints) {
    query = new SelectQuery(type);
    this.startIndex = startIndex;
    List<Ordering> orderings = new ArrayList<Ordering>();
    if (!sortConstraints.isEmpty()) {
      for (SortConstraint c : sortConstraints) {
        switch (c.getColumnSort()) {
        case ASCENDING:
          orderings.add(new Ordering(c.getPropertyModel().getPropertyName(), SortOrder.ASCENDING));
          break;
        case DESCENDING:
          orderings.add(new Ordering(c.getPropertyModel().getPropertyName(), SortOrder.DESCENDING));
          break;
        default:
          break;
        }
      }
    }
    Method label = AnnotationFinder.methodForAnnotation(Label.class, type.getJavaClass());
    QuerySortResult rslt = PersistentManagerImpl.querySort(query, label, context(), type.getJavaClass(),
        orderings.toArray(new Ordering[] {}));
    query.setFetchOffset(startIndex);
    query.setFetchLimit(endIndex - startIndex + 1);
    addAdditionalConstraints(query);
    currentPage = context().performQuery(query);
    // problem here: we're fetching /part/ of the list; ordering has to be
    // applied prior to the fetch!
    // FIXME
    rslt.type.sort(currentPage, rslt.ordering, label);
  }

  protected void addAdditionalConstraints(SelectQuery query) {
    // subclasses may override as needed to perform any required "winnowing".
  }

  protected static ObjectContext context() {
    return BaseContext.getThreadObjectContext();
  }

}
