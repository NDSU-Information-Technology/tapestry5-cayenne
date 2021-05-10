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

import org.apache.tapestry5.OptionModel;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * An implementation of OptionModel that uses the results of invoking a method
 * as the label.
 * 
 */
public class MethodLabelOptionModel implements OptionModel {

  private final String _label;
  private final Object _value;

  /**
   * @param value
   *          The object represented by the option model.
   * @param label
   *          The method to invoke on the object, representing its label.
   */
  public MethodLabelOptionModel(Object value, Method label) {
    _value = value;
    _label = Labeler.labelForObject(value, label);
  }

  public Map<String, String> getAttributes() {
    return null;
  }

  public String getLabel() {
    return _label;
  }

  public Object getValue() {
    return _value;
  }

  public boolean isDisabled() {
    return false;
  }
}
