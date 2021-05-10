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
import org.apache.tapestry5.commons.Location;
import org.apache.tapestry5.internal.bindings.AbstractBinding;

/**
 * Component binding that represents an EJBQLQuery. eg:
 * &lt;t:grid source="ejbq:select a from Artist a"/&gt;
 * 
 * @author robertz
 *
 */
public class EJBQLBinding extends AbstractBinding {

  private final EJBQLQuery query;
  private final String toString;

  public EJBQLBinding(final Location location, EJBQLQuery query, String toString) {
    super(location);
    this.query = query;
    this.toString = toString;
  }

  /*
   * the query doesn't change, but... the query /results/ do. So we're not
   * invariant
   */
  public boolean isInvariant() {
    return false;
  }

  @SuppressWarnings("unchecked")
  public Class getBindingType() {
    return EJBQLQuery.class;
  }

  public Object get() {
    return query;
  }

  public String toString() {
    return toString;
  }

}
