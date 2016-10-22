package com.googlecode.tapestry5cayenne.internal;

import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;

/**
 * Utility class for quick conversion from string to Ordering.
 * @author robertz
 * 
 */
public class OrderingUtils {
    
    private OrderingUtils() {}

    /**
     * Converts the provided property names to orderings, assuming an ordering of ascending for all properties.
     * @param vals
     * @return an array of Ordering objects.
     */
    public static Ordering[] stringToOrdering(String...vals) {
        return stringToOrdering(true,vals);
    }
    
    /**
     * Converts the provided property names to orderings.  
     * All orderings will be ascending or descending, according to the ascending parameter.
     * @param ascending
     * @param vals
     * @return
     */
    public static Ordering[] stringToOrdering(boolean ascending,String...vals) {
        Ordering[] o = new Ordering[vals.length];
        for(int i=0;i<o.length;i++) {
            o[i]=new Ordering(vals[i],ascending?SortOrder.ASCENDING:SortOrder.DESCENDING);
        }
        return o;
    }
    
}
