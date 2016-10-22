package com.googlecode.tapestry5cayenne.internal;

import java.util.List;

import org.apache.cayenne.BaseContext;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Query;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

/**
 * Implements a GridDataSource for queries.
 * Note that the current coercion from query to grid data source does NOT make use
 * of this class (yet); this class should be considered "experimental" for the time being.
 * @author robertz
 *
 */
public class QueryGridDataSource implements GridDataSource {
    
    private final Query query;
    
    @SuppressWarnings("unchecked")
    private List currentPage;
    
    private final int nrows;
    
    @SuppressWarnings("unchecked")
    private Class rowClass;
    
    public QueryGridDataSource(Query q,ObjectContext context) {
        this.query = q;
        //just set the page size for now.
        //We'll be changing this later, anyway, so just the first object.
        QueryType.typeForQuery(query).setPageSize(query, 1);
        currentPage = context.performQuery(query);
        nrows = currentPage.size();
        if (nrows > 0) {
            rowClass = currentPage.get(0).getClass();
        }
    }

    public int getAvailableRows() {
        return nrows;
    }

    public Class<?> getRowType() {
        return rowClass;
    }

    public Object getRowValue(int index) {
        if (currentPage.size() > index) {
	        return currentPage.get(index);
        }
        return null;
    }

    public void prepare(int startIndex, int endIndex,
            List<SortConstraint> sortConstraints) {
        int pageSize = endIndex - startIndex;
        QueryType.typeForQuery(query).setPageSize(query, pageSize);
        currentPage = BaseContext.getThreadObjectContext().performQuery(query);
    }

}
