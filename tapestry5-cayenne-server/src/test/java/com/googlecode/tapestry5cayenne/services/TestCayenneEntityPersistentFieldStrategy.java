package com.googlecode.tapestry5cayenne.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.PersistenceState;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.PersistentFieldChange;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;

@Test(groups="all")
public class TestCayenneEntityPersistentFieldStrategy {
    
    private CayenneEntityPersistentFieldStrategy _strategy;
    private Registry _registry;
    private ObjectContext _context;
    private Session _session;
    private Request _request;
    private List<Artist> _data;
    
    
    @BeforeClass
    void setupDB() throws Exception {
        TestUtils.setupdb();
        _registry = TestUtils.setupRegistry("App0", TapestryCayenneModule.class);
        _context = _registry.getService(ObjectContextProvider.class).currentContext();
        _data = TestUtils.basicData(_context);
    }
    
    @SuppressWarnings("unchecked")
    @BeforeMethod
    void setupObjects() {
        _request = EasyMock.createMock(Request.class);
        _session = EasyMock.createMock(Session.class);
        EasyMock.expect(_request.getSession(true)).andReturn(_session).anyTimes();
        EasyMock.expect(_request.getSession(false)).andReturn(_session).anyTimes();
        EasyMock.replay(_request);
        _strategy = new CayenneEntityPersistentFieldStrategy(_request,_registry.getService("CayenneEntityEncoder",ValueEncoder.class));
    }
    
    @AfterMethod
    void verify() {
        EasyMock.verify(_request,_session);
    }

    public void testPostChange() {
        _context.commitChanges();
        _session.setAttribute("CayenneEntity:testPage:foo:bar", "Artist::" + DataObjectUtils.intPKForObject(_data.get(0)));
        EasyMock.expectLastCall().once();
        EasyMock.replay(_session);
        _strategy.postChange("testPage", "foo", "bar", _data.get(0));
    }
    
    public void testGatherFieldChanges() {
        List<String> vals = new ArrayList<String>();
        vals.add("CayenneEntity:testPage:foo:bar");
        _data.get(0).setName("TemporaryChange");
        EasyMock.expect(_session.getAttributeNames("CayenneEntity:testPage:"))
            .andReturn(vals);
        EasyMock.expect(_session.getAttribute("CayenneEntity:testPage:foo:bar"))
            .andReturn("Artist::" + DataObjectUtils.intPKForObject(_data.get(0)));
        EasyMock.replay(_session);
        Collection<PersistentFieldChange> changes = _strategy.gatherFieldChanges("testPage");
        Assert.assertEquals(changes.size(),1);
        PersistentFieldChange c = changes.iterator().next();
        Assert.assertEquals(c.getComponentId(),"foo");
        Assert.assertEquals(c.getFieldName(),"bar");
        Artist a = (Artist) c.getValue();
        Assert.assertEquals(a,_data.get(0));
        Assert.assertEquals(a.getName(),"TemporaryChange");
        Assert.assertEquals(a.getPersistenceState(),PersistenceState.MODIFIED);
    }
    
    public void testGatherFieldChangesWithNull() {
        List<String> vals = new ArrayList<String>();
        vals.add("CayenneEntity:testPage:foo:bar");
        EasyMock.expect(_session.getAttributeNames("CayenneEntity:testPage:")).andReturn(vals);
        EasyMock.expect(_session.getAttribute("CayenneEntity:testPage:foo:bar"))
            .andReturn(null);
        EasyMock.replay(_session);
        Collection<PersistentFieldChange> changes = _strategy.gatherFieldChanges("testPage");
        Assert.assertEquals(changes.size(),1);
        PersistentFieldChange c = changes.iterator().next();
        Assert.assertEquals(c.getComponentId(),"foo");
        Assert.assertEquals(c.getFieldName(),"bar");
        Assert.assertNull(c.getValue());
    }
}
