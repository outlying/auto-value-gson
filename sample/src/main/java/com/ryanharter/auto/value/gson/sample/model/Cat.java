package com.ryanharter.auto.value.gson.sample.model;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

@AutoValue
public abstract class Cat {

    abstract String getName();

    public static TypeAdapter<Cat> makeMeOne(Gson gson){

        return new AutoValue_Cat.GsonTypeAdapter(gson);
    }
}
