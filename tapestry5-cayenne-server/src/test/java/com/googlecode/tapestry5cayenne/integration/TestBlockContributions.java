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
package com.googlecode.tapestry5cayenne.integration;

import com.googlecode.tapestry5cayenne.TestUtils;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.model.Painting;
import com.googlecode.tapestry5cayenne.services.ObjectContextProvider;
import org.apache.cayenne.Persistent;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.dom.Node;
import org.apache.tapestry5.ioc.IOCUtilities;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.test.PageTester;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

@Test(groups = "all", singleThreaded = true)
public class TestBlockContributions extends Assert {

  private Registry _registry;
  private PageTester _tester;
  private ObjectContextProvider _provider;
  private ValueEncoder<Persistent> _encoder;
  private List<Artist> _data;

  @SuppressWarnings("unchecked")
  @BeforeClass
  void setup() throws Exception {
    TestUtils.setupdb();
    _tester = new PageTester("com.googlecode.tapestry5cayenne.integration.app0", "app", "src/test/app0");
    _registry = _tester.getRegistry();
    _provider = _registry.getService(ObjectContextProvider.class);
    _data = TestUtils.basicData(_provider.currentContext());
    new Ordering(Artist.NAME.getName(), SortOrder.ASCENDING).orderList(_data);
    _encoder = _registry.getService("CayenneEntityEncoder", ValueEncoder.class);
  }

  @AfterClass
  void shutdown() {
    if (_tester != null) {
      _tester.shutdown();
    }
  }

  /**
   * Ensure that the toOneEditor is properly rendered
   */
  public void testToOneEditor() {
    Document doc = _tester.renderPage("TestToOneControl");
    // Verify the label
    Element el = doc.getRootElement().getElementByAttributeValue("for", "toOneList");
    assertEquals(el.getChildMarkup(), "Artist");

    // Verify the select list.
    // note that this is required, so the blank option isn't present,
    // so children size should be the same as data size.
    el = doc.getElementById("toOneList");
    assertEquals(el.getChildren().size(), _data.size());

    // we expect the list of items to be sorted by the @Label.
    Collections.sort(_data, new Comparator<Artist>() {
      public int compare(Artist o1, Artist o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    Iterator<Node> children = el.getChildren().iterator();
    for (Artist a : _data) {
      Element option = (Element) children.next();
      String val = option.getAttribute("value");
      Persistent obj = _encoder.toValue(val);
      assertEquals(obj, a, "Incorrect order of persistent objects!");
    }
  }

  /**
   * tests the "to_one" viewer; also tests editor submission.
   */
  public void testToOneViewer_and_toOneEditorSubmission() {
    // render the document, select the artist,
    // submit, then check the view.
    Document doc = _tester.renderPage("TestToOneControl");
    List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(), "body/form");
    assertFalse(els.isEmpty());
    Element form = els.get(0);

    Map<String, String> params = new HashMap<String, String>();
    params.put("price", "100.0");
    params.put("title", "dud");
    params.put("toOneList", _encoder.toClient(_data.get(1)));
    doc = _tester.submitForm(form, params);

    // make sure that the select is correctly selected.
    els = TestUtils.DOMFindAll(doc.getRootElement(), "body/form/select/option");
    try {
      PrintWriter writer = new PrintWriter(new File("/tmp/doc.txt"));
      doc.toMarkup(writer);
      writer.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    assertFalse(els.isEmpty());
    // find the option corresponding to _data.get(1).
    // remember, painting.artist is required, so no blank option, now that
    // t5cayenne automagically picks up those validations.
    assertTrue(els.get(1).getAttribute("selected").equals("selected"));
    assertTrue(els.get(1).getChildMarkup().equals("Picasso"));

    // make sure the output is correct.
    String markup = TestUtils.DOMFindAll(doc.getRootElement(), "body/dl/dt").get(2).getChildMarkup();
    assertEquals(markup, "Artist");
    markup = TestUtils.DOMFindAll(doc.getRootElement(), "body/dl/dd").get(2).getChildMarkup();
    assertEquals(markup, "Picasso");
  }

  /**
   * Ensures that the ToManyViewer.css link is properly rendered.
   */
  private Document assertToManyHead() {
    Document doc = _tester.renderPage("TestToManyControl");
    // make sure the stylesheet shows up.
    List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(), "head/link");

    // should be 2: one for tapestry, one for t5cayenne
    assertEquals(els.size(), 7);
    assertTrue(els.get(6).getAttribute("href").contains("ToManyViewer.css"));
    // ok... make sure we have the right thing on the bean display...
    return doc;
  }

  /**
   * Tests the behavior of the tomany view block & component with few elements
   * (should result in a listing of each element)
   */
  public void testToManyViewer_fewElements() {
    assertEquals(_data.get(0).getName(), "Dali");
    Document doc = assertToManyHead();
    List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(), "body/dl/dd/ul");
    // one for the paintingList property, and one for the paintings as a map
    // property.
    assertEquals(els.size(), 2);
    assertEquals(els.get(0).getChildren().size(), _data.get(0).getPaintingList().size());
    Iterator<Painting> it = _data.get(0).getPaintingList().iterator();
    for (Node n : els.get(0).getChildren()) {
      // should be a li...
      Element el = (Element) n;
      assertEquals(el.getName(), "li");
      assertEquals(el.getChildMarkup().trim(), doc.getMarkupModel().encode(it.next().toString()));
    }
    // now test the map...
    it = _data.get(0).getPaintingsByTitle().values().iterator();
    for (Node n : els.get(1).getChildren()) {
      Element el = (Element) n;
      assertEquals(el.getName(), "li");
      assertEquals(el.getChildMarkup().trim(), doc.getMarkupModel().encode(it.next().toString()));
    }
  }

  /**
   * Test what happens with lots of paintings. Currently, it should "kick over"
   * to generic descriptive text at 20 paintings.
   */
  @Test(dependsOnMethods = "testToManyViewer_fewElements")
  public void testToManyViewer_manyElements() {
    assertEquals(_data.get(0).getName(), "Dali");
    // add 18 paintings, because "TestUtils.basicData" (called from setup)
    // creates two paintings for each artist.
    List<Painting> paintings = TestUtils.addPaintings(_data.get(0), 18, _provider.currentContext());
    for (Painting p : paintings) {
      _data.get(0).addToPaintingList(p);
    }
    _data.get(0).getObjectContext().commitChanges();
    Document doc = assertToManyHead();

    List<Element> els = TestUtils.DOMFindAll(doc.getRootElement(), "body/dl/dd");
    assertEquals(els.get(0).getChildMarkup().trim(), "20 associated items");
    assertEquals(els.get(1).getChildMarkup().trim(), "20 associated items");
  }

  /**
   * Test that CayenneSelect renders properly
   */
  public void testCayenneSelect() {
    Document doc = _tester.renderPage("TestSelect");
    // Verify the label
    // Element el = doc.getElementById("toOneList:label");
    // assertEquals(el.getChildMarkup(),"Artist");

    // Verify the select list.
    Element el = doc.getElementById("select_0");
    assertEquals(el.getChildren().size() - 1, _data.size());

    // we expect the list of items to be sorted by the @Label.
    Collections.sort(_data, new Comparator<Artist>() {
      public int compare(Artist o1, Artist o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    Iterator<Node> children = el.getChildren().iterator();
    // skip the first node: it's blank.
    children.next();
    for (Artist a : _data) {
      Element option = (Element) children.next();
      String val = option.getAttribute("value");
      Persistent obj = _encoder.toValue(val);
      assertEquals(obj, a, "Incorrect order of persistent objects!");
    }
  }
}
