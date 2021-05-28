package com.broheim.websocket.core.endpoint;

import com.broheim.websocket.core.handler.CallableHandler;
import com.broheim.websocket.core.handler.DefaultCallableHandler;
import com.broheim.websocket.core.handler.DefaultRunnableHandler;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.handler.RunnableHandler;
import com.broheim.websocket.core.listener.AsyncMessageSendListener;
import com.broheim.websocket.core.listener.Listener;
import com.broheim.websocket.core.listener.RequestResponseMessageSendListener;
import com.broheim.websocket.core.listener.SyncMessageSendListener;
import com.broheim.websocket.core.publisher.DefaultEventPublisher;
import com.broheim.websocket.core.publisher.EventPublisher;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class EndpointConfig {

    protected List<Listener> listeners;
    protected EventPublisher eventPublisher;
    protected List<Handler> handlers;

    protected void defaultConfig() {
        if (null == this.handlers) {
            this.handlers = new ArrayList<>();
            this.handlers.add(new DefaultCallableHandler());
            this.handlers.add(new DefaultRunnableHandler());
        }

        for (Listener listener : this.listeners) {
            if (listener instanceof SyncMessageSendListener) {
                for (Handler handler : this.handlers) {
                    if (handler instanceof RunnableHandler) {
                        ((SyncMessageSendListener) listener).setRunnableHandler((RunnableHandler) handler);
                    }
                }
                continue;
            }
            if (listener instanceof AsyncMessageSendListener) {
                for (Handler handler : this.handlers) {
                    if (handler instanceof RunnableHandler) {
                        ((AsyncMessageSendListener) listener).setRunnableHandler((RunnableHandler) handler);
                    }
                }
                continue;
            }
            if (listener instanceof RequestResponseMessageSendListener) {
                for (Handler handler : this.handlers) {
                    if (handler instanceof CallableHandler) {
                        ((RequestResponseMessageSendListener) listener).setCallableHandler((CallableHandler) handler);
                    }
                }
                continue;
            }
        }

        if (null == this.eventPublisher) {
            this.eventPublisher = new DefaultEventPublisher(this.listeners);
        }
    }
}
