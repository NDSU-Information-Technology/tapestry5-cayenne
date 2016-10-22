package com.googlecode.tapestry5cayenne.integration.app0.pages;

import java.util.List;

import org.apache.cayenne.query.EJBQLQuery;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.BeanDisplay;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;

import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;

public class TestToManyControl {
    
    @Inject
    private ObjectContextProvider _provider;
    
    @SuppressWarnings("unused")
    @Persist
    @Property
    private Artist _artist;
    
    @Property
    private List<Artist> _artistList;
    
    @Component(parameters={
            "object=artist","model=artistModel"
    })
    @SuppressWarnings("unused")
    private BeanDisplay _display;
    
    @SuppressWarnings("unused")
    @Component(parameters={"source=artistList","model=artistModel"})
    private Grid _gridDisplay;
    
    
    @Inject
    private BeanModelSource source;
    
    @Inject
    private Messages messages;
    
    public BeanModel<Artist> getArtistModel() {
        BeanModel<Artist> ret = source.createDisplayModel(Artist.class, messages);
        ret.include(Artist.PAINTING_LIST_PROPERTY, Artist.PAINTINGS_BY_TITLE_PROPERTY);
        return ret;
    }
    
    @SetupRender
    @SuppressWarnings("unchecked")
    void init() {
        _artistList = _provider.currentContext().performQuery(
                new EJBQLQuery("select a from Artist a order by a.name"));
        if (!_artistList.isEmpty()) {
            _artist = (Artist) _artistList.get(0);
        }
    }
    
}
