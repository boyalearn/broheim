package com.broheim.websocket.spring.exporter;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Map;

public class WebSocketEndpointExporter implements SmartInitializingSingleton, ServletContextAware, ApplicationContextAware {

    private static final String SERVER_CONTAINER_CLASS_NAME = "javax.websocket.server.ServerContainer";

    @Nullable
    private ServerContainer serverContainer;


    private ServletContext servletContext;

    private ApplicationContext applicationContext;


    @Override
    public void afterSingletonsInstantiated() {

        serverContainer = (ServerContainer) servletContext.getAttribute(SERVER_CONTAINER_CLASS_NAME);
        try {
            registerEndpoint();
        } catch (DeploymentException e) {
            e.printStackTrace();
        }
    }

    private void registerEndpoint() throws DeploymentException {

        if (applicationContext != null) {
            String[] endpointBeanNames = applicationContext.getBeanNamesForAnnotation(ServerEndpoint.class);
            for (String beanName : endpointBeanNames) {
                serverContainer.addEndpoint(applicationContext.getType(beanName));
            }
        }


        if (applicationContext != null) {
            Map<String, ServerEndpointConfig> endpointConfigMap = applicationContext.getBeansOfType(ServerEndpointConfig.class);
            for (ServerEndpointConfig endpointConfig : endpointConfigMap.values()) {
                serverContainer.addEndpoint(endpointConfig);
            }
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
