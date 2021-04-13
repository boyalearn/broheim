package com.broheim.websocket.core.endpoint;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

public class EndpointCreator {

    public Class<AbstractWebSocketServerEndpoint> createEndpoint(String path) throws NotFoundException, CannotCompileException {

        ClassPool cp = ClassPool.getDefault();
        ClassLoader classLoader = this.getClass().getClassLoader();
        LoaderClassPath classLoaderPath=new LoaderClassPath(classLoader);
        cp.appendClassPath(classLoaderPath);
        CtClass ctClass = cp.makeClass("com.framework.websocket.core.endpoint.Dynamic"+path.replaceAll("/","")+"WebSocketServerEndpoint");
        CtClass superClass = cp.getCtClass(AbstractWebSocketServerEndpoint.class.getName());
        ctClass.setSuperclass(superClass);

        ClassFile ctFile = ctClass.getClassFile();
        ConstPool constpool = ctFile.getConstPool();

        AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation serverEndpoint = new Annotation("javax.websocket.server.ServerEndpoint", constpool);
        serverEndpoint.addMemberValue("value", new StringMemberValue(path, constpool));
        Annotation component = new Annotation("org.springframework.stereotype.Component", constpool);
        annotationsAttribute.addAnnotation(serverEndpoint);
        annotationsAttribute.addAnnotation(component);
        ctFile.addAttribute(annotationsAttribute);

        return (Class<AbstractWebSocketServerEndpoint>) ctClass.toClass();
    }

}
