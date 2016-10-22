package com.googlecode.tapestry5cayenne.model;

import org.apache.tapestry5.beaneditor.NonVisual;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.annotations.Label;
import com.googlecode.tapestry5cayenne.model.auto._Bid;

@SuppressWarnings("serial")
//use something different than what ordering by getLabel would give you...
@DefaultOrder("amount")
public class Bid extends _Bid {

    
    @Label
    @NonVisual
    public String getLabel() {
        return String.format("%s - %s - $%.2f",
                getPainting().getArtist().getName(),
                getPainting().getTitle(),
                getAmount());
    }
}
