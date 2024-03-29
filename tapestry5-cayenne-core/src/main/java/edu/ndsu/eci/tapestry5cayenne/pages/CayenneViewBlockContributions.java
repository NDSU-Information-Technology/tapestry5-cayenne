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
package edu.ndsu.eci.tapestry5cayenne.pages;

import java.util.Collection;
import java.util.Map;

import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.commons.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PropertyOutputContext;

import edu.ndsu.eci.tapestry5cayenne.internal.Labeler;

/**
 * 
 * Contains all of the custom "bean display" blocks used for displaying cayenne
 * objects.
 * 
 * @author robertz
 *
 */
public class CayenneViewBlockContributions {

  @Environmental
  private PropertyOutputContext _context;

  @Inject
  private Messages _messages;

  @Property
  @SuppressWarnings("unused")
  private Object _tmp;

  public Collection<?> getCollection() {
    Object val = _context.getPropertyValue();
    if (!(val instanceof Collection)) {
      bad_state("bad_state_collection", val);
    }
    return (Collection<?>) _context.getPropertyValue();
  }

  public String getToOneString() throws Exception {
    return Labeler.htmlLabelForObject(_context.getPropertyValue());
  }

  public Collection<?> getMapValues() {
    Object val = _context.getPropertyValue();
    if (!(val instanceof Map)) {
      bad_state("bad_state_map", val);
    }
    return ((Map<?, ?>) val).values();
  }

  private void bad_state(String key, Object val) {
    throw new IllegalStateException(
        _messages.format(key, (val == null ? null : val.getClass().getName()), _context.getPropertyName()));
  }

}
