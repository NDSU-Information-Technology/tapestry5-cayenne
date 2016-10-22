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

public class SimpleTestBean implements Comparable<SimpleTestBean> {

  BeanValue value;

  public Integer getTheInt() {
    return value.value;
  }

  public BeanValue value() {
    return value;
  }

  public Integer comparableValue() {
    return getTheInt();
  }

  public int compareTo(SimpleTestBean o) {
    return getTheInt().compareTo(o.getTheInt());
  }

  @Override
  public String toString() {
    return value.toString();
  }

}

class BeanValue {
  Integer value;

  @Override
  public String toString() {
    return value.toString();
  }
}
