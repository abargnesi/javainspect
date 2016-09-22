package com.myproject;

public interface Behavior {

    String translate(Object obj);

    default String serialize(Object obj) {
        return obj.toString();
    }
}
