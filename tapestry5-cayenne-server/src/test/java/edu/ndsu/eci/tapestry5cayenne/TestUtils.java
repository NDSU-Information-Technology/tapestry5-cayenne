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
package edu.ndsu.eci.tapestry5cayenne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.BaseContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.configuration.DataNodeDescriptor;
import org.apache.cayenne.configuration.server.DataContextFactory;
import org.apache.cayenne.configuration.server.DbAdapterFactory;
import org.apache.cayenne.configuration.server.ServerRuntime;
//import org.apache.cayenne.conf.Configuration;
//import org.apache.cayenne.conf.DefaultConfiguration;
import org.apache.cayenne.dba.DbAdapter;
import org.apache.cayenne.dba.hsqldb.HSQLDBAdapter;
import org.apache.cayenne.log.NoopJdbcEventLogger;
import org.apache.cayenne.map.DataMap;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.http.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.http.internal.TapestryAppInitializer;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.services.PersistentFieldManager;
import org.apache.tapestry5.internal.test.PageTesterModule;
import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.internal.services.MapSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.modules.TapestryModule;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.ValueEncoderSource;
import org.apache.tapestry5.services.transform.InjectionProvider2;

import edu.ndsu.eci.tapestry5cayenne.T5CayenneConstants;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.model.Painting;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {

  private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

  /**
   * Initializes an hsql database for tests. Note that this will drop tables, as
   * well as create new ones.
   * 
   * @throws Exception
   */
  public static void setupdb() throws Exception {
    ServerRuntime cayenneRuntime = ServerRuntime.builder().addConfig("cayenne-App0.xml").build();
//    DefaultConfiguration c = new DefaultConfiguration("cayenne.xml");
//    Configuration.initializeSharedConfiguration(c);
//    DbAdapter adapt = HSQLDBAdapter.class.newInstance();
    
    DbAdapterFactory adapterFactory = cayenneRuntime.getInjector().getInstance(DbAdapterFactory.class);
    DataNodeDescriptor nodeDescriptor = new DataNodeDescriptor();
    nodeDescriptor.setAdapterType("org.apache.cayenne.dba.hsqldb.HSQLDBAdapter");

    DbAdapter adapt = adapterFactory.createAdapter(nodeDescriptor, cayenneRuntime.getDataSource());

    
    ObjectContext dc = cayenneRuntime.newContext();
//    DataContext dc = new DataContextFactory().createContext(cayenneRuntime.getChannel()); 
//        cayenneRuntime.newContext();

    for (Object obj : cayenneRuntime.getDataDomain().getDataMaps()) {
      DataMap map = (DataMap) obj;
      DbGenerator dbgen = new DbGenerator(adapt, map, NoopJdbcEventLogger.getInstance());
      dbgen.setShouldDropTables(true);
      dbgen.setShouldDropPKSupport(true);
      dbgen.setShouldCreatePKSupport(true);
      dbgen.setShouldCreateFKConstraints(true);
      dbgen.setShouldCreateTables(true);
      dbgen.runGenerator(cayenneRuntime.getDataSource());
    }
    BaseContext.bindThreadObjectContext(dc);
  }

  /**
   * Sets up some basic data (artists and paintings) for use in t5cayenne tests.
   * 
   * @param context
   * @return The list of artists Sets up two artists ("Picasso" and "Dali"),
   *         each with two paintings.
   */
  public static List<Artist> basicData(ObjectContext context) {
    List<Artist> ret = new ArrayList<Artist>();
    Artist a = context.newObject(Artist.class);
    a.setName("Picasso");

    Painting p = context.newObject(Painting.class);
    p.setArtist(a);
    p.setPrice(10000.0);
    p.setTitle("Portrait of Igor Stravinsky");

    p = context.newObject(Painting.class);
    p.setArtist(a);
    p.setPrice(15000.0);
    p.setTitle("Dora Maar au Chat");

    ret.add(a);

    a = context.newObject(Artist.class);
    a.setName("Dali");

    p = context.newObject(Painting.class);
    p.setArtist(a);
    p.setTitle("Self-portrait");
    p.setPrice(12000.0);

    p = context.newObject(Painting.class);
    p.setArtist(a);
    p.setTitle("The Persistence of Memory");
    p.setPrice(19000.0);

    ret.add(a);

    context.commitChanges();

    return ret;
  }

  /**
   * Initializes the application registry, including all tapestry services.
   * 
   * @param appName
   *          The name of the application (allows tapestry to look for appName +
   *          Module)
   * @param modules
   *          Any additional modules to load
   * @return the initialized service/ioc registry.
   */
  public static Registry setupRegistry(String appName, Class<?>... modules) {
    Map<String, String> symbols = new HashMap<>();
    symbols.put(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "edu.ndsu.eci.tapestry5cayenne.integration");
    symbols.put(T5CayenneConstants.PROJECT_FILE, "cayenne-App0.xml");
    SymbolProvider provider = new MapSymbolProvider(symbols);
    TapestryAppInitializer initializer = new TapestryAppInitializer(logger, provider, appName,
        PageTesterModule.TEST_MODE);

    if (modules.length > 0) {
      for (Class clazz : modules) {
        System.out.println(clazz);
      }
      initializer.addModules(modules);
    }
//    IOCUtilities.addDefaultModules(null);
    initializer.addModules(TapestryModule.class);
    Registry ret = initializer.createRegistry();
    return ret;
  }

  /**
   * Find all elements matching the path given, underneath the element given by
   * root.
   * 
   * @param root
   *          root element to search from. The search starts at the children of
   *          the root.
   * @param path
   *          / delimitted search path. Path should start one level below the
   *          root element.
   * @return A list of elements matching the path, or empty list if none match.
   *         Example: Given the document:
   *         <html><body><form><div><input name="a"/></div><div><input name=
   *         "b"/></div></form></body></html>
   *         DOMFindAll(doc.getRootElement(),"body/form/div") would return the
   *         two div elements.
   *         DomFindAll(doc.getRootElement(),"body/form/div/input") would return
   *         both input elements.
   */
  public static final List<Element> DOMFindAll(Element root, String path) {
    String[] pathels = path.split("/");
    List<Element> searchels = new ArrayList<Element>();
    searchels.add(root);
    for (String pathel : pathels) {
      List<Element> nextsearch = new ArrayList<Element>();
      for (Element el : searchels) {
        for (Node n : el.getChildren()) {
          if (n instanceof Element && ((Element) n).getName().equals(pathel)) {
            nextsearch.add((Element) n);
          }
        }
      }
      searchels = nextsearch;
      if (searchels.isEmpty()) {// not found
        break;
      }
    }
    return searchels;
  }

  /**
   * Add a series of paintings to an artist. Each will be titled Paintingn where
   * n is the nth added painting (0-based). The price will be 1000.0*n.
   * 
   * @param artist
   *          the artist to add the paintings to.
   * @param numPaintings
   *          the number of paintings to add
   * @param context
   *          the context
   * @return the list of paintings.
   */
  public static List<Painting> addPaintings(Artist artist, int numPaintings, ObjectContext context) {
    List<Painting> newPaintings = new ArrayList<Painting>();
    for (int i = 1; i <= numPaintings; i++) {
      Painting p = new Painting();
      p.setPrice(i * 1000.0);
      p.setTitle("Painting " + i);
      artist.addToPaintingsByTitle(p);
      newPaintings.add(p);
    }
    context.commitChanges();
    return newPaintings;
  }

}
