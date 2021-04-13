package com.broheim.websocket.spring.handler;

import com.broheim.websocket.core.context.ChannelContext;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Setter
public class HandlerInvoker implements CommandHandler {

    private Object object;

    private Method method;

    private Class<?>[] argTypes;

    private String cmd;

    public HandlerInvoker() {

    }

    public HandlerInvoker(Object object, Method method, Class<?>[] argTypes, String cmd) {
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
    public void handle(ChannelContext channelContext, String body) {
        try {
            Object[] args = new Object[this.argTypes.length];
            for (int i = 0; i < this.argTypes.length; i++) {
                if (this.argTypes[i].isAssignableFrom(channelContext.getClass())) {
                    args[i] = channelContext;
                } else if (String.class.isAssignableFrom(this.argTypes[i])) {
                    args[i] = body;
                } else {
                    args[i] = null;
                }
            }
            method.invoke(object, args);
        } catch (IllegalAccessException e) {
            log.error(method.getName() + " invoke error", e);
        } catch (InvocationTargetException e) {
            log.error(method.getName() + " invoke error", e);
        }
    }
}
