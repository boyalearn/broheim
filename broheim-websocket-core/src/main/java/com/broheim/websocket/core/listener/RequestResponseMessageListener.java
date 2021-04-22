package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.context.MessageHolder;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.event.RequestResponseMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.handler.AsyncHandler;
import com.broheim.websocket.core.handler.Handler;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;

@Slf4j
public class RequestResponseMessageListener extends AsyncMessageSendListener implements EventListener<Event> {
    @Override
    public void onEvent(Event event) {
        Session session = event.getChannelContext().getEndpoint().getSession();
        if (event instanceof RequestResponseMessageEvent) {
            onEvent(event, event.getChannelContext(), session, ((RequestResponseMessageEvent) event).getMessage(),
                    ((RequestResponseMessageEvent) event).getTimeOut(), Protocol.REQ_RESP);
            return;
        }
        if (event instanceof OnMessageEvent) {
            this.onMessage(event.getChannelContext(), session, Protocol.REQ_RESP);
            return;
        }
    }

    @Override
    protected void onMessage(ChannelContext channelContext, Session session, String protocolType) {
        String message = channelContext.getMessage();
        SimpleMessage acceptMessage;
        try {
            acceptMessage = (SimpleMessage) channelContext.getProtocol().decode(message);
        } catch (MessageProtocolException e) {
            log.error("parse protocol error", e);
            return;
        }

        if (protocolType.equals(acceptMessage.getCmd()) && acceptMessage.getSerialNo() > 0) {
            String responseContext = doLogic(channelContext, acceptMessage);
            try {
                channelContext.sendText(((SimpleProtocol) channelContext.getProtocol()).encode(channelContext,
                        responseContext, acceptMessage.getSerialNo(), Protocol.ACK));
            } catch (JsonProcessingException e) {
                log.error("auto response json processing exception error", e);
            } catch (IOException e) {
                log.error("auto response io exception error", e);
            } catch (MessageProtocolException e) {
                log.error("auto response message protocol exception error", e);
            }
            return;
        }

        if (null != acceptMessage.getSerialNo() && Protocol.ACK.equals(acceptMessage.getCmd())
                && !Protocol.ASYNC.equals(acceptMessage.getBody())) {
            synchronized (session) {
                MessageHolder.putObject(session, acceptMessage.getSerialNo(), acceptMessage.getBody());
                session.notifyAll();
            }
        }
    }

    private String doLogic(ChannelContext channelContext, SimpleMessage simpleMessage) {
        List<Handler> handlerList = channelContext.getEndpoint().getEventPublisher().getHandlerList();
        for (Handler handler : handlerList) {
            if (handler instanceof AsyncHandler) {
                return ((AsyncHandler) handler).handle(simpleMessage.getBody());
            }
        }
        return "";
    }
}
