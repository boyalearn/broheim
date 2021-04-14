package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;

public class DefaultClientEventListener implements EventListener<Event>{



    @Override
    public void onEvent(Event event) {
        if(event instanceof OnMessageEvent){

        }

    }
}
