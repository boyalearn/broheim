package com.broheim.websocket.core.endpoint.context;


import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.handler.RunnableHandler;
import com.broheim.websocket.core.listener.EventListener;
import com.broheim.websocket.core.listener.MessageReceiveListener;
import com.broheim.websocket.core.listener.ServerHeartbeatListener;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.publisher.DefaultEventPublisher;
import com.broheim.websocket.core.publisher.EventPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PublisherHolder {

/*
    private static ServerConfig SERVER_CONFIG;*/

    private static final Map<Class<?>, EventPublisher> PUBLISHER_CENTER = new ConcurrentHashMap<>();

    public static EventPublisher findEventPublisher(Class<?> clazz) {
/*        if (null == SERVER_CONFIG) {
            SERVER_CONFIG = new ServerConfig();
        }
        EventPublisher publisher = PUBLISHER_CENTER.get(clazz);
        if (null == publisher) {
            synchronized (PUBLISHER_CENTER) {
                if (null == PUBLISHER_CENTER.get(clazz)) {
                    EventPublisher eventPublisher = SERVER_CONFIG.buildPublisherForEndpoint(clazz);
                    PUBLISHER_CENTER.put(clazz, eventPublisher);
                    return eventPublisher;
                }
                return PUBLISHER_CENTER.get(clazz);

        }
        return publisher;*/
        List<EventListener> listenerList = new ArrayList<EventListener>();
        listenerList.add(new MessageReceiveListener(new SimpleProtocol(), new RunnableHandler() {
            @Override
            public void handle(ChannelContext channelContext, String message) {
                System.out.println(message);
            }
        }));
        listenerList.add(new ServerHeartbeatListener(new SimpleProtocol()));
        EventPublisher eventPublisher = new DefaultEventPublisher(listenerList);
        return eventPublisher;
    }

/*    public static void setServerConfig(ServerConfig serverConfig) {
        SERVER_CONFIG = serverConfig;
    }*/
}
