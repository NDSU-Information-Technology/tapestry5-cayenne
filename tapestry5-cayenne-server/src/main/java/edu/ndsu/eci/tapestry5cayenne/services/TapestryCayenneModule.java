/*
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

import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ServiceOverride;

import edu.ndsu.eci.tapestry5cayenne.annotations.Cayenne;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneCoreModule;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {

  @SuppressWarnings("unchecked")
  public static void bind(ServiceBinder binder) {
    binder.bind(ObjectContextProvider.class, DataContextProviderImpl.class).withMarker(Cayenne.class)
        .withId("DataContext");
  }

  /**
   * Alias the DataContext-based object context provider to
   * ObjectContextProvider.
   * 
   * @param conf
   * @param provider
   */
  @SuppressWarnings("unchecked")
  @Contribute(ServiceOverride.class)
  public static void contributeServiceOverride(MappedConfiguration<Class, Object> conf,
      @Cayenne ObjectContextProvider provider) {
    conf.add(ObjectContextProvider.class, provider);
  }
}
