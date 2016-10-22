package com.googlecode.tapestry5cayenne.integration.app0.pages;

import org.apache.cayenne.ObjectContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import com.googlecode.tapestry5cayenne.ContextType;
import com.googlecode.tapestry5cayenne.annotations.OCType;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

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
      return !newContext.equals(provider.currentContext())
          && !newContext.equals(childContext)
          && !newContext.getChannel().equals(provider.currentContext())
          && newContext.getUserProperty("testprop") == null;
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
