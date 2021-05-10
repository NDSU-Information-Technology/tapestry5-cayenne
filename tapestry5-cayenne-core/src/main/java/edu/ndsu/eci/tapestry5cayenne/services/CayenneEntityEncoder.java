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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.commons.services.TypeCoercer;
import org.apache.tapestry5.ioc.annotations.Marker;

import edu.ndsu.eci.tapestry5cayenne.annotations.Cayenne;

/**
 * Basic Persistent ValueEncoder. This works for objects which implements
 * Persistent. If you're using the POJO facilities of cayenne, you will need to
 * contribute your own value encoders. This ValueEncoder can handle single or
 * multi-column primary keys.
 * 
 * @author Robert Zeigler
 */
@Marker(Cayenne.class)
public class CayenneEntityEncoder implements ValueEncoder<Persistent> {

  private final ObjectContextProvider _provider;
  private final Pattern _pattern = Pattern.compile("::");
  private final TypeCoercer _coercer;
  private final NonPersistedObjectStorer _storer;
  private final PersistentManager _manager;
  private final EncodedValueEncrypter _encrypter;

  public CayenneEntityEncoder(final ObjectContextProvider provider, final TypeCoercer coercer,
      final PersistentManager manager, final NonPersistedObjectStorer storer, final EncodedValueEncrypter encrypter) {
    _provider = provider;
    _coercer = coercer;
    _storer = storer;
    _manager = manager;
    _encrypter = encrypter;
  }

  public String toClient(final Persistent obj) {
    if (obj == null) {
      return _encrypter.encrypt("nil");
    }

    if (obj.getObjectId() == null || obj.getObjectId().isTemporary()) {

      final String key = _storer.store(obj);

      // TODO smells of tight coupling here, having to dig through so many
      // layers of objects.
      ObjEntity ent = _provider.currentContext().getEntityResolver().getObjEntity(obj.getClass());
      return _encrypter.encrypt(ent.getName() + "::t::" + key);
    }

    ObjectId id = obj.getObjectId();
    Map<String, Object> idSnap = id.getIdSnapshot();
    if (idSnap.size() == 1) {
      final String pk = _coercer.coerce(org.apache.cayenne.Cayenne.pkForObject(obj), String.class);
      return _encrypter.encrypt(id.getEntityName() + "::" + pk);
    }
    StringBuilder b = new StringBuilder(id.getEntityName());
    for (String key : sortedKeys(idSnap)) {
      String val = _coercer.coerce(idSnap.get(key), String.class);
      b.append("::").append(val);
    }
    return _encrypter.encrypt(b.toString());
  }

  private List<String> sortedKeys(Map<String, Object> map) {
    List<String> keys = new ArrayList<String>(map.keySet());
    Collections.sort(keys);
    return keys;
  }

  public Persistent toValue(String val) {
    val = _encrypter.decrypt(val);
    if (val == null || val.trim().length() == 0) {
      return null;
    }

    String[] vals = _pattern.split(val);
    if (vals.length < 2) {
      if (vals[0].equals("nil")) {
        return null;
      }

      // TODO i18n this
      throw new RuntimeException("Unable to convert " + val + " into a Cayenne Persistent object");
    }

    if (vals.length == 3 && vals[1].equals("t")) {
      // check to see if it's in storage...
      Persistent obj = _storer.retrieve(vals[2], vals[0]);
      if (obj == null) {
        throw new RuntimeException("Unable to convert " + val + " into a Cayenne Persistent object: missing object");
      }

      return obj;
    }
    if (vals.length == 2) {
      return _manager.find(vals[0], vals[1]);
    }
    // 1.6 introduces Arrays.copyOfRange, but that would require us to require
    // java 1.6...
    String[] pks = new String[vals.length - 1];
    System.arraycopy(vals, 1, pks, 0, pks.length);
    return _manager.find(vals[0], pks);
  }
}
