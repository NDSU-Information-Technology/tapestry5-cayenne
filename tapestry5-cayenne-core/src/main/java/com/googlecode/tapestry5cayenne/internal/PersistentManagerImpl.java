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
package com.googlecode.tapestry5cayenne.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.commons.services.TypeCoercer;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

/**
 * Implementation of the PersistentManager interface. See the interface for more
 * details.
 * 
 * @author robertz
 *
 */
public class PersistentManagerImpl implements PersistentManager {

  private final ObjectContextProvider _provider;
  private final TypeCoercer _coercer;

  public PersistentManagerImpl(ObjectContextProvider provider, TypeCoercer coercer) {
    _provider = provider;
    _coercer = coercer;
  }

  public <T> List<T> listAll(Class<T> type, Ordering... orderings) {
    return listAll(type, 0, orderings);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> listAll(Class<T> type, int limit, Ordering... orderings) {
    SelectQuery sq = new SelectQuery(type);
    ObjectContext context = _provider.currentContext();
    Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
    QuerySortResult rslt = querySort(sq, label, context, type, orderings);
    if (limit > 0) {
      sq.setFetchLimit(limit);
    }
    List<T> values = context.performQuery(sq);
    rslt.type.sort(values, rslt.ordering, label);
    return values;
  }

  public <T> List<T> listMatching(Class<T> type, Expression qualifier, Ordering... orderings) {
    return listMatching(type, qualifier, 0, orderings);
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> listMatching(Class<T> type, Expression qualifier, int limit, Ordering... orderings) {
    SelectQuery sq = new SelectQuery(type);
    ObjectContext context = _provider.currentContext();
    Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
    QuerySortResult rslt = querySort(sq, label, context, type, orderings);
    sq.setQualifier(qualifier);
    if (limit > 0) {
      sq.setFetchLimit(limit);
    }
    List<T> values = context.performQuery(sq);
    rslt.type.sort(values, rslt.ordering, label);
    return values;
  }

  /**
   * Determines what type of sorting to use for the given class. Only reason
   * this is not private is so that TestPersistentEntitySelectModel can more
   * easily test it.
   * 
   * @param sq
   * @param label
   * @param context
   * @param type
   * @param orderings
   * @return
   */
  static QuerySortResult querySort(SelectQuery sq, Method label, ObjectContext context, Class<?> type,
      Ordering... orderings) {
    QuerySortResult res = new QuerySortResult();
    // first check to see if there's anything in orderings...
    if (orderings.length > 0) {
      sq.addOrderings(Arrays.asList(orderings));
      res.type = QuerySortType.QUERY;
      return res;
    }
    // check for ordering annotation...
    DefaultOrder order = type.getAnnotation(DefaultOrder.class);
    if (order != null) {
      if (order.ascending().length == 1) {
        sq.addOrderings(Arrays.asList(OrderingUtils.stringToOrdering(order.ascending()[0], order.value())));
      } else if (order.ascending().length == order.value().length) {
        for (int i = 0; i < order.value().length; i++) {
          sq.addOrdering(
              new Ordering(order.value()[i], order.ascending()[i] ? SortOrder.ASCENDING : SortOrder.DESCENDING));
        }
      } else {
        throw new RuntimeException(
            "DefaultOrdering.ascending.length != 1 and DefaultOrdering.ascending.length != DefaultOrdering.orderings.length for type: "
                + type.getName());
      }
      res.type = QuerySortType.QUERY;
      return res;
    }
    if (label == null) {
      // check to see if the objs are comparable...
      if (Comparable.class.isAssignableFrom(type)) {
        res.type = QuerySortType.COMPARABLE;
      } else {
        res.type = QuerySortType.NOSORT;
      }
      return res;
    }
    if (!label.getName().startsWith("get")) {
      res.type = QuerySortType.METHOD;
      return res;
    }
    // extract the property name.
    String name = label.getName();
    if (name.length() == 4) {
      name = name.substring(3).toLowerCase();
    } else if (Character.isLowerCase(name.charAt(4))) {
      name = Character.toLowerCase(name.charAt(3)) + name.substring(4);
    } else {
      name = name.substring(3);
    }
    Ordering o = new Ordering(name, SortOrder.ASCENDING);
    res.ordering = o;

    ObjEntity ent = context.getEntityResolver().lookupObjEntity(type);
    ObjAttribute attr = (ObjAttribute) ent.getAttribute(name);
    if (attr != null) {
      sq.addOrdering(o);
      res.type = QuerySortType.QUERY;
    } else {
      res.type = QuerySortType.ORDERING;
    }
    return res;
  }

  public <T> T find(Class<T> type, Object id) {
    Object pk = _coercer.coerce(id, pkTypeForEntity(type));
    return DataObjectUtils.objectForPK(_provider.currentContext(), type, pk);
  }

  public <T> T find(Class<T> type, Map<String, Object> idMap) {
    return DataObjectUtils.objectForPK(_provider.currentContext(), type, mapIds(null, type, idMap));
  }

  @SuppressWarnings("unchecked")
  public <T> T find(String entity, Map<String, Object> idMap) {
    return (T) DataObjectUtils.objectForPK(_provider.currentContext(), entity, mapIds(entity, null, idMap));
  }

  @SuppressWarnings("unchecked")
  public <T> T find(String entity, Object[] pkVals) {
    ObjEntity ent = _provider.currentContext().getEntityResolver().getObjEntity(entity);
    ArrayList<ObjAttribute> pks = new ArrayList<ObjAttribute>(ent.getPrimaryKeys());

    Collections.sort(pks, new Comparator<ObjAttribute>() {
      public int compare(ObjAttribute o1, ObjAttribute o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    Map<String, Object> pkMap = new HashMap<String, Object>();

    if (pkVals.length != pks.size()) {
      throw new RuntimeException(
          "Wrong number of keys for entity '" + entity + "'. Expected " + pks.size() + " but got " + pkVals.length);
    }
    for (int i = 0; i < pkVals.length; i++) {
      ObjAttribute pk = pks.get(i);
      Object value = _coercer.coerce(pkVals[i], pk.getJavaClass());
      pkMap.put(pks.get(i).getName(), value);
    }
    return (T) DataObjectUtils.objectForPK(_provider.currentContext(), entity, pkMap);
  }

  private Map<String, Object> mapIds(String entityName, Class<?> entityClass, Map<String, Object> idMap) {
    Map<String, Object> ids = new HashMap<String, Object>();
    ObjEntity entity;
    if (entityName == null) {
      entity = _provider.currentContext().getEntityResolver().lookupObjEntity(entityClass);
    } else {
      entity = _provider.currentContext().getEntityResolver().getObjEntity(entityName);
    }
    for (Map.Entry<String, Object> id : idMap.entrySet()) {
      Object val = _coercer.coerce(id.getValue(), pkTypeForEntity(entity, id.getKey()));
      ids.put(id.getKey(), val);
    }
    return ids;
  }

  private Class<?> pkTypeForEntity(String name) {
    return pkTypeForEntity(_provider.currentContext().getEntityResolver().getObjEntity(name));
  }

  private Class<?> pkTypeForEntity(Class<?> type) {
    return pkTypeForEntity(_provider.currentContext().getEntityResolver().lookupObjEntity(type));
  }

  private Class<?> pkTypeForEntity(ObjEntity entity) {
    Collection<ObjAttribute> atts = entity.getPrimaryKeys();
    if (atts.size() != 1) {
      throw new RuntimeException(
          "T5Cayenne integration currently only handles entities with single-column primary keys");
    }
    ObjAttribute attribute = atts.iterator().next();
    return attribute.getJavaClass();
  }

  private Class<?> pkTypeForEntity(String entName, String name) {
    return pkTypeForEntity(_provider.currentContext().getEntityResolver().getObjEntity(entName), name);
  }

  private Class<?> pkTypeForEntity(Class<?> type, String name) {
    return pkTypeForEntity(_provider.currentContext().getEntityResolver().lookupObjEntity(type), name);
  }

  private Class<?> pkTypeForEntity(ObjEntity entity, String name) {
    for (ObjAttribute attr : entity.getPrimaryKeys()) {
      if (attr.getName().equals(name)) {
        return attr.getJavaClass();
      }
    }
    throw new RuntimeException("Entity '" + entity.getName() + "' does not have a key named " + name);
  }

  @SuppressWarnings("unchecked")
  public <T> T find(String entity, Object id) {
    Object pk = _coercer.coerce(id, pkTypeForEntity(entity));
    return (T) DataObjectUtils.objectForPK(_provider.currentContext(), entity, pk);
  }

  public <T> List<T> findByProperty(Class<T> type, Object... properties) {
    return findByProperties(type, 0, true, true, properties);
  }

  public <T> List<T> findByProperty(Class<T> type, int limit, Object... properties) {
    return findByProperties(type, limit, true, true, properties);
  }

  private <T> List<T> findByProperties(Class<T> type, int limit, boolean matchAll, boolean exactMatch,
      Object... properties) {

    if (properties.length % 2 != 0) {
      throw new IllegalArgumentException("Unbalanced property array");
    }
    if (properties.length < 2) {
      throw new IllegalArgumentException("Must provide at least one property pair, but no pairs were provided");
    }

    // check to make sure every "even" indexed property is a string...
    if (!(properties[0] instanceof String)) {
      throw new IllegalArgumentException("Non-string property name: " + properties[0]);
    }
    Expression e;
    if (exactMatch) {
      e = ExpressionFactory.matchExp((String) properties[0], properties[1]);
    } else {
      e = ExpressionFactory.likeExp((String) properties[0], properties[1]);
    }
    for (int i = 2; i < properties.length; i += 2) {
      if (!(properties[i] instanceof String)) {
        throw new IllegalArgumentException("Non-string property name: " + properties[i]);
      }
      if (matchAll) {
        if (exactMatch) {
          e = e.andExp(ExpressionFactory.matchExp((String) properties[i], properties[i + 1]));
        } else {
          e = e.andExp(ExpressionFactory.likeExp((String) properties[i], properties[i + 1]));
        }
      } else {
        if (exactMatch) {
          e = e.orExp(ExpressionFactory.matchExp((String) properties[i], properties[i + 1]));
        } else {
          e = e.orExp(ExpressionFactory.likeExp((String) properties[i], properties[i + 1]));
        }
      }
    }
    return listMatching(type, e, limit);

  }

  public <T> List<T> findByAnyProperty(Class<T> type, Object... properties) {
    return findByProperties(type, 0, false, true, properties);
  }

  public <T> List<T> findByAnyProperty(Class<T> type, int limit, Object... properties) {
    return findByProperties(type, limit, false, true, properties);
  }

  public <T> List<T> findLikeAnyProperty(Class<T> type, Object... properties) {
    return findByProperties(type, 0, false, false, properties);
  }

  public <T> List<T> findLikeAnyProperty(Class<T> type, int limit, Object... properties) {
    return findByProperties(type, limit, false, false, properties);
  }

}
