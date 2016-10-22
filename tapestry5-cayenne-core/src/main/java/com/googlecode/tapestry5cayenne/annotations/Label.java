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
 * Used to mark a particular method as the "label" for the object type. The
 * label is used when rendering selects for objects, as a short description for
 * the object, etc.
 * 
 * @author robertz
 * 
 *         toString could be used for this purpose (and t5cayenne will fall back
 *         to using toString if no @Label'ed method is found) but it is often
 *         desirable to /not/ override cayenne's default toString
 *         implementation; the default implementation is useful for debugging
 *         purposes, whereas label is explicitly meant for the purpose of
 *         displaying the entity to a user.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Label {
}
