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
package com.googlecode.tapestry5cayenne.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify the default sort order for a class. Used when providing lists
 * of objects, such as that provided by PersistentEntitySelectModel.
 * 
 * @author robertz
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultOrder {
  /**
   * An array of mapped property paths, specifying the orderings to use by
   * default for the annotated type.
   * 
   * @return An array of cayenne-mapped property paths
   */
  String[] value();

  /**
   * Allows more control over the ordering by specifying the sort direction
   * (true => ascending; false => descending). This can either be a single
   * value, representing the sort order for every specified property, or it can
   * be an array of the same length as orderings. An array of any other length
   * will result in a runtime exception.
   * 
   * @return An array of booleans representing whether the corresponding
   *         orderings should be ascending or descending.
   */
  boolean[] ascending() default { true };
}
