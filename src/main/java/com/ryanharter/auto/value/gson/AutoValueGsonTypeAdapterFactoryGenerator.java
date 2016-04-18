package com.ryanharter.auto.value.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.*;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates java file
 */
class AutoValueGsonTypeAdapterFactoryGenerator {

    private static final String FILE_NAME = "AutoValueGsonTypeAdapterFactory";
    private static final String TYPE_CONDITION = "if(rawType.equals($T.class))";

    private final HashMap<TypeElement, String> typeElements = new HashMap<>();

    void add(TypeElement typeElement, String methodName) {
        typeElements.put(typeElement, methodName);
    }

    /**
     * Builds source file
     *
     * @param filer to figure out file destination location
     * @throws IOException
     */
    void generate(Filer filer) throws IOException {

        if (typeElements.size() == 0) {
            return; // Don't generate for sake of it
        }

        TypeVariableName generic = TypeVariableName.get("T");
        TypeName typeToken = ParameterizedTypeName.get(ClassName.get(TypeToken.class), generic);
        TypeName typeAdapter = ParameterizedTypeName.get(ClassName.get(TypeAdapter.class), generic);

        CodeBlock.Builder codeBlock = CodeBlock.builder()
                .addStatement("Class<? super $L> rawType = type.getRawType()", generic);

        boolean first = true;
        for (Map.Entry<TypeElement, String> entry : typeElements.entrySet()) {
            if (first) {
                codeBlock.beginControlFlow(TYPE_CONDITION, entry.getKey());
                first = false;
            } else {
                codeBlock.nextControlFlow("else " + TYPE_CONDITION, entry.getKey());
            }

            // TODO find static methods with param Gson and return type TypeAdapter
            //typeElement.getClass().getMe

            codeBlock.addStatement("return (TypeAdapter<$L>) $T.$L(gson)", generic, entry.getKey(), entry.getValue());
        }
        if (!first) {
            codeBlock.endControlFlow();
        }
        codeBlock.addStatement("return null");

        MethodSpec.Builder factoryMethodBuilder = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addTypeVariable(generic)
                .addParameter(Gson.class, "gson")
                .addParameter(typeToken, "type")
                .addCode(codeBlock.build())
                .returns(typeAdapter);

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(FILE_NAME)
                .addSuperinterface(TypeAdapterFactory.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(factoryMethodBuilder.build());

        JavaFile javaFile = JavaFile.builder("", typeSpecBuilder.build())
                .indent("    ")
                .addFileComment("Auto-generated do not modify !")
                .build();


        JavaFileObject file = filer.createSourceFile(FILE_NAME);
        Writer writer = file.openWriter();

        javaFile.writeTo(writer);

        writer.close();
    }
}
