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

import org.apache.cayenne.CayenneContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.BaseContext;
import org.apache.cayenne.remote.ClientChannel;
import org.apache.cayenne.remote.ClientConnection;
import org.apache.cayenne.remote.hessian.HessianConnection;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateManager;

/**
 * Implementation of provider for CayenneContext.
 *
 * @author Kevin Menard
 * @version 1.0
 */
public class CayenneContextProviderImpl implements ObjectContextProvider {
  private final ApplicationStateManager asm;
  private final String webServiceUrl;
  private final String username;
  private final String password;
  private final String sharedSessionName;

  public CayenneContextProviderImpl(final ApplicationStateManager asm,
      @Inject @Symbol(TapestryCayenneModule.WEB_SERVICE_URL) final String webServiceUrl,
      @Inject @Symbol(TapestryCayenneModule.USERNAME) final String username,
      @Inject @Symbol(TapestryCayenneModule.PASSWORD) final String password,
      @Inject @Symbol(TapestryCayenneModule.SHARED_SESSION_NAME) final String sharedSessionName) {
    this.asm = asm;

    this.webServiceUrl = webServiceUrl;
    this.username = username;
    this.password = password;
    this.sharedSessionName = sharedSessionName;
  }

  public ObjectContext currentContext() {
    try {
      return BaseContext.getThreadObjectContext();
    } catch (final IllegalStateException exception) {
      // make our behavior consistent with DataContextProviderImpl in the server
      // package.
      if (asm.exists(ObjectContext.class)) {
        return asm.get(ObjectContext.class);
      }
      return null;
    }
  }

  public ObjectContext newContext() {
    final ClientConnection conn = new HessianConnection(webServiceUrl, username, password, sharedSessionName);
    final DataChannel channel = new ClientChannel(conn);

    return new CayenneContext(channel);
  }
}
