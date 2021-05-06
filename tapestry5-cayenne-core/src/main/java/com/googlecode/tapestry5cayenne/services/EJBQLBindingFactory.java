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
package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.commons.Location;
import org.apache.tapestry5.commons.internal.util.TapestryException;
import org.apache.tapestry5.services.BindingFactory;

import com.googlecode.tapestry5cayenne.internal.EJBQLBinding;

/**
 * EJBQLQuery binding factor, so you can do things like:
 * &lt;t:grid source="select a from Artist a order by a.lastName, a.firstName"/&gt;
 * 
 * @author robertz
 *
 */
public class EJBQLBindingFactory implements BindingFactory {

  public Binding newBinding(String description, ComponentResources container, ComponentResources component,
      String expression, Location location) {
    String toString = String.format("EJBQLBinding[%s %s(%s)]", description, container.getCompleteId(), expression);
    EJBQLQuery query;
    try {
      query = new EJBQLQuery(expression);
    } catch (Exception e) {
      throw new TapestryException("Unable to convert " + expression + " into an EJBQLQuery", e);
    }
    return new EJBQLBinding(location, query, toString);
  }

}
