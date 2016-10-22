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
package com.googlecode.tapestry5cayenne.internal;

/**
 * Bean for use with the environmental service. Used to allow communication
 * between the CayenneBeanModelSource, and DataTypeAnalyzers, particularly the
 * CayenneDataTypeAnalyzer.
 * 
 * @author robertz
 *
 */
public class BeanModelTypeHolder {

  private final Class<?> _type;

  public BeanModelTypeHolder(final Class<?> type) {
    _type = type;
  }

  /**
   * @return the type of object for which a bean model is being built
   */
  public Class<?> getType() {
    return _type;
  }
}
