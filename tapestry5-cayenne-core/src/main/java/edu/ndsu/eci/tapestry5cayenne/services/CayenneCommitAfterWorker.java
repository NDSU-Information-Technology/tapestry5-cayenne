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
package edu.ndsu.eci.tapestry5cayenne.services;

import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;

import edu.ndsu.eci.tapestry5cayenne.annotations.CommitAfter;

/**
 * Exactly analogous to the tapestry-hibernate CommitAfterWorker. That is: any
 * method annotated with @CommitAfer in a tapestry-controlled class (component,
 * page, mixin) will automatically call context.commitChanges() on completion of
 * the method; should commit fail, rollback is performed automatically.
 */
public class CayenneCommitAfterWorker implements ComponentClassTransformWorker2 {

  private final ObjectContextProvider provider;

  private final MethodAdvice advice = new MethodAdvice() {

    public void advise(MethodInvocation invocation) {

      try {
        invocation.proceed();
        provider.currentContext().commitChanges();

      } catch (RuntimeException e) {
        provider.currentContext().rollbackChanges();
        throw e;

      }

    }

  };

  public void transform(PlasticClass plasticClass, TransformationSupport support, MutableComponentModel model) {
    for (final PlasticMethod method : plasticClass.getMethodsWithAnnotation(CommitAfter.class)) {
      method.addAdvice(advice);
    }
  }

  public CayenneCommitAfterWorker(ObjectContextProvider provider) {
    this.provider = provider;
  }

}
