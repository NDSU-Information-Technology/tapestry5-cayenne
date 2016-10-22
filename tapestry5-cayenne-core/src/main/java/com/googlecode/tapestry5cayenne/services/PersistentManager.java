package com.googlecode.tapestry5cayenne.services;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.Ordering;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;

/**
 * PersistentManager is a service for managing basic, generic operations on data objects,
 * such as fetching an ordered list of objects of a particular type.
 * @author robertz
 *
 */

public interface PersistentManager {
    
    /**
     * Returns a list of all objects of type <T>.  
     * The list is ordered by the first rule that matches in the following list:
     *  1) passed in orderings, if any
     *  2) the {@link DefaultOrder} annotation, if present
     *  3) the ordering provided by a method marked with the {@link Label} annotation, if present
     *  4) The "natural" ordering of the objects, if they implement Comparable
     *  5) No particular ordering if all of the above are false
     * @param <T>
     * @param type 
     * @param orderings
     * @return
     */
    <T> List<T> listAll(Class<T> type, Ordering...orderings);
    
    /**
     * Returns a list of all objects of type <T>, limiting the results to the provided limit.
     * The list is ordered by the first rule that matches in the following list:
     *  1) passed in orderings, if any
     *  2) the {@link DefaultOrder} annotation, if present
     *  3) the ordering provided by a method marked with the {@link Label} annotation, if present
     *  4) The "natural" ordering of the objects, if they implement Comparable
     *  5) No particular ordering if all of the above are false
     * @param <T>
     * @param type the type of object to return
     * @param limit  the max number of objects to return
     * @param orderings orderings to override the default ordering, if necessary.
     * @return
     */
    <T> List<T> listAll(Class<T> type, int limit, Ordering...orderings);
    
    /**
     * Returns a list of objects of type <T> that match the provided expression.
     * The list is ordered as for listAll.
     * @param type The type of object to return
     * @param qualifier  the expression used to match the arguments
     * @param orderings (optional) the order in which the objects should be returned.
     */
    <T> List<T> listMatching(Class<T> type, Expression qualifier, Ordering... orderings);
    
    /**
     * Returns a list of objects of type <T> that match the provided expression, limiting the number of returned results.
     * The list is ordered as for listAll.
     * @param type The type of object to return
     * @param qualifier  the expression used to match the arguments
     * @param limit the max number of objects to return
     * @param orderings (optional) the order in which the objects should be returned.
     */
    <T> List<T> listMatching(Class<T> type, Expression qualifier, int limit, Ordering... orderings);
    
    /**
     * Finds the object of type <T> with id given by id.
     * This method will coerce the id into the appropriate data-type (eg: String -> int conversion), if necessary.
     * Only objects with single-column primary keys are supported.
     * This method ONLY handles objects which have already been committed to the database.
     * @param <T> 
     * @param type The type of object to find.
     * @param id The id (pk) of the object.
     * @return The matching object, if any, or null.
     */
    <T> T find(Class<T> type, Object id);
    
    /**
     * Finds the object of type T, based on the object entity name and the given id.
     * This method will coerce the id into the appropriate data-type (eg: String -> int conversion), if necessary.
     * Only objects with single-column primary keys are supported.
     * Note that if the java class of the ObjEntity given by the entity parameter does not match T, a RuntimeException will be thrown.
     * This method ONLY handles objects which have already been committed to the database.
     * @param <T> The type to find
     * @param entity The Object Entity name of the obj to find, as mapped in Cayenne.
     * @param id The id of the object to find.
     * @return The matching object, if any, or null.
     * @throws ClassCastException if the java class for entity cannot be cast to <T>.
     */
    <T> T find(String entity, Object id);
    
    /**
     * Finds the object of type <T>, with the (possibly multi-column id) given by idMap.
     * This method will attempt to coerce the pk values in idMap to the expected java class for the
     * id attribute (eg: String -> int conversion). ONLY works for objects which have already been committed to the database.
     * @param <T>
     * @param type The type of object to find.
     * @param idMap The (possibly multi-valued) mapping of pkname -> pkvalue. 
     * @return The matching object, if any, or null.
     */
    <T> T find(Class<T> type, Map<String, Object> idMap);
    
    /**
     * Finds the object of type <T>, with the (possibly multi-column id) given by idMap.
     * This method will attempt to coerce the pk values in idMap to the expected java class for the
     * id attribute (eg: String -> int conversion). ONLY works for objects which have already been committed to the database.
     * @param <T> The type to find
     * @param entity The Object Entity name of the obj to find, as mapped in Cayenne.
     * @param idMap The (possibly multi-valued) mapping of pkname -> pkvalue. 
     * @return The matching object, if any, or null.
     * @throws ClassCastException if the java class for the entity cannot be cast to <T>.
     */
    <T> T find(String entity, Map<String, Object> idMap);
    
    /**
     * Finds the object of type <T>, with the (possibly multi-column id) given by pkVals.
     * The values in pkVals MUST be listed in the order returned by sorting the key names.
     * Otherwise, identical to {@link #find(String, Map)}.
     * @param <T>
     * @param entity The object entity name of the obj to find, as mapped in Cayenne.
     * @param pkVals The array of pk vals. Coercion will occur to convert them to the appropriate type (eg: String -> int).
     * @return The matching object, if any, or null.
     * @throws ClassCastException if the java class for the entity cannot be cast to <T>.
     */
    <T> T find(String entity, Object[] pkVals);

