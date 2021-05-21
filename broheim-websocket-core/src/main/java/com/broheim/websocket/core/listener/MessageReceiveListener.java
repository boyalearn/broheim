package com.broheim.websocket.core.listener;


import com.broheim.websocket.core.handler.DefaultHandler;
import com.broheim.websocket.core.handler.RunnableHandler;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import com.broheim.websocket.core.util.StringUtil;

public class MessageReceiveListener implements EventListener<com.broheim.websocket.core.event.OnMessageEvent> {

    private SimpleProtocol simpleProtocol;

    private RunnableHandler handler;

    public MessageReceiveListener() {
        this.simpleProtocol = new SimpleProtocol();
        this.handler = new DefaultHandler();
    }
    public MessageReceiveListener(SimpleProtocol simpleProtocol, RunnableHandler handler) {
        this.simpleProtocol = simpleProtocol;
        this.handler = handler;
    }

    @Override
    public Object onEvent(com.broheim.websocket.core.event.OnMessageEvent event) throws Exception {
        String message = event.getMessage();
        SimpleMessage simpleMessage = simpleProtocol.decode(message);
        if (StringUtil.isNotEmpty(simpleMessage.getBody())) {
            handler.handle(event.getChannelContext(), simpleMessage.getBody());
        }
        return null;
    }

}
