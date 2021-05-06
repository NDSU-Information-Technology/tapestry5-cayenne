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

import java.util.Collection;

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.util.CayenneMapEntry;
import org.apache.cayenne.util.NameConverter;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.services.Environment;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.internal.BeanModelTypeHolder;

/**
 * Provides a cayenne-specific implementation of BeanModelSource. This is used
 * to override the default implementation. It is capable of handling Persistent
 * and non-persistent objects. It ensures that extraneous properties inherited
 * from, eg, CayenneDataObject don't show up in the default model.
 * 
 * @author robertz
 *
 */
@Marker(Cayenne.class)
public class CayenneBeanModelSource implements BeanModelSource {

  private final BeanModelSource _source;
  private final ObjectContextProvider _provider;
  private final Environment _environment;

  private static final String[] defaultExcludes = new String[] { "persistenceState", "snapshotVersion", "DEFAULT_VERSION" };

  public CayenneBeanModelSource(@InjectService("BeanModelSource") final BeanModelSource source,
      final ObjectContextProvider provider, final Environment environment) {
    _source = source;
    _provider = provider;
    _environment = environment;
  }

  public <T> BeanModel<T> createEditModel(Class<T> type, Messages messages) {
    return create(type, true, messages);
  }

  public <T> BeanModel<T> createDisplayModel(Class<T> type, Messages messages) {
    return create(type, false, messages);
  }

  @SuppressWarnings("unchecked")
  public <T> BeanModel<T> create(Class<T> type, boolean filterReadOnlyProperties, Messages messages) {
    _environment.push(BeanModelTypeHolder.class, new BeanModelTypeHolder(type));
    BeanModel<T> model = filterReadOnlyProperties ? _source.createEditModel(type, messages)
        : _source.createDisplayModel(type, messages);
    _environment.pop(BeanModelTypeHolder.class);
    model.getBeanType();
    ObjEntity ent = _provider.currentContext().getEntityResolver().lookupObjEntity(type);

    if (ent == null) {
      return model;
    }
    
    model = model.exclude(defaultExcludes);
    //make sure to exclude all public static XYZ_PROPERTY values. We can figure out what to exclude by checking the list of properties/relationships...
    //since Tapestry 5.3...
    model = excludeProps(model,ent.getAttributes(), "_PROPERTY");
    model = excludeProps(model, ent.getRelationships(), "_PROPERTY");
    return excludeProps(model, ent.getPrimaryKeys(), "_PK_COLUMN");
   
  }

  private <T> BeanModel<T> excludeProps(BeanModel<T> model, Collection<? extends CayenneMapEntry> entries, String suffix) {
    for (CayenneMapEntry entry : entries) {
        model = model.exclude(NameConverter.javaToUnderscored(entry.getName()) + suffix);
    }
    return model;
  }
  
}
