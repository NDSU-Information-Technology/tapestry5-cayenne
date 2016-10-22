package com.googlecode.tapestry5cayenne.model;

import com.googlecode.tapestry5cayenne.model.auto._App0Map;

public class App0Map extends _App0Map {

    private static App0Map instance;

    private App0Map() {}

    public static App0Map getInstance() {
        if(instance == null) {
            instance = new App0Map();
        }

        return instance;
    }
}
