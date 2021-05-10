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

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;

/**
 * Enum representing the major (select) query types in cayenne. Allows us to
 * utilize query-specific properties in a query-type-independent manner.
 * 
 * @author robertz
 *
 */
public enum QueryType {

  /**
   * Constant for EJBQLQueries.
   */
  EJBQL(EJBQLQuery.class) {
    public void setPageSize(Query q, int size) {
      ((EJBQLQuery) q).setPageSize(size);
    }
  },
  /**
   * Constant for SQLTemplate queries.
   */
  SQLTEMPLATE(SQLTemplate.class) {
    public void setPageSize(Query q, int size) {
      ((SQLTemplate) q).setPageSize(size);
    }
  },
  /**
   * Constant for SelectQuery queries.
   */
  SELECT(SelectQuery.class) {
    public void setPageSize(Query q, int size) {
      ((SelectQuery) q).setPageSize(size);
    }
  };

  private Class<? extends Query> queryClass;

  private QueryType(Class<? extends Query> queryClass) {
    this.queryClass = queryClass;
  }

  /**
   * Set the pageSize for the query type. If the particular query type in
   * question does not support pageSize, this method should be no-op.
   * 
   * @param q
   *          query on which to set the page size
   * @param size
   *          size to set it to.
   */
  public void setPageSize(Query q, int size) {
    // default implementation is no-op.
  }

  /**
   * Returns the enumeration constant corresponding to the type for the given
   * query.
   * 
   * @param q
   *          query to determine the type of
   * @return the enum type corresponding to the provided query.
   * @throws IllegalArgumentException
   *           if no matching QueryType can be found.
   */
  public static QueryType typeForQuery(Query q) {
    for (QueryType type : values()) {
      if (type.queryClass.isAssignableFrom(q.getClass())) {
        return type;
      }
    }
    throw new IllegalArgumentException("Error resolving query type - unknown type: " + q.getClass().getName());
  }
}
