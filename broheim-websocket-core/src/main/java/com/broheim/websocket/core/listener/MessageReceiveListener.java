package com.broheim.websocket.core.listener;


import com.broheim.websocket.core.acceptor.Acceptor;
import com.broheim.websocket.core.event.OnMessageEvent;

public class MessageReceiveListener<Event> implements EventListener<Event> {

    private Acceptor acceptor;

    public MessageReceiveListener(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof OnMessageEvent) {
            acceptor.doAccept(((OnMessageEvent) event).getChannelContext());
        }
    }
}
