package com.broheim.websocket.core.listener;


import com.broheim.websocket.core.acceptor.Acceptor;
import com.broheim.websocket.core.event.MessageEvent;

public class MessageReceiveListener<Event> implements EventListener<Event> {

    private Acceptor acceptor;

    public MessageReceiveListener(Acceptor acceptor) {
        this.acceptor = acceptor;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageEvent) {
            acceptor.doAccept(((MessageEvent) event).getChannelContext());
        }
    }
}
