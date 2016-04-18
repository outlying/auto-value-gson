package com.ryanharter.auto.value.gson;

import com.google.auto.value.AutoValue;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Process classes with @AutoValue annotation and generate proper TypeAdapterFactory for all of them
 */
public class AutoValueGsonAnnotationProcessor extends AbstractProcessor {

    private static final Set<String> SUPPORTED_ANNOTATION_TYPES;

    static {
        SUPPORTED_ANNOTATION_TYPES = new HashSet<>(Collections.singleton(AutoValue.class.getCanonicalName()));
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        AutoValueGsonTypeAdapterFactoryGenerator generator = new AutoValueGsonTypeAdapterFactoryGenerator();

        for (Element element : roundEnv.getElementsAnnotatedWith(AutoValue.class)) {
            if (element.getKind() == ElementKind.CLASS) {

                // Validate class, we need class A with public static method:
                // TypeAdapter<A> someName(Gson gson)

                TypeElement typeElement = (TypeElement) element;

                generator.add(typeElement);

            }
        }

        try {
            generator.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }



    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return SUPPORTED_ANNOTATION_TYPES;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
