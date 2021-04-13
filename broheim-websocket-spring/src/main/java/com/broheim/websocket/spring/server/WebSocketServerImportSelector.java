package com.broheim.websocket.spring.server;

import com.framework.websocket.core.exporter.WebSocketEndpointExporter;
import com.framework.websocket.spring.config.WebSocketConfigRegisterBean;
import com.framework.websocket.spring.processor.EndpointDefinitionRegistryProcessor;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;


public class WebSocketServerImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{
                EndpointDefinitionRegistryProcessor.class.getName(),
                WebSocketEndpointExporter.class.getName(),
                WebSocketConfigRegisterBean.class.getName()
        };
    }
}
