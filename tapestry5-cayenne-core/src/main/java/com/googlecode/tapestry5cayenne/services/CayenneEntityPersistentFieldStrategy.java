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
package com.googlecode.tapestry5cayenne.services;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.internal.services.AbstractSessionPersistentFieldStrategy;
import org.apache.tapestry5.services.PersistentFieldChange;
import org.apache.tapestry5.services.PersistentFieldStrategy;
import org.apache.tapestry5.services.Request;

import com.googlecode.tapestry5cayenne.annotations.Cayenne;

/**
 * Provides a strategy for use with {@link Persist} for Persistent entities.
 * Stores only an object identifier in the HttpSession, rather than serializing
 * the entire object tree into the session.
 * 
 * @author robertz
 *
 */
public class CayenneEntityPersistentFieldStrategy implements PersistentFieldStrategy {

  private final ValueEncoder<Persistent> _encoder;
  private final PersistentFieldStrategy _strategy;

  public CayenneEntityPersistentFieldStrategy(final Request request, @Cayenne final ValueEncoder<Persistent> encoder) {
    _encoder = encoder;
    _strategy = new CayenneEntityStrategy(request);
  }

  public void discardChanges(String pageName) {
    _strategy.discardChanges(pageName);
  }

  public Collection<PersistentFieldChange> gatherFieldChanges(String pageName) {
    Collection<PersistentFieldChange> changes = new ArrayList<PersistentFieldChange>();
    for (PersistentFieldChange change : _strategy.gatherFieldChanges(pageName)) {
      if (change.getValue() == null) {
        changes.add(change);
        continue;
      }
      changes.add(new CayennePersistentChange(change.getComponentId(), change.getFieldName(),
          _encoder.toValue(change.getValue().toString())));
    }
    return changes;
  }

  public void postChange(String pageName, String componentId, String fieldName, Object newValue) {
    if (newValue != null) {
      if (newValue instanceof Persistent) {
        newValue = _encoder.toClient((Persistent) newValue);
      } else {
        throw new IllegalArgumentException(
            "CayenneEntityPersistentFieldStrategy may only be used on objects of type org.apache.cayenne.Persistent");
      }
    }
    _strategy.postChange(pageName, componentId, fieldName, newValue);
  }

  private static final class CayenneEntityStrategy extends AbstractSessionPersistentFieldStrategy {
    public CayenneEntityStrategy(Request r) {
      super("CayenneEntity:", r);
    }
  }

  private static final class CayennePersistentChange implements PersistentFieldChange {

    private String _componentId;
    private String _fieldName;
    private Persistent _value;

    CayennePersistentChange(String componentId, String fieldName, Persistent value) {
      _componentId = componentId;
      _fieldName = fieldName;
      _value = value;
    }

    public String getComponentId() {
      return _componentId;
    }

    public String getFieldName() {
      return _fieldName;
    }

    public Object getValue() {
      return _value;
    }
  }
}
