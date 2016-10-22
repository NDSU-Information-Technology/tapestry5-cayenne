package com.googlecode.tapestry5cayenne.model;


import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.model.auto._Artist;



@SuppressWarnings("serial")
public class Artist extends _Artist implements Comparable<Artist>{
    
    @Override
    @Label
    public String getName() {
        return super.getName();
    }
    
    public Integer getNumPaintings() {
        return getPaintingList().size();
    }
    
    public int numPaintings() {
        return getPaintingList().size();
    }

    public int compareTo(Artist o) {
        return getName().compareTo(o.getName());
    }

}
