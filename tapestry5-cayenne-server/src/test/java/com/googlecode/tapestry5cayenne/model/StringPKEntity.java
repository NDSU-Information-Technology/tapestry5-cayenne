package com.googlecode.tapestry5cayenne.model;

import com.googlecode.tapestry5cayenne.annotations.DefaultOrder;
import com.googlecode.tapestry5cayenne.model.auto._StringPKEntity;



@SuppressWarnings("serial")
@DefaultOrder(
        value={StringPKEntity.STRING_PROP1_PROPERTY,StringPKEntity.INT_PROP1_PROPERTY},
        ascending={true,false})
public class StringPKEntity extends _StringPKEntity {

}
