package com.broheim.websocket.spring.processor;

import com.broheim.websocket.core.endpoint.AbstractWebSocketServerEndpoint;
import com.broheim.websocket.core.endpoint.EndpointCreator;
import com.broheim.websocket.spring.annonation.WebSocketController;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;

@Slf4j
public class EndpointDefinitionRegistryProcessor implements BeanDefinitionRegistryPostProcessor {

    private EndpointCreator endpointCreator = new EndpointCreator();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {

        String[] beanDefinitionNames = beanDefinitionRegistry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);

            if (beanDefinition instanceof RootBeanDefinition) {
                Class<?> beanClass;
                try {
                    beanClass = ((RootBeanDefinition) beanDefinition).getBeanClass();
                } catch (Exception e) {
                    continue;
                }
                WebSocketController webSocketController = beanClass.getAnnotation(WebSocketController.class);
                if (null != webSocketController) {
                    registerBeanDefinition(beanDefinitionRegistry, webSocketController.value());
                }
                continue;
            }

            if (beanDefinition instanceof ScannedGenericBeanDefinition) {
                String beanClassName = beanDefinition.getBeanClassName();
                Class<?> beanClass = null;
                try {
                    beanClass = Class.forName(beanClassName);
                } catch (ClassNotFoundException e) {
                    log.error("parse class error, the class is {}", beanClassName, e);
                    continue;
                }
                WebSocketController webSocketController = beanClass.getAnnotation(WebSocketController.class);
                if (null != webSocketController) {
                    registerBeanDefinition(beanDefinitionRegistry, webSocketController.value());
                }
                continue;
            }

        }

    }

    private void registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, String path) {
        try {
            Class<AbstractWebSocketServerEndpoint> endpoint = endpointCreator.createEndpoint(path);
            RootBeanDefinition beanDefinition = new RootBeanDefinition(endpoint);
            beanDefinitionRegistry.registerBeanDefinition(endpoint.getName(), beanDefinition);
        } catch (NotFoundException e) {
            log.error("not find class exception",e);
        } catch (CannotCompileException e) {
            log.error("cannot compile exception",e);

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        return ;
    }
}
