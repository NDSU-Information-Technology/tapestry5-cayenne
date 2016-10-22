package com.googlecode.tapestry5cayenne.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.googlecode.tapestry5cayenne.internal.util.MethodWrapper;
import org.apache.cayenne.BaseContext;
import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.ioc.services.Coercion;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.model.Bid;
import com.googlecode.tapestry5cayenne.model.BigIntPKEntity;
import com.googlecode.tapestry5cayenne.model.Painting;
import com.googlecode.tapestry5cayenne.model.StringPKEntity;
import com.googlecode.tapestry5cayenne.model.TinyIntPKEntity;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

@Test(groups="all")
public class TestPersistentManagerImpl {

    private ObjectContext _context;
    private PersistentManager _manager;
    private Artist picasso;
    private Artist dali;
    
    @BeforeClass
    void setup() throws Exception {
        TestUtils.setupdb();
        _context = BaseContext.getThreadObjectContext();
        List<Artist> data = TestUtils.basicData(_context);
        new Ordering(Artist.NAME_PROPERTY,SortOrder.ASCENDING).orderList(data);
        dali = data.get(0);
        picasso=data.get(1);
        assertEquals(dali.getName(),"Dali");
        assertEquals(picasso.getName(),"Picasso");
        TypeCoercer coercer = new TypeCoercer() {
            public void clearCache() {}
            @SuppressWarnings("unchecked")
            public <S, T> T coerce(S arg0, Class<T> arg1) {
                return (T) arg0;
            }
            public <S, T> String explain(Class<S> arg0, Class<T> arg1) { return null; }
            public <S, T> Coercion<S, T> getCoercion(Class<S> sourceType, Class<T> targetType) {
                return null;
            }
        };
        
        _manager = new PersistentManagerImpl(new ObjectContextProvider() {
            public ObjectContext currentContext() {
                return _context;
            }
            public ObjectContext newContext() {
                return _context;
            }
        },
        coercer);
    }
    
    @DataProvider(name="sorts")
    public Object[][] sorts() throws Exception {
        return new Object[][] {
                //prop is in model
                {
                    new SelectQuery(Artist.class),
                    new MethodWrapper(Artist.class.getMethod("getName")),
                    Artist.class,
                    new QuerySortResult(QuerySortType.QUERY,new Ordering("name",SortOrder.ASCENDING))
                },
                //label is a javabeans prop, but not in model
                {
                    new SelectQuery(Artist.class),
                    new MethodWrapper(Artist.class.getMethod("getNumPaintings")),
                    Artist.class,
                    new QuerySortResult(QuerySortType.ORDERING,new Ordering("numPaintings",SortOrder.ASCENDING))
                },
                {
                    new SelectQuery(Artist.class),
                    new MethodWrapper(Artist.class.getMethod("numPaintings")),
                    Artist.class,
                    new QuerySortResult(QuerySortType.METHOD,null)
                },
                {
                    new SelectQuery(BigIntPKEntity.class),
                    new MethodWrapper(null),
                    BigIntPKEntity.class,
                    new QuerySortResult(QuerySortType.NOSORT,null)
                },
                {
                    new SelectQuery(Artist.class),
                    new MethodWrapper(null),
                    Artist.class,
                    new QuerySortResult(QuerySortType.COMPARABLE,null)
                }
        };
    }
    
    @Test(dataProvider="sorts")
    public void query_sort(SelectQuery sq, MethodWrapper label, Class<?> type, QuerySortResult expected) {
        QuerySortResult result = PersistentManagerImpl.querySort(sq, label.getMethod(), BaseContext.getThreadObjectContext(), type,new Ordering[]{});
        assertEquals(result.type,expected.type);
        if (expected.ordering == null) {
            assertNull(result.ordering);
        } else {
            assertEquals(result.ordering.isAscending(),expected.ordering.isAscending());
            assertEquals(result.ordering.getSortSpecString(),expected.ordering.getSortSpecString());
        }
        if (expected.type ==  QuerySortType.QUERY) {
            assertEquals(sq.getOrderings().get(0),result.ordering);
        } else {
            assertEquals(sq.getOrderings().size(),0);
        }
    }
    
    public void testExplicitOrdering() {
        List<Artist> objs = _manager.listAll(
                Artist.class, 
                OrderingUtils.stringToOrdering(Artist.NAME_PROPERTY));
        assertEquals(objs,Arrays.asList(dali,picasso));
    }
    
