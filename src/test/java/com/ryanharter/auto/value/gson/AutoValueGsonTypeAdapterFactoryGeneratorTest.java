package com.ryanharter.auto.value.gson;

import com.ryanharter.auto.value.gson.processor.Cat;
import com.ryanharter.auto.value.gson.processor.Dog;
import org.junit.Test;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public class AutoValueGsonTypeAdapterFactoryGeneratorTest {

    @Test
    public void testFileGeneration() throws Exception {

        // Given
        AutoValueGsonTypeAdapterFactoryGenerator generator = new AutoValueGsonTypeAdapterFactoryGenerator();

        // When
        generator.generate();

        // Then
        // TODO check output
    }

}