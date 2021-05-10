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

import java.util.Collection;

import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.util.CayenneMapEntry;
import org.apache.cayenne.util.Util;
import org.apache.tapestry5.beanmodel.BeanModel;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.services.Environment;

import edu.ndsu.eci.tapestry5cayenne.annotations.Cayenne;
import edu.ndsu.eci.tapestry5cayenne.internal.BeanModelTypeHolder;

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
    ObjEntity ent = _provider.currentContext().getEntityResolver().getObjEntity(type);

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
        model = model.exclude(javaToUnderscored(entry.getName()) + suffix);
    }
    return model;
  }
  
  /**
   * Converts a String name to a String forllowing java convention for the static final
   * variables. E.g. "abcXyz" will be converted to "ABC_XYZ".
   * 
   * was removed from later version of Cayenne, so copied from previous version
   */
  public static String javaToUnderscored(String name) {
    if (name == null) {
        return null;
    }

    // clear of non-java chars. While the method name implies that a passed identifier
    // is pure Java, it is used to build pk columns names and such, so extra safety
    // check is a good idea
    name = Util.specialCharsToJava(name);

    char charArray[] = name.toCharArray();
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < charArray.length; i++) {
        if ((Character.isUpperCase(charArray[i])) && (i != 0)) {

            char prevChar = charArray[i - 1];
            if ((Character.isLowerCase(prevChar))) {
                buffer.append("_");
            }
        }

        buffer.append(Character.toUpperCase(charArray[i]));
    }

    return buffer.toString();
}
  
}
