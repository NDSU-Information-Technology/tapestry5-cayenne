/*
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

import junit.framework.Assert;

import org.apache.cayenne.access.DataContext;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.commons.AnnotationProvider;
import org.apache.tapestry5.http.services.RequestFilter;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.modules.TapestryModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.annotations.Cayenne;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneModule;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Test to ensure that the TapestryCayenne module is properly contributing all
 * services that it needs to.
 * 
 * @author robertz
 *
 */
@Test(groups = "all")
public class TestTapestryCayenneModule extends Assert {

  private Registry _registry;

  @BeforeClass
  @SuppressWarnings("unused")
  private void init_registry() {
    RegistryBuilder blder = new RegistryBuilder();
    blder.add(TapestryCayenneModule.class, TapestryModule.class, TestModule.class);
    _registry = blder.build();
  }

  @AfterTest
  @SuppressWarnings("unused")
  private void shutdown_registry() {
    _registry.shutdown();
  }

  @SuppressWarnings("unchecked")
  public void hasValueEncoderWithId() {
    ValueEncoder encoder = _registry.getService("CayenneEntityEncoder", ValueEncoder.class);
    assertNotNull(encoder);
  }

  @SuppressWarnings("unchecked")
  public void hasValueEncoderWithMarker() {
    ValueEncoder encoder = _registry.getObject(ValueEncoder.class,
        new SimpleAnnotationProvider<Cayenne>(Cayenne.class));
    assertNotNull(encoder);
  }

  public void hasDataContextProviderWithId() {
    ObjectContextProvider provider = _registry.getService("DataContext", ObjectContextProvider.class);
    assertNotNull(provider);
    assertTrue(provider.newContext() instanceof DataContext);
  }

  public void hasDataContextProviderWithMarker() {
    ObjectContextProvider provider = _registry.getObject(ObjectContextProvider.class,
        new SimpleAnnotationProvider<Cayenne>(Cayenne.class));
    assertNotNull(provider);
    assertTrue(provider.newContext() instanceof DataContext);
  }

  @Test(enabled = false)
  // TODO (KJM 04/15/08) This test needs to move to the client module.
  public void hasCayenneContextProviderWithId() {
    ObjectContextProvider provider = _registry.getService("CayenneContext", ObjectContextProvider.class);
    assertNotNull(provider);
    // can't assert anything at the moment since we're not configuring the
    // CayenneContextProviderImpl.
  }

  public void hasRequestFilterWithId() {
    RequestFilter filter = _registry.getService("CayenneFilter", RequestFilter.class);
    assertNotNull(filter);
  }

  public void hasRequestFilterWithMarker() {
    RequestFilter filter = _registry.getObject(RequestFilter.class,
        new SimpleAnnotationProvider<Cayenne>(Cayenne.class));
    assertNotNull(filter);
  }

}

class SimpleAnnotationProvider<A extends Annotation> implements AnnotationProvider, InvocationHandler {
  private Class<A> _clazz;

  SimpleAnnotationProvider(Class<A> annotationClass) {
    _clazz = annotationClass;
  }

  @SuppressWarnings("unchecked")
  public <T extends Annotation> T getAnnotation(Class<T> cls) {
    if (_clazz.isAssignableFrom(cls)) {
      try {
        Class<?>[] ifc = new Class[] { _clazz };
        return (T) Proxy.newProxyInstance(_clazz.getClassLoader(), ifc, this);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (method.getName().equals("annotationType")) {
      return _clazz;
    }
    throw new UnsupportedOperationException(method.getName());
  }

}