    public void testDefaultOrdering() {
        Bid b = new Bid();
        b.setPainting(dali.getPaintingList().get(0));
        b.setAmount(new BigDecimal(27.00));
        Bid b2 = new Bid();
        b2.setPainting(dali.getPaintingList().get(0));
        b2.setAmount(new BigDecimal(25.00));
        _context.commitChanges();
        List<Bid> objs = _manager.listAll(Bid.class);
        assertEquals(objs.size(),2);
        assertEquals(objs.get(0),b2);
        assertEquals(objs.get(1),b);
    }
    
    @Test(expectedExceptions=RuntimeException.class)
    public void testDefaultOrderingUnbalancedAscending() {
        _manager.listAll(TinyIntPKEntity.class);
    }
    
    public void testDefaultOrderingMultiOrder() {
        StringPKEntity pke1 = _context.newObject(StringPKEntity.class);
        pke1.setId("spke1");
        pke1.setIntProp1(10);
        pke1.setStringProp1("abc");
        _context.registerNewObject(pke1);
        
        StringPKEntity pke2 = _context.newObject(StringPKEntity.class);
        pke2.setId("spke2");
        pke2.setIntProp1(20);
        pke2.setStringProp1("abc");
        
        _context.commitChanges();
        List<StringPKEntity> objs = _manager.listAll(StringPKEntity.class);
        assertEquals(objs.size(),2);
        assertEquals(objs.get(0),pke2);
        assertEquals(objs.get(1),pke1);
    }
    
    
    @DataProvider(name="list_matching")
    Object[][] listMatching() {
        return new Object[][] {
                {
                    Artist.class,
                    ExpressionFactory.matchExp(Artist.NAME_PROPERTY, "Flinstone"),
                    Collections.emptyList(),
                    new Ordering[]{}
                },
                {
                    Artist.class,
                    ExpressionFactory.matchExp(Artist.NAME_PROPERTY, "Picasso"),
                    Arrays.asList(picasso),
                    new Ordering[]{}
                },
                {
                    Painting.class,
                    ExpressionFactory.likeExp(Painting.TITLE_PROPERTY, "%P%"),
                    Arrays.asList(
                            picasso.getPaintingsByTitle().get("Portrait of Igor Stravinsky"),
                            dali.getPaintingsByTitle().get("The Persistence of Memory")
                            ),
                    new Ordering[]{}
                },
                {
                    Painting.class,
                    ExpressionFactory.likeExp(Painting.TITLE_PROPERTY, "%P%"),
                    Arrays.asList(
                            dali.getPaintingsByTitle().get("The Persistence of Memory"),
                            picasso.getPaintingsByTitle().get("Portrait of Igor Stravinsky")
                            ),
                    new Ordering[]{
                       new Ordering("title",SortOrder.DESCENDING)
                    }
                },
        };
    }
    
    @Test(dataProvider="list_matching")
    public void testListMatching(Class<?> type, Expression qualifier, List<?> expected, Ordering... orderings) {
        List<?> ret = _manager.listMatching(type, qualifier, orderings);
        assertEquals(ret,expected);
    }
    
    
    public void testFind_Class() {
        assertEquals(picasso,_manager.find(Artist.class, DataObjectUtils.intPKForObject(picasso)));
    }
    
    
    public void testFind_String() {
        Artist a = _manager.find("Artist", DataObjectUtils.intPKForObject(dali));
        assertEquals(dali,a);
    }
    
