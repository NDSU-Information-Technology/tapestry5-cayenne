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
package com.googlecode.tapestry5cayenne.integration.app0.pages;

import java.sql.SQLException;
import java.util.List;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.googlecode.tapestry5cayenne.annotations.CommitAfter;
import com.googlecode.tapestry5cayenne.model.Artist;
import com.googlecode.tapestry5cayenne.services.PersistentManager;

public class CommitAfterTestPage {

  @Inject
  private ObjectContext context;

  @Inject
  private PersistentManager manager;

  // note that you have to @Persist the artist through requests to ensure that
  // you are using the correct artist each time.
  // Otherwise, having multiple artists can throw things off. But, to fully test
  // CommitAfter, you also have to refresh
  // the artist from the DB each time.
  @Property
  @Persist
  private Artist artist;

  @SetupRender
  public void setupRender() {
    if (artist == null) {
      List<Artist> artists = manager.listAll(Artist.class, new Ordering("name", SortOrder.ASCENDING));
      if (artists.isEmpty()) {
        artist = context.newObject(Artist.class);
        artist.setName("Dali");
        context.commitChanges();
      } else {
        artist = artists.get(0);
      }
    } else {
      // force a DB refresh...
      artist = (Artist) DataObjectUtils.objectForPK(context, artist.getObjectId());
    }
  }

  @CommitAfter
  void onActionFromCommitOk(Artist artist) {
    artist.setName("Commitokname");
  }

  @CommitAfter
  void doActionFromRuntimeException(Artist artist) {
    artist.setName("Failedsavename");
    throw new RuntimeException("ignore");
  }

  void onActionFromRuntimeException(Artist artist) {
    try {
      doActionFromRuntimeException(artist);
    } catch (RuntimeException e) {
      // Ignore
    }
  }

  @CommitAfter
  void doActionFromCheckedException(Artist artist) throws SQLException {
    artist.setName("savesokwithcheckedexceptionname");
    throw new SQLException("blah");
  }

  void onActionFromCheckedException(Artist artist) {
    try {
      doActionFromCheckedException(artist);
    } catch (SQLException e) {
      // Ignore
    }
  }
}
