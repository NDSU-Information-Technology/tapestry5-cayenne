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

import java.io.IOException;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.http.services.RequestFilter;
import org.apache.tapestry5.http.services.RequestHandler;
import org.apache.tapestry5.http.services.Response;
import org.apache.tapestry5.services.ApplicationStateManager;

/**
 * Provides a RequestFilter which ensures that there is a DataContext associated
 * with the current request. Currently uses a session-based strategy.
 * 
 * @author robertz
 */
public class CayenneRequestFilter implements RequestFilter {

  private final ApplicationStateManager asm;
  private final ObjectContextProvider provider;

  public CayenneRequestFilter(final ApplicationStateManager asm, final ObjectContextProvider provider) {
    this.asm = asm;
    this.provider = provider;
  }

  public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
    ObjectContext oc;
    if (asm.exists(ObjectContext.class)) {
      oc = asm.get(ObjectContext.class);
    } else {
      oc = provider.currentContext() != null ? provider.currentContext() : provider.newContext();
      asm.set(ObjectContext.class, oc);
    }

    BaseContext.bindThreadObjectContext(oc);
    try {
      return handler.service(request, response);
    } finally {
      BaseContext.bindThreadObjectContext(null);
    }
  }
}
