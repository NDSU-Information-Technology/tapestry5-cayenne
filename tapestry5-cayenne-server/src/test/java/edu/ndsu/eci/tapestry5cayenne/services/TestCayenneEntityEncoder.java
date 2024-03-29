/*
/*  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.ndsu.eci.tapestry5cayenne.services;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.Persistent;
import org.apache.tapestry5.commons.services.TypeCoercer;
import org.apache.tapestry5.ioc.Registry;
import org.easymock.EasyMock;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import edu.ndsu.eci.tapestry5cayenne.TestUtils;
import edu.ndsu.eci.tapestry5cayenne.model.AcceptedBid;
import edu.ndsu.eci.tapestry5cayenne.model.Artist;
import edu.ndsu.eci.tapestry5cayenne.model.Bid;
import edu.ndsu.eci.tapestry5cayenne.model.BigIntPKEntity;
import edu.ndsu.eci.tapestry5cayenne.model.Painting;
import edu.ndsu.eci.tapestry5cayenne.model.SmallIntPKEntity;
import edu.ndsu.eci.tapestry5cayenne.model.StringPKEntity;
import edu.ndsu.eci.tapestry5cayenne.model.TinyIntPKEntity;
import edu.ndsu.eci.tapestry5cayenne.services.CayenneEntityEncoder;
import edu.ndsu.eci.tapestry5cayenne.services.EncodedValueEncrypter;
import edu.ndsu.eci.tapestry5cayenne.services.NonPersistedObjectStorer;
import edu.ndsu.eci.tapestry5cayenne.services.ObjectContextProvider;
import edu.ndsu.eci.tapestry5cayenne.services.PersistentManager;
import edu.ndsu.eci.tapestry5cayenne.services.TapestryCayenneModule;

@Test(groups = "all")
public class TestCayenneEntityEncoder extends Assert {

  private CayenneEntityEncoder _encoder;
  private ObjectContextProvider _provider;
  private Registry _registry;
  private PersistentManager _manager;

  @BeforeClass
  void setupDB() throws Exception {
    TestUtils.setupdb();
    _registry = TestUtils.setupRegistry("App0", TapestryCayenneModule.class);
    _provider = _registry.getService(ObjectContextProvider.class);
    _manager = _registry.getService(PersistentManager.class);
    EncodedValueEncrypter enc = _registry.getService("PlainTextEncrypter", EncodedValueEncrypter.class);
    _encoder = createEncoder(enc);
  }

  @AfterTest
  void shutdownRegistry() {
    if (_registry != null) {
      _registry.shutdown();
    }
  }

  @SuppressWarnings("unused")
  @DataProvider(name = "conversions")
  private Object[][] conversionValues() {
    ObjectContext ctxt = _provider.currentContext();
    Artist a = _provider.currentContext().newObject(Artist.class);
    a.setName("test");
    StringPKEntity stringPk = ctxt.newObject(StringPKEntity.class);
    TinyIntPKEntity tinyPk = ctxt.newObject(TinyIntPKEntity.class);
    // have to explicitly set the id here because cayenne wants
    // to start id numbering at 200, and 200 is already too big for tinyPk.
    tinyPk.setObjectId(new ObjectId("TinyIntPKEntity", "id", 1));
    SmallIntPKEntity smallPk = ctxt.newObject(SmallIntPKEntity.class);
    BigIntPKEntity bigPk = ctxt.newObject(BigIntPKEntity.class);
    // set this to be something bigger than Integer.class can handle
    // (unsigned) int is just over 4 billion, so, 5 billion.
    bigPk.setObjectId(new ObjectId("BigIntPKEntity", "id", 5000000000L));
    stringPk.setId("testingstrings");

    Painting p = _provider.currentContext().newObject(Painting.class);
    p.setArtist(a);
    p.setPrice(25.0);
    p.setTitle("A work in progress");
    Bid b = _provider.currentContext().newObject(Bid.class);
    b.setAmount(new BigDecimal(25.00));
    b.setBidder(a);
    b.setPainting(p);
    AcceptedBid ab = _provider.currentContext().newObject(AcceptedBid.class);
    ab.setAcceptor(a);
    ab.setBid(b);
    ab.setAcceptedDate(new Date());
    _provider.currentContext().commitChanges();
    Artist a2 = new Artist();
    Artist a3 = _provider.currentContext().newObject(Artist.class);

    return new Object[][] {
        // Null object handling.
        { null, "nil" },
        // transient object handling
        { a2, "Artist::" + "t::" + a2.hashCode() },
        // "new" object handling.
        { a3, "Artist::" + "t::" + a3.hashCode() },
        // committed object handling, int pk.
        { a, "Artist::" + Cayenne.intPKForObject(a) },
        // committed object handling non-numeric pk
        { stringPk, "StringPKEntity::testingstrings" },
        // committed object, non INTEGER pks...
        { tinyPk, "TinyIntPKEntity::" + Cayenne.intPKForObject(tinyPk) },
        { smallPk, "SmallIntPKEntity::" + Cayenne.intPKForObject(smallPk) },
        { bigPk, "BigIntPKEntity::" + Cayenne.longPKForObject(bigPk) },
        { ab, "AcceptedBid::" + Cayenne.intPKForObject(a) + "::" + Cayenne.intPKForObject(b) }
        // TODO might be nice to have a way to store objs in the url in a
        // "tamper-proof" fashion.
        // at least as an option.
    };
  }

  @Test(dataProvider = "conversions")
  void testConversion(Persistent serverVal, String clientVal) {
    String client = _encoder.toClient(serverVal);
    assertEquals(client, clientVal, "Encoder incorrectly encoded artist value");
    Persistent server = _encoder.toValue(client);
    assertEquals(server, serverVal, "Encoder incorrectly converted client value to server value");
  }

  @Test
  void testDoubleConvertNewObject() {
    Artist a = _provider.currentContext().newObject(Artist.class);
    a.setName("TestArtist");
    String clientString = _encoder.toClient(a);
    assertEquals(a, _encoder.toValue(clientString));
    assertEquals(a, _encoder.toValue(clientString));
  }

  @DataProvider(name = "strings")
  Object[][] strings() {
    return new Object[][] { { "" }, { " " }, { "\t" }, };
  }

  @Test(dataProvider = "strings")
  void testEmptyStringHandling(String string) {
    assertNull(_encoder.toValue(string));
  }

  public void test_encrypt_called_in_toClient() {
    Artist a = _provider.currentContext().newObject(Artist.class);
    a.setName("test");
    _provider.currentContext().commitChanges();
    String value = "Artist::" + Cayenne.intPKForObject(a);

    EncodedValueEncrypter enc = EasyMock.createMock(EncodedValueEncrypter.class);
    EasyMock.expect(enc.encrypt(value)).andReturn(value);
    EasyMock.replay(enc);

    CayenneEntityEncoder encoder = createEncoder(enc);

    String actualValue = encoder.toClient(a);
    assertEquals(actualValue, value);
    EasyMock.verify(enc);
  }

  public void test_encrypt_called_for_nullvalue() {
    EncodedValueEncrypter enc = EasyMock.createMock(EncodedValueEncrypter.class);
    EasyMock.expect(enc.encrypt("nil")).andReturn("nil");
    EasyMock.replay(enc);

    CayenneEntityEncoder encoder = createEncoder(enc);
    assertEquals(encoder.toClient(null), "nil");
    EasyMock.verify(enc);
  }

  public void test_encrypt_called_for_transientvalue() {
    Artist a = new Artist();

    EncodedValueEncrypter enc = EasyMock.createMock(EncodedValueEncrypter.class);
    String val = "Artist::t::" + a.hashCode();
    EasyMock.expect(enc.encrypt(val)).andReturn(val);
    EasyMock.replay(enc);

    CayenneEntityEncoder encoder = createEncoder(enc);

    assertEquals(encoder.toClient(a), val);
    EasyMock.verify(enc);

  }

  public void test_encrypt_called_for_newvalue() {
    Artist a = _provider.currentContext().newObject(Artist.class);

    EncodedValueEncrypter enc = EasyMock.createMock(EncodedValueEncrypter.class);
    String val = "Artist::t::" + a.hashCode();
    EasyMock.expect(enc.encrypt(val)).andReturn(val);
    EasyMock.replay(enc);

    CayenneEntityEncoder encoder = createEncoder(enc);

    assertEquals(encoder.toClient(a), val);
    EasyMock.verify(enc);

  }

  public void test_decrypt_called_in_toValue() {
    Artist a = _provider.currentContext().newObject(Artist.class);
    a.setName("test");
    _provider.currentContext().commitChanges();
    String value = "Artist::" + Cayenne.intPKForObject(a);

    EncodedValueEncrypter enc = EasyMock.createMock(EncodedValueEncrypter.class);
    EasyMock.expect(enc.decrypt(value)).andReturn(value);
    EasyMock.replay(enc);

    CayenneEntityEncoder encoder = createEncoder(enc);
    Artist a2 = (Artist) encoder.toValue(value);
    assertEquals(a2, a);
    EasyMock.verify(enc);
  }

  private CayenneEntityEncoder createEncoder(EncodedValueEncrypter enc) {
    return new CayenneEntityEncoder(_provider, _registry.getService(TypeCoercer.class), _manager,
        _registry.getService(NonPersistedObjectStorer.class), enc);
  }

}
