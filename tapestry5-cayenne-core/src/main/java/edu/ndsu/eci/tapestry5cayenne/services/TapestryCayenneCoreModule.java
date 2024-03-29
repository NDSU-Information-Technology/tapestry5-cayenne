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

import java.util.List;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.ObjEntity;
import org.apache.cayenne.query.Query;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.beanmodel.services.BeanModelSource;
import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.ObjectLocator;
import org.apache.tapestry5.commons.OrderedConfiguration;
import org.apache.tapestry5.commons.services.Coercion;
import org.apache.tapestry5.commons.services.CoercionTuple;
import org.apache.tapestry5.commons.services.DataTypeAnalyzer;
import org.apache.tapestry5.commons.services.TypeCoercer;
import org.apache.tapestry5.commons.util.VersionUtils;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.http.services.RequestFilter;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.DisplayBlockContribution;
import org.apache.tapestry5.services.EditBlockContribution;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.PersistentFieldStrategy;
import org.apache.tapestry5.services.ValidationConstraintGenerator;
import org.apache.tapestry5.services.ValueEncoderFactory;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.InjectionProvider2;

import edu.ndsu.eci.tapestry5cayenne.T5CayenneConstants;
import edu.ndsu.eci.tapestry5cayenne.annotations.Cayenne;
import edu.ndsu.eci.tapestry5cayenne.internal.PersistentManagerImpl;
import edu.ndsu.eci.tapestry5cayenne.internal.PersistentObjGridDataSource;
import edu.ndsu.eci.tapestry5cayenne.internal.PlainTextEncodedValueEncrypter;
import edu.ndsu.eci.tapestry5cayenne.internal.QueryType;

/**
 * Core module. This module is a "SubModule" of the TapestryModule, defined in
 * tapestry5-cayenne-client and tapestry5-cayenne-core. Any shared services,
 * contributions, etc. will be here.
 * 
 * @author robertz
 */
public class TapestryCayenneCoreModule {

  /**
   * Key which provides the default location to insert the CayenneRequestFilter.
   * By default, this is at the end of the requestfilter pipeline.
   */
  public static final String FILTER_LOCATION = "tapestrycayenne.filterlocation";

  /**
   * Configuration key providing the limit of the number of unpersisted objects
   * to retain in memory. This is used by the default implementation of
   * NonPersistedObjectStorer to set an upper bounds to the amount of objects
   * allowed to accrue in memory. The default is 500.
   */
  public static final String UNPERSISTED_OBJECT_LIMIT = "tapestrycayenne.unpersistedlimit";

  /**
   * Configuration/symbol key for ascertaining the version of the
   * tapestry5-cayenne library.
   */
  public static final String T5CAYENNE_VERSION = "tapestry5cayene.version";

  /**
   * Constant for the T5Cayenne Persistence strategy (storing only pk-info in
   * the session, rather than the full object)
   */
  public static final String T5CAYENNE_PERSISTENCE_STRATEGY = "cayenneentity";

  /**
   * Constant for the binding prefix for ejbql binding (value: ejbq).
   */
  public static final String T5CAYENNE_EJBQ_BINDING = "ejbq";

  /**
   * Constant for the binding prefix for obj entity binding (value: ent).
   */
  public static final String T5CAYENNE_OBJENT_BINDING = "ent";

