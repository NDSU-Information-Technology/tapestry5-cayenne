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
package com.googlecode.tapestry5cayenne.components;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.corelib.components.Select;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PropertyEditContext;

import com.googlecode.tapestry5cayenne.PersistentEntitySelectModel;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

/**
 * Generic editor for to-one relationships. Builds a select list of each object
 * of the type of field. For instance, a painting has an Artist field; this
 * component will build an editor for the Artist field by building a select with
 * each artist in the system listed in the select.
 * 
 * @author robertz
 *
 */
public class ToOneEditor implements Field {

  @Environmental
  private PropertyEditContext _context;

  @Inject
  private PersistentManager _manager;

  @SuppressWarnings("unused")
  @Component(parameters = { "model=model", "value=value", "validate=prop:validation" })
  private Select _toOneList;

  public Persistent getValue() {
    return (Persistent) _context.getPropertyValue();
  }

  public void setValue(Persistent value) {
    _context.setPropertyValue(value);
  }

  @SuppressWarnings("unchecked")
  public SelectModel getModel() {
    Class<?> type = _context.getPropertyType();
    return new PersistentEntitySelectModel(type, _manager);
  }

  public FieldValidator<?> getValidation() {
    return _context.getValidator(_toOneList);
  }

  public String getControlName() {
    return _toOneList.getControlName();
  }

  public String getLabel() {
    return _context.getLabel();
  }

  public boolean isDisabled() {
    return _toOneList.isDisabled();
  }

  public boolean isRequired() {
    return _toOneList.isRequired();
  }

  public String getClientId() {
    return _toOneList.getClientId();
  }
}
