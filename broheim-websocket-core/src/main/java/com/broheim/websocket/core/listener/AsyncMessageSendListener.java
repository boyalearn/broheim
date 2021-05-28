package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.event.send.SendAsyncMessageEvent;
import com.broheim.websocket.core.handler.RunnableHandler;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import com.broheim.websocket.core.util.StringUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Setter
public class AsyncMessageSendListener implements Listener {

    private final static String SEND = "async";
    private SimpleProtocol simpleProtocol;
    private RunnableHandler runnableHandler;

    public AsyncMessageSendListener() {
        this.simpleProtocol = new SimpleProtocol();
    }

    public AsyncMessageSendListener(SimpleProtocol simpleProtocol) {
        this.simpleProtocol = simpleProtocol;
    }


    @Override
    public void onEvent(Event event) throws Exception {
        if (event instanceof SendAsyncMessageEvent) {
            SendAsyncMessageEvent sendAsyncMessageEvent = (SendAsyncMessageEvent) event;
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setType(SEND);
            simpleMessage.setBody(sendAsyncMessageEvent.getMessage());

            String sendMessage = simpleProtocol.encode(simpleMessage);
            sendAsyncMessageEvent.getChannelContext().sendText(sendMessage);
        }
        if (event instanceof OnMessageEvent) {
            OnMessageEvent onMessageEvent = (OnMessageEvent) event;
            String message = onMessageEvent.getMessage();
            SimpleMessage simpleMessage = simpleProtocol.decode(message);
            if (SEND.equals(simpleMessage.getType()) && StringUtil.isNotEmpty(simpleMessage.getBody())) {
                ChannelContext channelContext = onMessageEvent.getChannelContext();
                runnableHandler.handle(channelContext, simpleMessage.getBody());
            }
        }
    }

}
