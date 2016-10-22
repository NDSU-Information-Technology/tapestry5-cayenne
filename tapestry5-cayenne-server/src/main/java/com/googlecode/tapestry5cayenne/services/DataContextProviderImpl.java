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
package com.googlecode.tapestry5cayenne.services;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.BaseContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.conf.Configuration;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ApplicationStateManager;

import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

/**
 * Implementation of provider for DataContext.
 *
 * @author Robert Zeigler
 * @version 1.0
 */
public class DataContextProviderImpl implements ObjectContextProvider {

  private final ApplicationStateManager asm;

  public DataContextProviderImpl(final ApplicationStateManager asm) {
    this.asm = asm;
  }

  public ObjectContext currentContext() {
    try {
      return BaseContext.getThreadObjectContext();
    } catch (final IllegalStateException exception) {
      // note that asm is a thread/request-specific service
      // there are fringe cases, thus far only encountered during testing, where
      // the request is over, but we want to access the session-associated
      // data context, if any. So we fall back to check for said ObjectContext.
      if (asm.exists(ObjectContext.class)) {
        return asm.get(ObjectContext.class);
      }
      // it would be nice to throw an exception here, but that's a fundamental
      // change of behavior for this service.
      return null;
    }
  }

  public ObjectContext newContext() {
    return DataContext.createDataContext();
  }

  /**
   * Shut the service down gracefully.
   * 
   * @param shutdownHub
   *          registry shutdown hub service
   */
  @PostInjection
  public void shutdownService(RegistryShutdownHub shutdownHub) {
    shutdownHub.addRegistryShutdownListener(new Runnable() {
      public void run() {
        Configuration.getSharedConfiguration().shutdown();
      }
    });
  }

}
