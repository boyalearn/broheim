package com.broheim.websocket.spring.annonation;



import com.broheim.websocket.spring.server.WebSocketServerImportSelector;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WebSocketServerImportSelector.class)
public @interface EnableWebSocketServer {
}
