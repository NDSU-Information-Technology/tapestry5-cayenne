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
package com.googlecode.tapestry5cayenne.components;

import java.util.Collection;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.internal.Labeler;

/**
 * A component for viewing a "toMany" relationship. The output depends on the
 * number of items in the relationship. With many items, a simple message is
 * displayed, such as: 25 associated items With few associated items, each item
 * is displayed as an entry in an unordered list. The list is styled to scroll
 * if it overflows its bounds. The associated ul has a css class of
 * t-cayenne-tomany-display, for easy css selection. The text displayed for each
 * item is the result of invoking the method labeled with the {@link Label}
 * annotation. If no method is annotated, the text displayed will be the result
 * of invoking "toString" on the object.
 * 
 * @author robertz
 */
@Import(stylesheet = "ToManyViewer.css")
public class ToManyViewer {

  /**
   * Threshold at which generic text (x associated items) is displayed, rather
   * than listing the individual elements.
   */
  private static final int SIZE_LIMIT = 20;

  @Property
  private Object _tmp;

  @Parameter
  @Property
  /**
   * The collection representing the toMany relationship.
   */
  private Collection<?> _source;

  @Inject
  private Messages _messages;

  public boolean isSmallEnough() {
    return _source.size() < SIZE_LIMIT;
  }

  public String getStringFromTmp() {
    return Labeler.htmlLabelForObject(_tmp);
  }

  public String getToManyString() throws Exception {
    return _messages.format("associated_items", _source.size());
  }

  public boolean isEmpty() {
    return _source.isEmpty();
  }
}
