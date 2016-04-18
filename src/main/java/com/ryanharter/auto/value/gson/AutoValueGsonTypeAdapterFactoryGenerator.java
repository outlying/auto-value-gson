package com.ryanharter.auto.value.gson;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Generates AutoValueGsonTypeAdapterFactory.java file
 */
class AutoValueGsonTypeAdapterFactoryGenerator {

    private static final String FILE_NAME = "AutoValueGsonTypeAdapterFactory";
    private static final String TYPE_CONDITION = "if(rawType.equals($T.class))";

    private final Set<Class> classSet = new HashSet<>();

    /**
     * Add class for type factory
     *
     * @param aClass any class
     */
    void add(Class aClass) {
        classSet.add(aClass);
    }

    /**
     * Builds source file
     *
     * @throws IOException
     */
    void generate() throws IOException {

        TypeName generic = TypeVariableName.get("T");
        TypeName typeToken = ParameterizedTypeName.get(ClassName.get(TypeToken.class), generic);
        TypeName typeAdapter = ParameterizedTypeName.get(ClassName.get(TypeAdapter.class), generic);

        CodeBlock.Builder codeBlock = CodeBlock.builder()
                .addStatement("Class<? super $L> rawType = type.getRawType()", generic);

        boolean first = true;
        for (Class processClass : classSet){
            if(first){
                codeBlock.beginControlFlow(TYPE_CONDITION, processClass);
                first = false;
            } else {
                codeBlock.nextControlFlow("else " + TYPE_CONDITION, processClass);
            }
            codeBlock.addStatement("return (TypeAdapter<$L>) new $T.typeAdapter(gson)", generic, processClass);
        }
        codeBlock.endControlFlow();
        codeBlock.addStatement("return null");

        MethodSpec.Builder factoryMethodBuilder = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Gson.class, "gson")
                .addParameter(typeToken, "type")
                .addCode(codeBlock.build())
                .returns(typeAdapter);

        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(FILE_NAME)
                .superclass(TypeAdapterFactory.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(factoryMethodBuilder.build());

        JavaFile javaFile = JavaFile.builder("", typeSpecBuilder.build())
                .addFileComment("Auto-generated do not modify !")
                .build();

        javaFile.writeTo(System.out); // TODO change destination
    }
}
