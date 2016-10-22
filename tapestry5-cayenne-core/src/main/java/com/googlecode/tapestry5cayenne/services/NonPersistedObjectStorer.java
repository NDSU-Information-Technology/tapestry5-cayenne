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

import org.apache.cayenne.Persistent;

/**
 * Contract for converting uncommitted Persistent objects to client-side
 * representations and back.
 * 
 * @author robertz
 *
 */
public interface NonPersistedObjectStorer {

  /**
   * Stores a non-persisted persistent object for later retrieval.
   * 
   * @return a String key for the dao.
   */
  String store(Persistent dao);

  /**
   * Retrieves the non-persisted persistent object from storage.
   * 
   * @param key
   *          the key as returned from store
   * @param objEntityName
   *          the name of the object entity.
   */
  Persistent retrieve(String key, String objEntityName);

}
