package com.googlecode.tapestry5cayenne.components;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cayenne.Persistent;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.map.ObjAttribute;
import org.apache.cayenne.map.ObjEntity;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Mixins;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.SearchType;
import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.internal.AnnotationFinder;
import com.googlecode.tapestry5cayenne.internal.Labeler;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

/**
 * Component which renders a text field that uses auto-complete to select an entity.
 * By default, the search is done on the database side, using all available ObjAttributes for the entity. 
 * Users may supply custom property paths to search through to override the default behavior.
 * 
 * The properties may be searched by prefix (like 'input%'), suffix(like '%input'), or anywhere (like '%input%'), with
 * anywhere being the default (see {@link SearchType} for more details).
 * 
 * The text displayed to the user for the suggestion list is the {@link Label} if it exists, otherwise the result of toString.
 * The list of suggestions is ordered according to {@link DefaultOrder} if present, {@link Label} if DefaultOrder is missing and Label is present,
 * and otherwise the result of toString() on the object.
 * 
 * The component fires two events which expect a return type for the callback to be Persistent.
 * The first event is "missingentity".  This is fired when the text typed by the user results in no entities being returned from the database.
 * This event provides the opportunity for applications to create entities "on demand" based on the text input by the user.
 * If there is no event handler, the bound value will be set to null. Event handlers can expect a single String parameter which is the user-entered text:
 * <code>
 * @OnEvent(EntityField.MISSING_ENTITY)
 * Persistent onMissingentityFromXYZ(String input) {
 *   Persistent obj =...;
 *   ...
 *   return obj;
 * }
 * </code>
 * 
 * The second event is the the "multipleentities" event, which is fired when the user's text matches multiple input items.
 * This gives application code the chance to select the appropriate item to use.  If there is no handler, the bound value
 * will be set to the first item returned in the list. Handlers can expect two parameters: first, the string entered by the user;
 * second, the list of values fetched from the database.
 * <code>
 * @OnEvent(EntityField.MULTIPLE_ENTITIES)
 * Persistent onMultipleentitiesFromXYZ(String input, List<Persistent> values) {
 *   correctIndex=0;
 *   //resolve the issue...
 *   ...
 *   return values.get(correctIndex);
 * }
 * 
 * @author robertz
 *
 */
public class EntityField {
    /**
     * Name of the Event fired when the text input by a user matches no entities.
     * Firing this event gives application code a chance to create the appropriate entity
     * corresponding to user text, if desired.  The event is fired with the user-input text as the sole 
     * object parameter.  So handlers should look like:
     * Persistent onMissingentityFromXXX(String text) {
     *   ...
     *   return obj;
     * }
     */
    public static final String MISSING_ENTITY="missingentity";
    
    /**
     * Name of the Event fired when 
     */
    public static final String MULTIPLE_ENTITIES="multipleentities";
    
    //when we switch to 5.0.14, uncomment this line.
    //@Parameter(required=true,allowNull=true)
    /**
     * The Persistent value to edit.
     */
    @Parameter(required=true)
    private Persistent value;

    /**
     * A comma delimited list of database paths to search.
     * By default, EntityField will search through all available 
     * ObjAttributes for the ObjEntity corresponding to the Persistent value.  This property allows for 
     * that search to be overridden with specific property paths that should be searched.
     * For example, an entity such as User might have fields:
     *   firstName
     *   lastName
     *   username
     *   relationship1...
     * Entity field wil search through firstName, lastName, and usename.
     * If you only wanted to search firstName and lastName, you would specify:
     * searchProperties="firstName,lastName"
     */
    @Parameter(required=false)
    private String searchProperties;

    /**
     * searchType determines how the search is performed. See {@link SearchType}.
     */
    @Parameter(required=false)
    private SearchType searchType=SearchType.ANYWHERE;
    
    /**
     * Allows the application to supply additional winnowing criteria on the search query, as required.
     * This expression will be "anded" to the search criteria generated by this component.
     */ @Parameter(required=false)
    private Expression additionalCriteria;
    
    /**
     * Maximum number of results to return to the user. 
     * 0 for unlimited (the default).
     */
    @Parameter(defaultPrefix=BindingConstants.LITERAL)
    private int resultLimit=0;

    public String getUserValue() {
        return Labeler.labelForObject(value);
    }

    public void setUserValue(String input) {
        List<? extends Persistent> objs = objsForInput(input);
        if (objs.isEmpty()) {
            resources.triggerEvent(
                    MISSING_ENTITY,
                    new Object[]{input},
                    new ComponentEventCallback<Persistent>() {
                        public boolean handleResult(Persistent result){
                            value=result;
                            return true;
                        }
                    });
        } else if (objs.size() > 1) {
            //set the default...
            this.value=objs.get(0);
            resources.triggerEvent( 
                    MULTIPLE_ENTITIES, 
                    new Object[]{input,objs}, 
                    new ComponentEventCallback<Persistent>() {
                        public boolean handleResult(Persistent result) {
                            value=result;
                            return true;
                        }
                    });
        } else {
            this.value = objs.get(0);
        }
    }

    @Inject
    private PersistentManager manager;

    @Inject
    private ComponentResources resources;

    @Inject
    private ObjectContextProvider provider;

    @SuppressWarnings("unused")
    @Component
    @Mixins("autocomplete")
    private TextField userValue;

    @OnEvent(value="providecompletions")
    String[] suggestions(String input) {
        ArrayList<String> tmp = new ArrayList<String>();
        Method m = AnnotationFinder.methodForAnnotation(Label.class, resources.getBoundType("value"));
        List<?> objs = objsForInput(input);
        for(Object obj : objs) {
            tmp.add(Labeler.labelForObject(obj,m));
        }
        return tmp.toArray(new String[tmp.size()]);
    }
    
    @SuppressWarnings("unchecked")
    private List<? extends Persistent> objsForInput(String input) {
        Class<? extends Persistent> type = resources.getBoundType("value");
        Expression e=null;
        input = searchType.maskInput(input);
        if (searchProperties == null || searchProperties.trim().equals("")) {
            ObjEntity entity = provider.currentContext().getEntityResolver().lookupObjEntity(type);
            Iterator<ObjAttribute> it = entity.getAttributes().iterator();
            if (it.hasNext()) {
                e = ExpressionFactory.likeExp(it.next().getName(), input);
            }
            for(;it.hasNext();) {
                e = e.orExp(ExpressionFactory.likeExp(it.next().getName(),input));
            }
        } else {
            String[] names = searchProperties.split(",");
            //not "" or null, so there must be at least one name...
            e = ExpressionFactory.likeExp(names[0], input);
            for(int i=1;i<names.length;i++) {
                e = e.orExp(ExpressionFactory.likeExp(names[i],input)); 
            }
        }
        if (additionalCriteria != null) {
            e = additionalCriteria.andExp(e);
        }
        if (resultLimit > 0) {
            return manager.listMatching(type, e, resultLimit);
        } else {
            return manager.listMatching(type, e);
        }
    }
}
