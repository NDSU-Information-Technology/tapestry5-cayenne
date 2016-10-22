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

import com.googlecode.tapestry5cayenne.ContextType;

/**
 * Marker annotation for use in conjunction with tapestry's Inject annotation
 * for injecting contexts. When specifying: <code>
 * &#64;Inject
 * private ObjectContext oc;
 * </code> t5cayenne will normally inject the "current" context (normally
 *         associated with the user's session). This behavior can be modified
 *         with OCType annotation: <code>
 * &#64;Inject
 * &#64;OCType(ContextType.NEW)
 * private ObjectContext oc;
 * </code> In this case, a newly-created ObjectContext would be used. See
 *                          {@link ContextType} for valid values and their
 *                          meanings.
 * 
 * @author robertz
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OCType {

  ContextType value() default ContextType.CURRENT;
}
