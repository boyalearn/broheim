package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.protocol.Protocol;
import lombok.Setter;

@Setter
public class DefaultClientEventListener implements EventListener<Event>{

    private Protocol protocol;



    @Override
    public void onEvent(Event event) {
        if(event instanceof OnMessageEvent){

        }

    }
}
