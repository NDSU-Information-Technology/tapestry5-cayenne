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
package edu.ndsu.eci.tapestry5cayenne.internal;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

/**
 * Utility class for quick conversion from string to Ordering.
 * 
 * @author robertz
 * 
 */
public class OrderingUtils {

  private OrderingUtils() {
  }

  /**
   * Converts the provided property names to orderings, assuming an ordering of
   * ascending for all properties.
   * 
   * @param vals values to order
   * @return an array of Ordering objects.
   */
  public static Ordering[] stringToOrdering(String... vals) {
    return stringToOrdering(true, vals);
  }

  /**
   * Converts the provided property names to orderings. All orderings will be
   * ascending or descending, according to the ascending parameter.
   * 
   * @param ascending if ascending
   * @param vals values
   * @return orderings
   */
  public static Ordering[] stringToOrdering(boolean ascending, String... vals) {
    Ordering[] o = new Ordering[vals.length];
    for (int i = 0; i < o.length; i++) {
      o[i] = new Ordering(vals[i], ascending ? SortOrder.ASCENDING : SortOrder.DESCENDING);
    }
    return o;
  }

}