    /**
     * Finds all objects of the provided type, matching the entire set of properties provided.
     * @param <T> 
     * @param type the type to match
     * @param properties The properties to match
     * @return A list containing objects which exactly match all of the given properties
     * @throws IllegalArgumentException if: properties.length%2 !=0, or any properties[i] (i%2==0) is not a string.
     * This method takes the provided properties and builds an expression from them.  The assumption is that 
     * properties looks like: { propertyName, propertyValue, propertyName2, propertyValue2,...}.
     * The properties are "and'ed" together. The objects are ordered according to the rules for listAll. 
     * For more control over the expression and/or ordering, try listMatching.
     */
    <T> List<T> findByProperty(Class<T> type, Object... properties);
    
    /**
     * Finds all objects of the provided type, matching the entire set of properties provided, and limiting the results.
     * @param <T> 
     * @param type the type to match
     * @param limit the max number of results to return.
     * @param properties The properties to match
     * @return A list containing objects which exactly match all of the given properties
     * @throws IllegalArgumentException if: properties.length%2 !=0, or any properties[i] (i%2==0) is not a string.
     * This method takes the provided properties and builds an expression from them.  The assumption is that 
     * properties looks like: { propertyName, propertyValue, propertyName2, propertyValue2,...}.
     * The properties are "and'ed" together. The objects are ordered according to the rules for listAll. 
     * For more control over the expression and/or ordering, try listMatching.
     */
    <T> List<T> findByProperty(Class<T> type, int limit, Object... properties);
    
    
    /**
     * Finds all objects of the provided type which match any of the properties provided.
     * @param <T>
     * @param type the type to match
     * @param properties  the properties to match
     * @return A list containing the objects which exactly match any of the given properties
     * @throws IllegalArgumentException if: properties.length%2 != 0 or any properties[i] is not a string, with i%2==0.
     * This method takes the provided properties and builds an expression from them.  The assumption is that properties looks like:
     * { propertyName, propertyValue, propertyName2, propertyValue2,...}.
     * The properties are "or'ed" together.  The objects are ordered according to the rules for listAll.
     * For more control over the expression, try listMatching.
     */
    <T> List<T> findByAnyProperty(Class<T> type, Object... properties);
    
    /**
     * Finds all objects of the provided type which match any of the properties provided with a cap on the number of results returned.
     * @param <T>
     * @param type the type to match
     * @param limit the max number of objects to match.
     * @param properties  the properties to match
     * @return A list containing the objects which exactly match any of the given properties
     * @throws IllegalArgumentException if: properties.length%2 != 0 or any properties[i] is not a string, with i%2==0.
     * This method takes the provided properties and builds an expression from them.  The assumption is that properties looks like:
     * { propertyName, propertyValue, propertyName2, propertyValue2,...}.
     * The properties are "or'ed" together.  The objects are ordered according to the rules for listAll.
     * For more control over the expression, try listMatching.
     */
    <T> List<T> findByAnyProperty(Class<T> type, int limit, Object... properties);
    
    /**
     * Finds all objects of the provided type which partial-match any of the properties provided.
     * @param <T>
     * @param type
     * @param properties the properties to match
     * @return A list containing the objects which partially match any of the given properties
     * @throws IllegalArgumentException if: properties.length%2 != 0 or any properties[i], with i%2==0, is not a string.
     * This method takes the provided proeprties and builds an expression from them.  The assumption is that properties looks like:
     * { propertName, propertyValue, propertyName2, propertyValue2,...}.
     * The properties are "or'ed" together in a chain of "LIKE" expressions. Note that although the expressions used are "LIKE" expresisons,
     * it is the responsibility of the user to place wildcards in appropriate places.
     * The objects are ordered according to the rules for listAll.
     * For more control over the expression, try listMatching.
     * Example use:
     *   manager.findLikeAnyProperty(User.class,"login","foo%","firstName","bar%");
     * This would ultimately execute a query on the table underlying the User class, finding a user with a login like "foo%" or a firstName like "bar%".
     */
    <T> List<T> findLikeAnyProperty(Class<T> type, Object... properties);
    
    /**
     * Finds all objects of the provided type which partial-match any of the properties provided, limiting the results returned.
     * @param <T>
     * @param type the type of object to match
     * @param limit the max number of results to return
     * @param properties the properties to match
     * @return A list containing the objects which partially match any of the given properties
     * @throws IllegalArgumentException if: properties.length%2 != 0 or any properties[i], with i%2==0, is not a string.
     * This method takes the provided proeprties and builds an expression from them.  The assumption is that properties looks like:
     * { propertName, propertyValue, propertyName2, propertyValue2,...}.
     * The properties are "or'ed" together in a chain of "LIKE" expressions. Note that although the expressions used are "LIKE" expresisons,
     * it is the responsibility of the user to place wildcards in appropriate places.
     * The objects are ordered according to the rules for listAll.
     * For more control over the expression, try listMatching.
     * Example use:
     *   manager.findLikeAnyProperty(User.class,"login","foo%","firstName","bar%");
     * This would ultimately execute a query on the table underlying the User class, finding a user with a login like "foo%" or a firstName like "bar%".
     */
    <T> List<T> findLikeAnyProperty(Class<T> type, int limit, Object... properties);
}
