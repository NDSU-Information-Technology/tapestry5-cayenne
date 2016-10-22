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

import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Perfectly analogous to the tapestry-hibernate "CommitAfter" annotation. Apply
 * this to a method where you want the current object context to be committed.
 * On failure, the context will roll back and the exception wrapped and rethrown
 * in a runtime exception.
 * 
 * Generally, you should handle committing yourself to ensure that you handle
 * errors in a meaningful way. However, this annotation is useful for rapid
 * prototyping.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
@Documented
public @interface CommitAfter {

}
