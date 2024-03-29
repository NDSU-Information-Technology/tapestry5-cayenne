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

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.http.services.Request;
import org.apache.tapestry5.ioc.annotations.Inject;

import edu.ndsu.eci.tapestry5cayenne.ContextType;
import edu.ndsu.eci.tapestry5cayenne.annotations.OCType;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;

public class InjectObjectContextTestPage {

  @Inject
  private ObjectContextProvider provider;

  @Inject
  private ObjectContext currentNoOCType;

  @Inject
  @OCType(ContextType.CURRENT)
  private ObjectContext currentWithOCType;

  @Inject
  @OCType(ContextType.NEW)
  private ObjectContext newContext;

  @Inject
  @OCType(ContextType.CHILD)
  private ObjectContext childContext;

  public boolean isCurrentReallyCurrent() {
    provider.currentContext().setUserProperty("testprop", new Object());
    return currentNoOCType.getUserProperty("testprop") != null;

  }

  public boolean isNoAnnotationSameAsOCTypeCurrent() {
    return currentWithOCType.getUserProperty("testprop") != null;
  }

  public boolean isChildAnnotationChildOfCurrent() {
    return childContext.getChannel().equals(provider.currentContext());
  }

  public boolean isNewContextReallyNew() {
    if (newContext.getUserProperty("newcontextprop") == null) {
      newContext.setUserProperty("newcontextprop", "new" + System.currentTimeMillis());
    }
    return !newContext.equals(provider.currentContext()) && !newContext.equals(childContext)
        && !newContext.getChannel().equals(provider.currentContext()) && newContext.getUserProperty("testprop") == null;
  }

  public String getNewContextProp() {
    return (String) newContext.getUserProperty("newcontextprop");
  }

  @Inject
  private Request request;

  public void onActionFromInvalidateSession() {
    request.getSession(false).invalidate();
  }

}
