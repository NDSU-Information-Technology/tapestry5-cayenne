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

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.commons.Location;
import org.apache.tapestry5.services.BindingFactory;

import edu.ndsu.eci.tapestry5cayenne.internal.ObjEntityBinding;

/**
 * BindingFactory for ObjEntity, so you can do things like:
 * &lt;t:grid source="ent:User"/&gt;
 * 
 * @author robertz
 *
 */
public class ObjEntityBindingFactory implements BindingFactory {

  private final ObjectContextProvider provider;

  public ObjEntityBindingFactory(final ObjectContextProvider p) {
    provider = p;
  }

  public Binding newBinding(String description, ComponentResources container, ComponentResources component,
      String expression, Location location) {
    String toString = String.format("ObjEntityBinding[%s %s(%s)]", description, container.getCompleteId(), expression);
    ObjEntity objEnt = provider.currentContext().getEntityResolver().getObjEntity(expression);
    return new ObjEntityBinding(location, objEnt, toString);
  }

}