    @DataProvider(name="find_by_property")
    Object[][] findByProperty() {
        Painting p = dali.getPaintingsByTitle().get("The Persistence of Memory");
        return new Object[][] {
                /* test to make sure that "unbalanced" property sets throw Illegal Argument Exception*/
                {Artist.class,null,new IllegalArgumentException("Unbalanced property array"), new Object[]{Artist.NAME_PROPERTY}},
                /* test to make sure that non-string "property names" throw Illegal Argument Exception */
                {Artist.class,null,new IllegalArgumentException("Non-string property name: 123"), new Object[] {123,"foo"}},
                /* what if the non-string prop is further in the array?*/
                {Artist.class,null,new IllegalArgumentException("Non-string property name: 123"), new Object[] {Artist.NAME_PROPERTY,"Picasso",123,"foo"}},
                /* test to make sure that an empty property list throws an IllegalArgumentException */
                {Artist.class,null,new IllegalArgumentException("Must provide at least one property pair, but no pairs were provided"),new Object[]{}},
                /* test to make sure that finding by one property returns the correct object...*/
                {Artist.class,Arrays.asList(dali),null,new Object[]{Artist.NAME_PROPERTY,dali.getName()}},
                /* test multiple properties and deeper navigation paths... */
                {
                    Painting.class,
                    Arrays.asList(p),
                    null,
                    new Object[]{
                        Painting.TITLE_PROPERTY,p.getTitle(),
                        Painting.ARTIST_PROPERTY + "." + Artist.NAME_PROPERTY,dali.getName()}
                },
        };
    }
    
     
    /**
     * Tests PersistentManagerImpl.findByProperty.
     * If expectedResult is null and expectedException is not, then the test ensures that the appropriate exception was thrown.
     * Otherwise it ensures that the call returns the expectedResult.
     */
    @Test(dataProvider="find_by_property")
    public void test_find_by_property(Class<? extends Persistent> type, List<?>expectedResults, Throwable expectedException, Object...properties) {
        testPropFind(type,expectedResults,expectedException,true,true,properties);
    }
    
    private void testPropFind(Class<?> type, int limit, List<?> expectedResults, Throwable expectedException, boolean matchAll, boolean exactMatch, Object... properties) {
        Throwable t=null;
        List<?> results=null;
        try {
          if (matchAll) {
              if (limit >0) {
                  results = _manager.findByProperty(type,limit,properties);
              } else {
                  results = _manager.findByProperty(type,properties);
              }
          } else {
              if (exactMatch) {
                  if (limit > 0) {
                      results = _manager.findByAnyProperty(type, limit,properties);
                  } else {
                      results = _manager.findByAnyProperty(type, properties);
                  }
              } else {
                  if (limit > 0) {
                      results = _manager.findLikeAnyProperty(type, limit, properties);
                  } else {
                      results = _manager.findLikeAnyProperty(type, properties);
                  }
              }
          }
        } catch (Exception e) {
            t = e;
        }
        if (expectedException != null) {
            assertNotNull(t);
            assertEquals(t.getClass(),expectedException.getClass());
            assertEquals(t.getMessage(),expectedException.getMessage());
        } else {
            if (t != null) {
                t.printStackTrace();
            }
            assertNull(t);
        }
        assertEquals(results,expectedResults);
    }
    
    private void testPropFind(Class<?> type, List<?> expectedResults, Throwable expectedException, boolean matchAll, boolean exactMatch, Object...properties) {
        testPropFind(type,0,expectedResults,expectedException,matchAll,exactMatch,properties);
    }
    
    @DataProvider(name="find_by_any_property")
    Object[][] findByAnyProperty() {
        Painting p1 = dali.getPaintingsByTitle().get("The Persistence of Memory");
        assertNotNull(p1);
        Painting p2 = dali.getPaintingsByTitle().get("Self-portrait");
        assertNotNull(p2);
        return new Object[][] {
                /* test to make sure that "unbalanced" property sets throw Illegal Argument Exception*/
                {Artist.class,null,new IllegalArgumentException("Unbalanced property array"), new Object[]{Artist.NAME_PROPERTY}},
                /* test to make sure that non-string "property names" throw Illegal Argument Exception */
                {Artist.class,null,new IllegalArgumentException("Non-string property name: 123"), new Object[] {123,"foo"}},
                /* what if the non-string prop is further in the array?*/
                {Artist.class,null,new IllegalArgumentException("Non-string property name: 123"), new Object[] {Artist.NAME_PROPERTY,"Picasso",123,"foo"}},
                /* test to make sure that an empty property list throws an IllegalArgumentException */
                {Artist.class,null,new IllegalArgumentException("Must provide at least one property pair, but no pairs were provided"),new Object[]{}},
                /* test to make sure that finding by one property returns the correct object...*/
                {Artist.class,Arrays.asList(picasso),null,new Object[]{Artist.NAME_PROPERTY,picasso.getName()}},
                /* test multiple properties and deeper navigation paths... */
                {
                    Painting.class,
                    Arrays.asList(p2,p1),
                    null,
                    new Object[]{
                        Painting.TITLE_PROPERTY,p1.getTitle(),
                        Painting.ARTIST_PROPERTY + "." + Artist.NAME_PROPERTY,dali.getName()}
                },
        };
    }
    
