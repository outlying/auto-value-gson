package com.ryanharter.auto.value.gson.sample.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Dog {

    abstract String getName();

    public static Dog create(){
        return null;
    }

    public static TypeAdapter<Dog> randomNameToFind(Gson gson) {
        return new AutoValue_Dog.GsonTypeAdapter(gson);
    }
}
