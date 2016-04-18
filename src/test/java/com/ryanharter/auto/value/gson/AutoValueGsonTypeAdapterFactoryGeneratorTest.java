package com.ryanharter.auto.value.gson;

import com.ryanharter.auto.value.gson.processor.Cat;
import com.ryanharter.auto.value.gson.processor.Dog;
import org.junit.Test;

import static org.junit.Assert.*;

public class AutoValueGsonTypeAdapterFactoryGeneratorTest {

    @Test
    public void testFileGeneration() throws Exception {

        // Given
        AutoValueGsonTypeAdapterFactoryGenerator generator = new AutoValueGsonTypeAdapterFactoryGenerator();
        generator.add(Dog.class);
        generator.add(Cat.class);

        // When
        generator.generate();

        // Then
        // TODO check output
    }
}