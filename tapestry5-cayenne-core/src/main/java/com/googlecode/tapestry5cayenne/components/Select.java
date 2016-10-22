package com.googlecode.tapestry5cayenne.components;

import com.googlecode.tapestry5cayenne.PersistentEntitySelectModel;
import com.googlecode.tapestry5cayenne.services.PersistentManager;
import org.apache.cayenne.Persistent;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Displays a selection list for Cayenne persistent objects.  Designed to
 * be used inside of custom forms.
 *
 * @author Kevin Menard
 */
public class Select implements Field
{
    @Inject
    @Property
    private ComponentResources resources;

    @Inject
    private PersistentManager manager;

    @Parameter
    private Persistent value;

    @Parameter
    private String label;

    @Parameter
    private boolean disabled;

    @Component(
            inheritInformalParameters = true,
            parameters = {
                    "value=inherit:value",
                    "label=inherit:label",
                    "disabled=inherit:disabled",
                    "model=model"
            },
            publishParameters = "blankLabel,blankOption,encoder,validate,zone"
    )
    private org.apache.tapestry5.corelib.components.Select select;

    @SuppressWarnings("unchecked")
    public SelectModel getModel()
    {
        return new PersistentEntitySelectModel(resources.getBoundType("value"), manager);
    }


    public String getControlName() {
        return select.getControlName();
    }

    public String getLabel() {
        return label;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isRequired() {
        return select.isRequired();
    }

    public String getClientId() {
        return select.getClientId();
    }
}
