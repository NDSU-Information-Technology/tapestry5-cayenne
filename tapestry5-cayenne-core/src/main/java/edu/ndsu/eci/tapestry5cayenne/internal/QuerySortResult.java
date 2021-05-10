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

/**
 * Data Storage class for holding information about how the query sort should
 * take place.
 * 
 * @author robertz
 *
 */
// note that there still seem to be some quirks wrt tapestry's live service
// reloading. Had to move this
// class out. It works fine in a real app, but when testing against PageTester,
// I was seeing access-related exceptions.
public class QuerySortResult {
  public QuerySortResult() {

  }

  public QuerySortResult(QuerySortType type, Ordering ordering) {
    this.type = type;
    this.ordering = ordering;
  }

  public QuerySortType type;
  public Ordering ordering;
}