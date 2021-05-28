package com.broheim.websocket.spring.config;


import com.broheim.websocket.core.endpoint.server.WebSocketServer;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.spring.annonation.Command;
import com.broheim.websocket.spring.annonation.WebSocketController;
import com.broheim.websocket.spring.handler.CommandCallableHandler;
import com.broheim.websocket.spring.handler.CommandRunnableHandler;
import com.broheim.websocket.spring.handler.HandlerCallableInvoker;
import com.broheim.websocket.spring.handler.HandlerCreator;
import com.broheim.websocket.spring.handler.HandlerRunnableInvoker;
import com.broheim.websocket.spring.reactor.CommandCallableReactor;
import com.broheim.websocket.spring.reactor.CommandRunnableReactor;
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
        List<CommandRunnableHandler> commandRunnableHandlers = new ArrayList<>();
        List<CommandCallableHandler> commandCallableHandlers = new ArrayList<>();

        for (String webSocketControllerName : webSocketControllerNames) {
            Object bean = applicationContext.getBean(webSocketControllerName);
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Command command = method.getAnnotation(Command.class);
                if (null != command) {
                    if (void.class == method.getReturnType()) {
                        HandlerRunnableInvoker handlerInvoker = HandlerRunnableInvoker.class.newInstance();
                        handlerInvoker.setObject(bean);
                        handlerInvoker.setMethod(method);
                        handlerInvoker.setArgTypes(method.getParameterTypes());
                        handlerInvoker.setCmd(command.value());
                        commandRunnableHandlers.add(handlerInvoker);
                    } else {
                        HandlerCallableInvoker handlerInvoker = HandlerCallableInvoker.class.newInstance();
                        handlerInvoker.setObject(bean);
                        handlerInvoker.setMethod(method);
                        handlerInvoker.setArgTypes(method.getParameterTypes());
                        handlerInvoker.setCmd(command.value());
                        commandCallableHandlers.add(handlerInvoker);
                    }
                }
            }
        }
        CommandCallableReactor commandCallableReactor = new CommandCallableReactor();
        commandCallableReactor.setCommandCallableHandlers(commandCallableHandlers);
        CommandRunnableReactor commandRunnableReactor = new CommandRunnableReactor();
        commandRunnableReactor.setCommandRunnableHandlers(commandRunnableHandlers);
        WebSocketServer server = new WebSocketServer();
        List<Handler> handlers = new ArrayList<>();
        handlers.add(commandCallableReactor);
        handlers.add(commandRunnableReactor);
        server.setHandlers(handlers);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
