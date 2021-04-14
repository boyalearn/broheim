package com.broheim.websocket.spring.config;


import com.broheim.websocket.core.config.ServerConfig;
import com.broheim.websocket.core.context.PublisherHolder;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.reactor.Reactor;
import com.broheim.websocket.spring.annonation.Command;
import com.broheim.websocket.spring.annonation.WebSocketController;
import com.broheim.websocket.spring.handler.HandlerCreator;
import com.broheim.websocket.spring.handler.HandlerInvoker;
import com.broheim.websocket.spring.reactor.CommandReactor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class WebSocketConfigRegisterBean implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;


    private HandlerCreator handlerCreator = new HandlerCreator();


    @Override
    public void afterPropertiesSet() throws Exception {

        String[] webSocketControllerNames = applicationContext.getBeanNamesForAnnotation(WebSocketController.class);

        List<Handler> handlerList = new ArrayList<>();

        for (String webSocketControllerName : webSocketControllerNames) {
            Object bean = applicationContext.getBean(webSocketControllerName);
            WebSocketController webSocketController = bean.getClass().getAnnotation(WebSocketController.class);
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Command command = method.getAnnotation(Command.class);
                if (null != command) {
                    Class<HandlerInvoker> handlerInvokerClass = handlerCreator.modifyHandlerInvoker(webSocketController.value());
                    HandlerInvoker handlerInvoker = handlerInvokerClass.newInstance();
                    handlerInvoker.setObject(bean);
                    handlerInvoker.setMethod(method);
                    handlerInvoker.setArgTypes(method.getParameterTypes());
                    handlerInvoker.setCmd(command.value());
                    handlerList.add(handlerInvoker);
                }
            }
        }

        Reactor reactor = applicationContext.getBean(Reactor.class);
        if (null == reactor) {
            reactor = new CommandReactor();
        }
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.addReactor(reactor);
        serverConfig.addHandlers(handlerList);
        PublisherHolder.setServerConfig(serverConfig);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
