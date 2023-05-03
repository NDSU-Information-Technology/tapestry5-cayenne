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

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.BaseContext;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.tapestry5.ioc.annotations.Symbol;

import edu.ndsu.eci.tapestry5cayenne.T5CayenneConstants;

/**
 * Implementation of provider for DataContext.
 *
 * @author Robert Zeigler
 * @version 1.0
 */
public class DataContextProviderImpl implements ObjectContextProvider {

  private final ServerRuntime serverRuntime;

  public DataContextProviderImpl(@Symbol(T5CayenneConstants.PROJECT_FILE) String projectFile) {
    try {
      this.serverRuntime = ServerRuntime.builder().addConfig(projectFile).build();
    } catch (Exception e) {
      //cayenne 3.1M3 introduces multiple project files, and the default naming isn't cayenne.xml anymore.
      //so if there's an exception here, it's most likely a config file not found, or other exception.
      throw new RuntimeException("Exception configuring Cayenne server runtime using cayenne project file: " + projectFile + ". Have you tried overriding the default (cayenne.xml) value for the T5CayenneConstants.PROJECT_FILE symbol?", e);
    }
  }

  public ObjectContext currentContext() {
    try {
      ObjectContext context = BaseContext.getThreadObjectContext();
      return context;
    } catch (final IllegalStateException exception) {
      // it would be nice to throw an exception here, but that's a fundamental
      // change of behavior for this service.
      return newContext();
    }
  }

  public ObjectContext newContext() {
    return serverRuntime.newContext();
  }

  @Override
  public ObjectContext newChildContext(DataChannel parentChannel) {
    return serverRuntime.newContext(parentChannel);
  }

}
