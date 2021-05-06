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

import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import com.googlecode.tapestry5cayenne.annotations.Cayenne;

@SubModule(TapestryCayenneCoreModule.class)
public class TapestryCayenneModule {
  /**
   * Configuration key for setting the URL for the Cayenne Web Service that the
   * client should connect to.
   */
  public static final String WEB_SERVICE_URL = "tapestrycayenne.client.web_service_url";

  /**
   * Configuration key for setting the username to use in basic auth to the
   * Cayenne Web Service.
   */
  public static final String USERNAME = "tapestrycayenne.client.username";

  /**
   * Configuration key for setting the password to use in basic auth ot the
   * Cayenne Web Service.
   */
  public static final String PASSWORD = "tapestrycayenne.client.password";

  /**
   * Configuration key for setting the Hessian shared session name.
   */
  public static final String SHARED_SESSION_NAME = "tapestrycayenne.client.shared_session_name";

  public static void contributeFactoryDefaults(final MappedConfiguration<String, String> conf) {
    conf.add(WEB_SERVICE_URL, "http://localhost:8080/cws");
    conf.add(USERNAME, "");
    conf.add(PASSWORD, "");
    conf.add(SHARED_SESSION_NAME, "");
  }

  @SuppressWarnings("unchecked")
  public static void bind(final ServiceBinder binder) {
    binder.bind(ObjectContextProvider.class, CayenneContextProviderImpl.class).withMarker(Cayenne.class).withId("CayenneContext");
  }
}
