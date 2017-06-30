package com.example;

import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import sun.reflect.generics.scope.MethodScope;
import sun.security.jgss.spi.MechanismFactory;

@SupportedAnnotationTypes("com.example.BindView")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//@AutoService(Processor.class)
//AutoService自动生成文件(in processor.jar): META-INF/services/javax.annotation.processing.Processor
public class ButterKnifeProcessor extends AbstractProcessor {
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> types = new LinkedHashSet<>();
//        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
//            types.add(annotation.getCanonicalName());
//        }
//        return types;
//    }
//
//    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
//        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
//        annotations.add(BindView.class);
//        return annotations;
//    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("=======================process()");
        parseRoundEnvironment(roundEnv);
        return true;
    }


    private void printSet(Set<? extends TypeElement> annotations) {
        for (TypeElement element : annotations) {
            printValue(element.getSimpleName());
        }
    }

    /**
     * just test
     */
    private void printElement(Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        printValue("========annotation 所在的类完整名称 " + enclosingElement.getQualifiedName());
        printValue("========annotation 所在类的类名 " + enclosingElement.getSimpleName());
        printValue("========annotation 所在类的父类 " + enclosingElement.getSuperclass());
        printValue("========element name " + element.getSimpleName());
        printValue("========element type " + element.asType());
        printValue("========annotation value " + element.getAnnotation(BindView.class).value());
        printValue("\n");
    }


    private static void printValue(Object obj) {
        System.out.println(obj);
    }


    private void parseRoundEnvironment(RoundEnvironment roundEnv) {

        Map<TypeElement, BindClass> map = new LinkedHashMap<>();
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            //所在类名 String className = enclosingElement.getSimpleName().toString();
            //所在类全名 String qualifiedName = enclosingElement.getQualifiedName().toString();
            //注解的值
            int annotationValue = element.getAnnotation(BindView.class).value();

            BindClass bindClass = map.get(enclosingElement);
            if (bindClass == null) {
                bindClass = BindClass.createBindClass(enclosingElement);
                map.put(enclosingElement, bindClass);
            }
            String name = element.getSimpleName().toString();
            TypeName type = TypeName.get(element.asType());
            ViewBinding viewBinding = ViewBinding.createViewBind(name, type, annotationValue);
            bindClass.addAnnotationField(viewBinding);

            //printElement(element);
        }


        for (Map.Entry<TypeElement, BindClass> entry : map.entrySet()) {
            printValue("==========" + entry.getValue().getBindingClassName());
            try {
                entry.getValue().preJavaFile().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
