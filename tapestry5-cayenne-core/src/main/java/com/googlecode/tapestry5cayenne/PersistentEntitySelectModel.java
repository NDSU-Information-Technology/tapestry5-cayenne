package com.googlecode.tapestry5cayenne;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.cayenne.query.Ordering;
import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.util.AbstractSelectModel;

import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.internal.AnnotationFinder;
import com.googlecode.tapestry5cayenne.internal.MethodLabelOptionModel;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

/**
 * Defines a selection model for use with cayenne relationships.
 * The model will build a list of all objects of the type provided to the constructor.
 * Objects will be ordered according to the following rules:
 * 1) If orderings are explicitly provided via the varags constructor parameter, those orderings will be used.
 * 2) If the class type has a method annotated with the Label annotation, then the objects will be 
 *  ordered by the result of invoking the labeled method.  The model will check to see if the method
 *  corresponds to a known database property.  If so, the sort will occur at the database level.
 *  If not, the sort will occur in-memory.  
 * 3) If no label is found, but the class is an instance of comparable, the objects will be sorted in-memory
 *  according to their "comparable" ordering.
 * 4) If no method can be found and the objects are not comparable, no sorting will occur.
 * 
 * Each option in the model displays a single Persistent object.  The user-presented value for the object
 * is the result of invoking a @Label-annotated method, if present, or toString().
 * 
 * @author robertz
 * @see Label
 */
public class PersistentEntitySelectModel<T> extends AbstractSelectModel {
    
    private final List<OptionModel> _options;
    
    /**
     * Constructs the model by looking up the entities corresponding to type, using the provided ObjectContext.
     * All provided orderings will be honored.  
     */
    public PersistentEntitySelectModel(Class<T> type, PersistentManager manager, Ordering...orderings) {
        this(type, manager.listAll(type, orderings));
    }

    public PersistentEntitySelectModel(Class<T> type, List<T> options) {
        Method label = AnnotationFinder.methodForAnnotation(Label.class, type);
        _options = new ArrayList<OptionModel>();
        for(Object obj : options) {
            _options.add(new MethodLabelOptionModel(obj,label));
        }
    }

    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    public List<OptionModel> getOptions() {
        return _options;
    }
}
