package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.query.Ordering;

/**
 * Data Storage class for holding information about how the query sort should take place.
 * @author robertz
 *
 */
//note that there still seem to be some quirks wrt tapestry's live service reloading. Had to move this
//class out.  It works fine in a real app, but when testing against PageTester, I was seeing access-related exceptions.
public class QuerySortResult {
    public QuerySortResult() {

    }

    public QuerySortResult(QuerySortType type, Ordering ordering) {
        this.type=type;
        this.ordering = ordering;
    }
    public QuerySortType type;
    public Ordering ordering;
}