    /**
     * Tests PersistentManagerImpl.findByProperty.
     * If expectedResult is null and expectedException is not, then the test ensures that the appropriate exception was thrown.
     * Otherwise, it ensures that the call returns the expectedResult.
     */
    @Test(dataProvider="find_by_any_property")
    public void test_find_by_any_property(Class<? extends Persistent> type, List<?> expectedResults, Throwable expectedException, Object...properties) {
        testPropFind(type,expectedResults,expectedException,false,true,properties);
    }
    
    
    @DataProvider(name="find_like_any_property")
    Object[][] findLikeAnyProperty() {
        Painting p = dali.getPaintingsByTitle().get("Self-portrait");
        Painting p2 = dali.getPaintingsByTitle().get("The Persistence of Memory");
        String strippedName = "Dal%";
        return new Object[][] {
                {Artist.class,null,new IllegalArgumentException("Unbalanced property array"),new Object[]{Artist.NAME_PROPERTY}},
                {Artist.class,null,new IllegalArgumentException("Unbalanced property array"),new Object[]{Artist.NAME_PROPERTY,"Picasso","blah"}},
                {Artist.class,null,new IllegalArgumentException("Non-string property name: 123"), new Object[] {123,"foo"}},
                {Artist.class,null, new IllegalArgumentException("Non-string property name: 123"), new Object[] {Artist.NAME_PROPERTY,"Picasso",123,"foo"}},
                {Artist.class,null,new IllegalArgumentException("Must provide at least one property pair, but no pairs were provided"), new Object[]{}},
                {Artist.class,Arrays.asList(dali),null,new Object[]{Artist.NAME_PROPERTY,strippedName}},
                {
                    Painting.class,
                    Arrays.asList(p,p2),
                    null,
                    new Object[] {
                        Painting.TITLE_PROPERTY,"Self-portrai%",
                        Painting.ARTIST_PROPERTY + "." + Artist.NAME_PROPERTY,strippedName,
                    }
                },
                
        };
    }
    
    @Test(dataProvider="find_like_any_property")
    public void test_find_like_any_property(Class<? extends Persistent> type, List<?> expectedResults, Throwable expectedException, Object...properties) {
        testPropFind(type,expectedResults,expectedException,false,false,properties);
    }
    
    @DataProvider(name="find_like_any_property_with_limit")
    Object[][] findLikeAnyPropertyWithLimit() {
        Painting p = dali.getPaintingsByTitle().get("Self-portrait");
        String strippedName = "Dal%";
        return new Object[][] {
                {Artist.class,1,null,new IllegalArgumentException("Unbalanced property array"),new Object[]{Artist.NAME_PROPERTY}},
                {Artist.class,1,null,new IllegalArgumentException("Unbalanced property array"),new Object[]{Artist.NAME_PROPERTY,"Picasso","blah"}},
                {Artist.class,1,null,new IllegalArgumentException("Non-string property name: 123"), new Object[] {123,"foo"}},
                {Artist.class,1,null, new IllegalArgumentException("Non-string property name: 123"), new Object[] {Artist.NAME_PROPERTY,"Picasso",123,"foo"}},
                {Artist.class,1,null,new IllegalArgumentException("Must provide at least one property pair, but no pairs were provided"), new Object[]{}},
                {Artist.class,1,Arrays.asList(dali),null,new Object[]{Artist.NAME_PROPERTY,strippedName}},
                {
                    Painting.class,
                    1,
                    Arrays.asList(p),
                    null,
                    new Object[] {
                        Painting.TITLE_PROPERTY,"Self-portrai%",
                        Painting.ARTIST_PROPERTY + "." + Artist.NAME_PROPERTY,strippedName,
                    }
                },
                
        };
    }
    @Test(dataProvider="find_like_any_property_with_limit")
    public void test_find_like_any_property_with_limit(Class<? extends Persistent> type, int limit, List<?> expectedResults, Throwable expectedException, Object...properties) {
        testPropFind(type,limit,expectedResults,expectedException,false,false,properties);
    }
}
