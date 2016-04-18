package com.ryanharter.auto.value.gson;

import com.google.auto.value.AutoValue;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Arrays;
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
                for (Element subElement : element.getEnclosedElements()) {
                    if (isElementValid(subElement)) {
                        ExecutableElement executableElement = (ExecutableElement) subElement;
                        if (isExecutableElementValid(executableElement)) {
                            generator.add((TypeElement) element, executableElement.getSimpleName().toString());
                        }
                    }
                }
            }
        }

        try {
            generator.generate(processingEnv.getFiler());
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate output file");
        }

        return true;
    }

    private boolean isExecutableElementValid(ExecutableElement executableElement) {
        return !executableElement.getParameters().isEmpty() &&
                executableElement.getParameters().get(0).asType().toString().equals("com.google.gson.Gson");
        // TODO missing return parameter check, would be better to check that
    }

    private boolean isElementValid(Element subElement) {
        return subElement.getKind() == ElementKind.METHOD &&
                subElement.getModifiers().containsAll(Arrays.asList(Modifier.STATIC, Modifier.PUBLIC));
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
