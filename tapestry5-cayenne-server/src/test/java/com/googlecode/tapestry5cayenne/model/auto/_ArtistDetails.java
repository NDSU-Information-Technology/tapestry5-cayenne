package com.googlecode.tapestry5cayenne.model.auto;

import java.util.Date;

import org.apache.cayenne.CayenneDataObject;

import com.googlecode.tapestry5cayenne.model.Artist;

/**
 * Class _ArtistDetails was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _ArtistDetails extends CayenneDataObject {

    public static final String BIO_PROPERTY = "bio";
    public static final String BIRTHDATE_PROPERTY = "birthdate";
    public static final String DEATHDATE_PROPERTY = "deathdate";
    public static final String ARTIST_PROPERTY = "artist";

    public static final String ARTISTID_PK_COLUMN = "artistid";

    public void setBio(String bio) {
        writeProperty("bio", bio);
    }
    public String getBio() {
        return (String)readProperty("bio");
    }

    public void setBirthdate(Date birthdate) {
        writeProperty("birthdate", birthdate);
    }
    public Date getBirthdate() {
        return (Date)readProperty("birthdate");
    }

    public void setDeathdate(Date deathdate) {
        writeProperty("deathdate", deathdate);
    }
    public Date getDeathdate() {
        return (Date)readProperty("deathdate");
    }

    public void setArtist(Artist artist) {
        setToOneTarget("artist", artist, true);
    }

    public Artist getArtist() {
        return (Artist)readProperty("artist");
    }


}
