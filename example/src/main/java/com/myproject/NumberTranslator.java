package com.myproject;

public class NumberTranslator implements Behavior {

    public String translate(Object obj) {
        return serialize(obj);
    }
}
