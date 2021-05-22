package com.broheim.websocket.spring.handler;

import com.broheim.websocket.core.endpoint.context.ChannelContext;

public class InvokerUtils {

    public static Object[] findArgs(Class<?>[] argTypes, ChannelContext channelContext, String body) {
        Object[] args = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            if (argTypes[i].isAssignableFrom(channelContext.getClass())) {
                args[i] = channelContext;
            } else if (String.class.isAssignableFrom(argTypes[i])) {
                args[i] = body;
            } else {
                args[i] = null;
            }
        }
        return args;
    }
}
