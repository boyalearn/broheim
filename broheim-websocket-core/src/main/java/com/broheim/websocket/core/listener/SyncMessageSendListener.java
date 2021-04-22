package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.SendSyncMessageEvent;

import javax.websocket.Session;

public class SyncMessageSendListener implements EventListener<Event> {
    @Override
    public void onEvent(Event event) {
        ChannelContext channelContext = event.getChannelContext();
        Session session = channelContext.getEndpoint().getSession();
        if (event instanceof SendSyncMessageEvent) {
            SendSyncMessageEvent syncMessageEvent = (SendSyncMessageEvent) event;
            String message = syncMessageEvent.getMessage();
            synchronized (session) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    throw new RuntimeException("Send Error");
                }
            }
            return;
        }
    }
}
