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
package edu.ndsu.eci.tapestry5cayenne.internal;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.cayenne.query.Ordering;

/**
 * QuerySortType is used by the RelationshipSelectModel for sorting of
 * relationship lists. The type determines how those lists will be sorted, and
 * determines whether the sort will occur in memory or via the db.
 * 
 * @author robertz
 */
public enum QuerySortType {
  /**
   * No sort should be performed
   */
  NOSORT,
  /**
   * Sort in-memory via a "label" method.
   */
  METHOD {
    @Override
    public void sort(final List<?> results, final Ordering ordering, final Method label) {
      Collections.sort(results, new Comparator<Object>() {

        @SuppressWarnings("unchecked")
        public int compare(Object o1, Object o2) {
          Object lbl1;
          Object lbl2;
          try {
            lbl1 = label.invoke(o1);
            lbl2 = label.invoke(o2);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
          if (lbl1 instanceof Comparable) {
            return ((Comparable) lbl1).compareTo(lbl2);
          }
          return lbl1.toString().compareTo(lbl2.toString());
        }

      });
    }
  },
  /**
   * Sort in-memory via an Ordering.
   */
  ORDERING {
    @Override
    public void sort(final List<?> results, final Ordering ordering, final Method label) {
      ordering.orderList(results);
    }
  },
  /**
   * Sort in-memory via direct object comparison (Objects must implement the
   * Comparable interface).
   */
  COMPARABLE {
    @Override
    @SuppressWarnings("unchecked")
    public void sort(final List<?> results, final Ordering ordering, final Method label) {
      Collections.sort((List<Comparable>) results);
    }
  },
  /**
   * Sort in the query to the database.
   */
  QUERY;

  /**
   * Sorts the results.
   * 
   * @param results
   *          The results to sort
   * @param ordering
   *          The ordering to use, if any.
   * @param label
   *          The label to use, if any. Which values may be null depends on the
   *          type of QuerySortType. The default is for this method to do
   *          nothing.
   */
  public void sort(final List<?> results, final Ordering ordering, final Method label) {
  }
}
