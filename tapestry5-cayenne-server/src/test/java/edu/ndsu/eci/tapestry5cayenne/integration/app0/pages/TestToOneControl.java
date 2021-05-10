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
package edu.ndsu.eci.tapestry5cayenne.integration.app0.pages;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanDisplay;
import org.apache.tapestry5.corelib.components.BeanEditForm;

import edu.ndsu.eci.tapestry5cayenne.model.Painting;

public class TestToOneControl {

  @Persist
  @Property
  @SuppressWarnings("unused")
  private Painting _painting;

  @SuppressWarnings("unused")
  @Component(parameters = { "object=painting" })
  private BeanEditForm _form;

  @SuppressWarnings("unused")
  @Component(parameters = { "object=painting" })
  private BeanDisplay _display;

}
