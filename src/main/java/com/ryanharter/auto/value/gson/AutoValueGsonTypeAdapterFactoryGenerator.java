package com.ryanharter.auto.value.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Generates java file
 */
class AutoValueGsonTypeAdapterFactoryGenerator {

    private static final String FILE_NAME = "AutoValueGsonTypeAdapterFactory";
    private static final String TYPE_CONDITION = "if(rawType.equals($T.class))";

    private final Set<TypeElement> typeElements = new LinkedHashSet<>();
    private String filePackage;

    void add(TypeElement typeElement) {
        typeElements.add(typeElement);

        filePackage = typeElement.getEnclosingElement().toString();
    }

    /**
     * Builds source file
     *
     * @throws IOException
     */
    void generate() throws IOException {

        TypeVariableName generic = TypeVariableName.get("T");
        TypeName typeToken = ParameterizedTypeName.get(ClassName.get(TypeToken.class), generic);
        TypeName typeAdapter = ParameterizedTypeName.get(ClassName.get(TypeAdapter.class), generic);

        CodeBlock.Builder codeBlock = CodeBlock.builder()
                .addStatement("Class<? super $L> rawType = type.getRawType()", generic);

        boolean first = true;
        for (TypeElement typeElement : typeElements) {
            if (first) {
                codeBlock.beginControlFlow(TYPE_CONDITION, typeElement);
                first = false;
            } else {
                codeBlock.nextControlFlow("else " + TYPE_CONDITION, typeElement);
            }

            // TODO find static methods with param Gson and return type TypeAdapter
            //typeElement.getClass().getMe

            codeBlock.addStatement("return (TypeAdapter<$L>) new $T.methodName(gson)", generic, typeElement);
        }
        if(!first){
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

        JavaFile javaFile = JavaFile.builder(filePackage + "", typeSpecBuilder.build())
                .addFileComment("Auto-generated do not modify !")
                .build();


        //javaFile.writeTo(System.out); // TODO change destination
        javaFile.writeTo(new File("."));
    }
}
