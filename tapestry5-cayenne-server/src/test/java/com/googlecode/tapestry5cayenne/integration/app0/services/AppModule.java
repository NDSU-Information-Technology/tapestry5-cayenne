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
package com.googlecode.tapestry5cayenne.integration.app0.services;

import java.util.List;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ApplicationInitializer;
import org.apache.tapestry5.services.ApplicationInitializerFilter;
import org.apache.tapestry5.services.Context;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.services.TapestryCayenneModule;

@SubModule(TapestryCayenneModule.class)
public class AppModule {

  public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
    configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
    configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
  }

  public static void contributeApplicationInitializer(OrderedConfiguration<ApplicationInitializerFilter> conf) {
    conf.add("setupdb", new ApplicationInitializerFilter() {
      public void initializeApplication(Context context, ApplicationInitializer handler) {
        try {
          TestUtils.setupdb();
          ObjectContext oc = BaseContext.getThreadObjectContext();
          // put in some artists and paintings...
          List<Artist> artists = TestUtils.basicData(oc);
          TestUtils.addPaintings(artists.get(0), 15, oc);
          TestUtils.addPaintings(artists.get(1), 18, oc);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
        handler.initializeApplication(context);
      }
    });
  }
}
