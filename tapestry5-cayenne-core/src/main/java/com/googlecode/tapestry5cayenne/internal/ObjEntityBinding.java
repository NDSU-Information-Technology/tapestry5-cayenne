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

import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.internal.bindings.AbstractBinding;
import org.apache.tapestry5.ioc.Location;

/**
 * Binding for ObjEntity. Use as: ent:XXX where XXX is the entity name. For
 * example: ent:User
 * 
 * @author robertz
 *
 */
public class ObjEntityBinding extends AbstractBinding {

  private final ObjEntity entity;
  private final String toString;

  public ObjEntityBinding(Location location, ObjEntity entity, String toString) {
    super(location);
    this.entity = entity;
    this.toString = toString;
  }

  public Object get() {
    return entity;
  }

  public String toString() {
    return toString;
  }
}
