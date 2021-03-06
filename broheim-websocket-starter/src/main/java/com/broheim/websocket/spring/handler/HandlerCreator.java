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

    public Class<HandlerInvoker> modifyHandlerInvoker(String path) throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        ClassLoader classLoader = this.getClass().getClassLoader();
        LoaderClassPath classLoaderPath=new LoaderClassPath(classLoader);
        classPool.appendClassPath(classLoaderPath);

        CtClass ctClass = classPool.makeClass("com.framework.websocket.spring.handler.CommandHandlerInvoker");
        CtClass superClass = classPool.getCtClass(HandlerInvoker.class.getName());
        ctClass.setSuperclass(superClass);
        ConstPool constpool = ctClass.getClassFile().getConstPool();
        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation serverEndpoint = new Annotation(SocketEndpointPath.class.getName(), constpool);
        serverEndpoint.addMemberValue("value", new StringMemberValue(path, constpool));
        annotationsAttribute.addAnnotation(serverEndpoint);
        ctClass.getClassFile().addAttribute(annotationsAttribute);
        return (Class<HandlerInvoker>) ctClass.toClass();
    }
}
