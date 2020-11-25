package cn.mama.com;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * LinkPath注解处理器
 * Created by xiej on 2020/11/18
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"cn.mama.com.LinkPath"})
public class PathLinkProcessor extends AbstractProcessor {

    public Messager mMessager;
    public Elements mElements;
    public Filer mFiler;

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mFiler = processingEnv.getFiler();//文件相关的辅助类
        mElements = processingEnv.getElementUtils();//元素相关的辅助类
        mMessager = processingEnv.getMessager();//日志相关的辅助类

        if (set == null || set.isEmpty()) {
            //生成文件无二次注解，无需执行多次
            return false;
        }

//        mMessager.printMessage(Diagnostic.Kind.NOTE, "-------------------->执行+1");

        ParameterizedTypeName returnMapType = ParameterizedTypeName.get(
            ClassName.get(HashMap.class), ClassName.get(String.class),
            ClassName.get(PathMeta.class));

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("initRouter")
            .addModifiers(Modifier.PUBLIC)
            .returns(returnMapType)
            .addStatement("HashMap<String, PathMeta> map = new HashMap<>()");

        methodSpecBuilder.addStatement("PathMeta pathMeta");

        List<String> methodNames = new ArrayList<>();

        for (Element element : roundEnvironment.getElementsAnnotatedWith(LinkPath.class)) {

            String methodName = element.getSimpleName().toString();
            String className = element.getEnclosingElement().toString();

            StringBuilder sb = new StringBuilder();
            ExecutableElement executableElement = (ExecutableElement) element;
            List<? extends VariableElement> variableElement = executableElement.getParameters();
            for (int i = 0; i < variableElement.size(); i++) {
//                mMessager.printMessage(Kind.NOTE,
//                    "方法" + executableElement.getSimpleName() + "->参数" + variableElement.get(i)
//                        .getSimpleName());
                sb.append(variableElement.get(i));
                sb.append(",");
            }

            methodSpecBuilder.addStatement("pathMeta = new PathMeta()");
            methodSpecBuilder.addStatement("pathMeta.setPath($S)", className);
            methodSpecBuilder.addStatement("pathMeta.setParameter($S)", sb.toString());
            methodSpecBuilder.addStatement("pathMeta.setMethodName($S)", methodName);//保存原方法名

            LinkPath annotation = element.getAnnotation(LinkPath.class);
            if (!annotation.alias().isEmpty()) {
                methodName = annotation.alias();
            }

            //唯一性校验
            if (methodNames.contains(methodName)) {
                mMessager.printMessage(Diagnostic.Kind.WARNING,
                    String.format(
                        "-------------------项目中存在多个%s()方法，构建PathLink失败-------------------",
                        methodName));

                return true;
            }

            methodNames.add(methodName);
            //生成路由表key=别名，value=PathMeta()
            methodSpecBuilder.addStatement(
                "map.put($S, pathMeta)",
                methodName);
        }
        methodSpecBuilder.addStatement("return map");
        MethodSpec methodSpec = methodSpecBuilder.build();

        TypeSpec typeSpec = TypeSpec.classBuilder("PathLinkUtils")
            .addModifiers(Modifier.PUBLIC)
            .addMethod(methodSpec)
            .build();

        try {
            JavaFile javaFile = JavaFile.builder("com.xj.pathlink", typeSpec)
                .addFileComment(" This codes are generated automatically. Do not modify!")
                .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //true表示注解已经被当前处理器处理，不需要其他处理器再次处理
        return true;
    }
}
