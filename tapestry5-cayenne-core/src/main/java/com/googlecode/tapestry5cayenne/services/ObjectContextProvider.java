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

/**
 * Defines an interface through which Cayenne a new or existing ObjectContext
 * may be obtained.
 *
 * @author Robert Zeigler
 */
public interface ObjectContextProvider {

  /**
   * @return An existing data context, associated with the current request, if
   *         possible.
   */
  ObjectContext currentContext();

  /**
   * @return a new ObjectContext
   */
  ObjectContext newContext();

}