  /**
   * Factory defaults for: FILTER_LOCATION - after all other request filters
   * UNPERSISTED_OBJECT_LIMIT - 500 T5CAYENNE_VERSION - current version of the
   * integration module.
   * 
   * @param configuration configuration
   */
  public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
    configuration.add(FILTER_LOCATION, "after:*");
    configuration.add(UNPERSISTED_OBJECT_LIMIT, "500");
    configuration.add(T5CAYENNE_VERSION, VersionUtils
        .readVersionNumber("META-INF/maven/edu.ndsu.eci.tapestry5-cayenne/tapestry5-cayenne-core/pom.properties"));
    configuration.add(T5CayenneConstants.PROJECT_FILE, "cayenne.xml");
  }

  @SuppressWarnings("unchecked")
  public static void bind(ServiceBinder binder) {
    binder.bind(ValueEncoder.class, CayenneEntityEncoder.class).withId("CayenneEntityEncoder");

    binder.bind(BeanModelSource.class, CayenneBeanModelSource.class).withId("CayenneBeanModelSource");

    binder.bind(DataTypeAnalyzer.class, CayenneDataTypeAnalyzer.class).withId("CayenneDataTypeAnalyzer");

    binder.bind(NonPersistedObjectStorer.class, DefaultNonPersistedObjectStorer.class)
        .withId("DefaultNonPersistedObjectStorer").withMarker(Cayenne.class);
    binder.bind(PersistentManager.class, PersistentManagerImpl.class);

    binder.bind(EncodedValueEncrypter.class, PlainTextEncodedValueEncrypter.class).withId("PlainTextEncrypter");

    binder.bind(RequestFilter.class, CayenneRequestFilter.class).withId("CayenneFilter").withMarker(Cayenne.class);

    binder.bind(BindingFactory.class, EJBQLBindingFactory.class).withId("EJBQLBindingFactory")
        .withMarker(Cayenne.class);

  }

  /**
   * Contribute our ObjectContextInjectionProvider to the "master"
   * InjectionProvider.
   * 
   * @param configuration configuration
   * @param locator locator
   */
  public static void contributeInjectionProvider(OrderedConfiguration<InjectionProvider2> configuration, ObjectLocator locator) {
    configuration.add("ObjectContext", locator.autobuild(ObjectContextInjectionProvider.class), "before:Service");
  }

  /**
   * add the library mapping for t5cayenne. Prefix is "cay".
   * 
   * @param configuration configuration
   */
  public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
    configuration.add(new LibraryMapping("cay", "edu.ndsu.eci.tapestry5cayenne"));
  }

  /**
   * contribute the t5cayenne "Persistent" object value encoder.
   * @param configuration configuration
   * @param provider context provider
   * @param coercer type coercer
   * @param manager persistence manager
   * @param storer storer
   * @param enc value encrypter
   */
  @SuppressWarnings("unchecked")
  public static void contributeValueEncoderSource(MappedConfiguration<Class, ValueEncoderFactory> configuration,
      @Cayenne final ObjectContextProvider provider, final TypeCoercer coercer, final PersistentManager manager,
      final NonPersistedObjectStorer storer, final EncodedValueEncrypter enc) {
    configuration.add(Persistent.class, new ValueEncoderFactory<Persistent>() {
      public ValueEncoder<Persistent> create(Class<Persistent> persistentClass) {
        return new CayenneEntityEncoder(provider, coercer, manager, storer, enc);
      }
    });
  }

  @Contribute(ServiceOverride.class)
  public static void contributeServiceOverride(MappedConfiguration<Class, Object> conf, @Cayenne BeanModelSource source,
      @InjectService("PlainTextEncrypter") EncodedValueEncrypter enc, @Local NonPersistedObjectStorer storer) {
    conf.add(BeanModelSource.class, source);
    conf.add(EncodedValueEncrypter.class, enc);
    conf.add(NonPersistedObjectStorer.class, storer);

  }

  /**
   * contribute the t5cayenne-specific DataTypeAnalyzer which handles, eg,
   * identifying relationships, among other things.
   * 
   * @param configuration configuration
   * @param analyzer analyzer
   */
  public static void contributeDataTypeAnalyzer(OrderedConfiguration<DataTypeAnalyzer> configuration, @Cayenne DataTypeAnalyzer analyzer) {
    // add after Annotation; we want to make sure that explicitly-defined data
    // types are honored.
    configuration.add("Cayenne", analyzer, "after:Annotation");
  }

  /**
   * Contribute our view and edit blocks for to_one and to_many.
   * 
   * @param configuration configuration
   */
  public static void contributeBeanBlockSource(Configuration<BeanBlockContribution> configuration) {
    configuration.add(new EditBlockContribution("to_one", "cay/CayenneEditBlockContributions", "to_one_editor"));
    configuration.add(new DisplayBlockContribution("to_one", "cay/CayenneViewBlockContributions", "to_one_viewer"));
    configuration.add(new DisplayBlockContribution("to_many_map", "cay/CayenneViewBlockContributions", "to_many_map_viewer"));
    configuration.add(new DisplayBlockContribution("to_many_collection", "cay/CayenneViewBlockContributions",
        "to_many_collection_viewer"));
  }

  /**
   * Contributes the following:
   * <dl>
   * <dt>cayenneentity</dt>
   * <dd>Stores the id of the entity and reloads from the
   * {@link ObjectContext}</dd>
   * </dl>
   * @param configuration configuration
   * @param locator locator
   */
  public static void contributePersistentFieldManager(MappedConfiguration<String, PersistentFieldStrategy> configuration, ObjectLocator locator) {
    configuration.add("cayenneentity", locator.autobuild(CayenneEntityPersistentFieldStrategy.class));
  }

  /**
   * Contribute query -&gt; list (consequently: query -&gt; GridDataSource) coercion
   * and ObjEntity -&gt; GridDataSource contributions.
   * 
   * @param configuration configuration
   */
  @SuppressWarnings("unchecked")
  public static void contributeTypeCoercer(MappedConfiguration<CoercionTuple.Key, CoercionTuple> configuration) {
    Coercion<String, Expression> string2ExpressionCoercion = new Coercion<String, Expression>() {
      public Expression coerce(String input) {
        return ExpressionFactory.exp(input); 
      }
    };
    
    CoercionTuple<String, Expression> expressionTuple = new CoercionTuple<String, Expression>(String.class, Expression.class, string2ExpressionCoercion);
    configuration.add(expressionTuple.getKey(), expressionTuple);
    
    Coercion<Query, List> query2ListCoercion = new Coercion<Query, List>() {
      public List coerce(Query input) {
        /*
         * as much as I would like to use ObjectContextProvider here, injecting
         * it results in TypeCoercer not instantiable b/c it depends on itself I
         * /think/ b/c of alias/aliasoverrides.
         */
        ObjectContext oc = BaseContext.getThreadObjectContext();
        if (input.getMetaData(oc.getEntityResolver()).getPageSize() == 0) {
          QueryType.typeForQuery(input).setPageSize(input, 20);
        }
        return oc.performQuery(input);
      }
    };
    
    CoercionTuple<Query, List> queryTuple = new CoercionTuple<Query, List>(Query.class, List.class, query2ListCoercion);
    configuration.add(queryTuple.getKey(), queryTuple);
    
    Coercion<ObjEntity, GridDataSource> obj2GdsCoercion = new Coercion<ObjEntity, GridDataSource>() {
      public GridDataSource coerce(ObjEntity input) {
        return new PersistentObjGridDataSource(input);
      }
    };

    CoercionTuple<ObjEntity, GridDataSource> objTuple = new CoercionTuple<ObjEntity, GridDataSource>(ObjEntity.class, GridDataSource.class, obj2GdsCoercion);
    configuration.add(objTuple.getKey(), objTuple);
    
    Coercion<Class, ObjEntity> class2ObjEntity = new Coercion<Class, ObjEntity>() {
      public ObjEntity coerce(Class input) {
        return BaseContext.getThreadObjectContext().getEntityResolver().getObjEntity(input);
      }
    };
    
    CoercionTuple<Class, ObjEntity> class2ObjEntityTuple = new CoercionTuple<Class, ObjEntity>(Class.class, ObjEntity.class, class2ObjEntity);
    configuration.add(class2ObjEntityTuple.getKey(), class2ObjEntityTuple);
  }

  /**
   * Add the t5cayenne request filter.
   * 
   * @param configuration configuration
   * @param filter request filter
   * @param location filter location
   * 
   */
  public static void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
      @Cayenne RequestFilter filter, @Symbol(TapestryCayenneCoreModule.FILTER_LOCATION) String location) {
    configuration.add("cayenne", filter, location);
  }

  /**
   * Contribute the "ejbq" and "ent" bindings
   * 
   * @param configuration configuration
   * @param ejbqlBindingFactory binding factory
   * @param locator locator
   */
  public static void contributeBindingSource(MappedConfiguration<String, BindingFactory> configuration,
      @Local BindingFactory ejbqlBindingFactory, ObjectLocator locator) {
    configuration.add(T5CAYENNE_EJBQ_BINDING, ejbqlBindingFactory);
    configuration.add(T5CAYENNE_OBJENT_BINDING, locator.autobuild(ObjEntityBindingFactory.class));
  }

  /**
   * Contribute the "CommitAfter" worker.
   * 
   * @param configuration configuration
   * @param locator locator
   */
  public static void contributeComponentClassTransformWorker(
      OrderedConfiguration<ComponentClassTransformWorker2> configuration, ObjectLocator locator) {
    // add as per the hibernate module: after logging.
    configuration.add("CayenneCommitAfter", locator.autobuild(CayenneCommitAfterWorker.class), "after:Log");
  }

  /**
   * Add the cayenne constraint generator, which enables auto-detection of
   * required properties, etc. based on the mapping information.
   * 
   * @param configuration configuration
   * @param locator locator
   */
  public static void contributeValidationConstraintGenerator(OrderedConfiguration<ValidationConstraintGenerator> configuration,
      ObjectLocator locator) {
    configuration.add("Cayenne", locator.autobuild(CayenneConstraintGenerator.class), "after:ValidateAnnotation");
  }
}
