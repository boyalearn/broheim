package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AsyncMessageSendListener implements EventListener<Event> {

    private SimpleProtocol simpleProtocol;

    public AsyncMessageSendListener() {
        this.simpleProtocol = new SimpleProtocol();
    }

    public AsyncMessageSendListener(SimpleProtocol simpleProtocol) {
        this.simpleProtocol = simpleProtocol;
    }

    @Override
    public Object onEvent(Event event) throws Exception {
        if (event instanceof SendAsyncMessageEvent) {
            SendAsyncMessageEvent sendAsyncMessageEvent = (SendAsyncMessageEvent) event;
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setBody(sendAsyncMessageEvent.getMessage());

            String sendMessage = simpleProtocol.encode(simpleMessage);
            sendAsyncMessageEvent.getChannelContext().sendText(sendMessage);
        }
        return null;
    }

}
