package com.broheim.websocket.spring.handler;

import com.broheim.websocket.core.annonation.SocketEndpointPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class HandlerCreator {

    public Class<?> modifyHandlerInvoker(String path, String className,Class<?> supperClazz) throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        ClassLoader classLoader = this.getClass().getClassLoader();
        LoaderClassPath classLoaderPath = new LoaderClassPath(classLoader);
        classPool.appendClassPath(classLoaderPath);

        CtClass ctClass = classPool.makeClass(className);
        CtClass superClass = classPool.getCtClass(supperClazz.getName());
        ctClass.setSuperclass(superClass);
        ConstPool constpool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation serverEndpoint = new Annotation(SocketEndpointPath.class.getName(), constpool);
        serverEndpoint.addMemberValue("value", new StringMemberValue(path, constpool));
        annotationsAttribute.addAnnotation(serverEndpoint);
        ctClass.getClassFile().addAttribute(annotationsAttribute);
        return ctClass.toClass();
    }
}
