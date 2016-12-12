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

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.ObjectLocator;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.apache.tapestry5.ioc.services.PropertyShadowBuilder;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.PlasticField;

import com.googlecode.tapestry5cayenne.ContextType;
import com.googlecode.tapestry5cayenne.annotations.Cayenne;
import com.googlecode.tapestry5cayenne.annotations.OCType;
import com.googlecode.tapestry5cayenne.internal.ObjectContextWrapper;

import org.apache.tapestry5.services.transform.InjectionProvider2;

/**
 * Provides an InjectionProvider so pages and components can @Inject an
 * ObjectContext directly. Wanted to do this as an ObjectProvider, but it has
 * too many dependencies on "late" binding services that result in cyclic
 * dependencies. InjectionProvider isn't so central to tapestry ioc, so it's
 * easier to use and accomplishes the same goal (except that it only works for
 * page and component classes, not services).
 * 
 * @author robertz
 *
 */
public class ObjectContextInjectionProvider implements InjectionProvider2 {

  private final ObjectContextProvider provider;
  private final PerthreadManager threadManager;
  private final PropertyShadowBuilder shadowBuilder;

  /**
   * @param provider the ObjectContextProvider which is ultimately used to grab the appropriate context.
   * @param manager manager
   * @param shadowBuilder shadow builder
   */
  // putting @Cayenne on the parameter is the difference between T5-ioc
  // dying a gruesome death (recursive dependencies), and starting happily.
  public ObjectContextInjectionProvider(@Cayenne final ObjectContextProvider provider, PerthreadManager manager,
      PropertyShadowBuilder shadowBuilder) {
    this.provider = provider;
    this.threadManager = manager;
    this.shadowBuilder = shadowBuilder;
  }

  public boolean provideInjection(PlasticField field, ObjectLocator locator, MutableComponentModel componentModel) {
    try {
      if (!(ObjectContext.class.isAssignableFrom(Class.forName(field.getTypeName())))) {
        return false;
      }
    } catch (ClassNotFoundException e) {
      // this is probably a primitive (boolean)
      return false;
    }

    OCType t = field.getAnnotation(OCType.class);
    ContextType ctype = t == null ? ContextType.CURRENT : t.value();
    ObjectContext toInject;
    switch (ctype) {
    case NEW:
      toInject = shadowBuilder.build(new ObjectContextWrapper(threadManager, provider), "newContext",
          ObjectContext.class);
      break;
    case CHILD:
      toInject = shadowBuilder.build(new ObjectContextWrapper(threadManager, provider), "childContext",
          ObjectContext.class);
      break;
    default:
      toInject = shadowBuilder.build(new ObjectContextWrapper(threadManager, provider), "currentContext",
          ObjectContext.class);
    }
    field.inject(toInject);
    return true;
  }

}
