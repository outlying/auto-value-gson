package com.ryanharter.auto.value.gson;

import com.google.auto.value.AutoValue;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
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
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
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
