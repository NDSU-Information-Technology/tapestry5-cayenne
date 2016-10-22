package com.googlecode.tapestry5cayenne.integration.app0.pages;

import com.googlecode.tapestry5cayenne.components.Select;
import com.googlecode.tapestry5cayenne.model.Artist;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;

/**
 * Created by IntelliJ IDEA.
 * User: kmenard
 * Date: Jun 13, 2008
 * Time: 4:44:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestSelect
{
    @Persist
    @Property
    @SuppressWarnings("unused")
    private Artist artist;

    @SuppressWarnings("unused")
    @Component
    private Form form;

    @Component(parameters={
            "value=artist", "label=literal:Cayenne Select"
    })
    @SuppressWarnings("unused")
    private Select select;

}
