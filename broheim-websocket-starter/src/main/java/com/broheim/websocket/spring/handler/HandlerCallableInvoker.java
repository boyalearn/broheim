package com.broheim.websocket.spring.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Setter
public class HandlerCallableInvoker implements CommandCallableHandler {

    private Object object;

    private Method method;

    private Class<?>[] argTypes;

    private String cmd;

    public HandlerCallableInvoker() {

    }

    public HandlerCallableInvoker(Object object, Method method, Class<?>[] argTypes, String cmd) {
        this.object = object;
        this.method = method;
        this.argTypes = argTypes;
        this.cmd = cmd;
    }

    @Override
    public String getCmd() {
        return cmd;
    }

    @Override
    public Object handle(ChannelContext channelContext, String body) {
        try {
            return method.invoke(object, InvokerUtils.findArgs(this.argTypes, channelContext, body));
        } catch (IllegalAccessException e) {
            log.error(method.getName() + " invoke error", e);
        } catch (InvocationTargetException e) {
            log.error(method.getName() + " invoke error", e);
        }
        return null;
    }
}
