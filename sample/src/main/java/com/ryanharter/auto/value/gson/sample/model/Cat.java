package com.ryanharter.auto.value.gson.sample.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Cat {

    abstract String getName();

    static Cat create(String name){
        return AutoValue_Cat.create(name);
    }
}
