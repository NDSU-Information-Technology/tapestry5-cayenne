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
package edu.ndsu.eci.tapestry5cayenne.model;

import java.util.ArrayList;
import java.util.List;

import edu.ndsu.eci.tapestry5cayenne.annotations.Label;
import edu.ndsu.eci.tapestry5cayenne.model.auto._Artist;

@SuppressWarnings("serial")
public class Artist extends _Artist implements Comparable<Artist> {

  @Override
  @Label
  public String getName() {
    return super.getName();
  }

  public Integer getNumPaintings() {
    return getPaintingList().size();
  }

  public int numPaintings() {
    return getPaintingList().size();
  }

  public int compareTo(Artist o) {
    return getName().compareTo(o.getName());
  }

}
