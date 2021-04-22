package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.context.MessageHolder;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.event.RequestResponseMessageEvent;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;

@Slf4j
public class AsyncMessageSendListener implements EventListener<Event> {

    @Override
    public void onEvent(Event event) {
        ChannelContext channelContext = event.getChannelContext();
        Session session = channelContext.getEndpoint().getSession();
        if (event instanceof SendAsyncMessageEvent) {
            onEvent(event, channelContext, session, ((SendAsyncMessageEvent) event).getMessage(), ((SendAsyncMessageEvent) event).getTimeOut(), Protocol.ASYNC);
            return;
        }
        if (event instanceof OnMessageEvent) {
            onMessage(channelContext, session, Protocol.ASYNC);
            return;
        }
    }

    protected void onEvent(Event event, ChannelContext channelContext, Session session, String message, Long timeOut, String protocolType) {

        Integer serialNo = channelContext.getEndpoint().sendId().getAndIncrement();
        synchronized (session) {
            try {
                String encodeMessage = ((SimpleProtocol) (channelContext.getProtocol())).encode(channelContext, message, serialNo, protocolType);
                MessageHolder.putObject(session, serialNo, -1);
                System.out.println("response :"+encodeMessage);
                session.getBasicRemote().sendText(encodeMessage);
                //没有设置超时时间的同步发送默认设置3分钟也不可能无限制等待。
                if (null == timeOut) {
                    timeOut = 3 * 60 * 1000L;
                }
                Long startTime = System.currentTimeMillis();
                session.wait(timeOut);
                Long leaveTimeOut = timeOut + startTime - System.currentTimeMillis();
                Object result = null;
                while (leaveTimeOut > 0 && new Integer(-1).equals(result = MessageHolder.getObject(session, serialNo))) {
                    session.wait(leaveTimeOut);
                    leaveTimeOut = timeOut + startTime - System.currentTimeMillis();
                }
                if (event instanceof RequestResponseMessageEvent) {
                    ((RequestResponseMessageEvent) event).setResult(result);
                }
                if (leaveTimeOut < 0) {
                    throw new RuntimeException("send time out");
                }
            } catch (Exception e) {
                throw new RuntimeException("Send Error");
            } finally {
                MessageHolder.removeObject(session, serialNo);
            }
        }
        return;
    }

    protected void onMessage(ChannelContext channelContext, Session session, String protocolType) {
        String message = channelContext.getMessage();
        SimpleMessage responseMessage;
        try {
            responseMessage = (SimpleMessage) channelContext.getProtocol().decode(message);
        } catch (MessageProtocolException e) {
            log.error("parse protocol error", e);
            return;
        }

        if (protocolType.equals(responseMessage.getCmd()) && responseMessage.getSerialNo() > 0) {
            try {
                channelContext.sendText(((SimpleProtocol) channelContext.getProtocol()).encode(channelContext, responseMessage.getCmd(), responseMessage.getSerialNo(), Protocol.ACK));
            } catch (JsonProcessingException e) {
                log.error("auto response json processing exception error", e);
            } catch (IOException e) {
                log.error("auto response io exception error", e);
            } catch (MessageProtocolException e) {
                log.error("auto response message protocol exception error", e);
            }
        }

        if (null != responseMessage.getSerialNo() && Protocol.ACK.equals(responseMessage.getCmd())
                && protocolType.equals(responseMessage.getBody())) {
            synchronized (session) {
                MessageHolder.putObject(session, responseMessage.getSerialNo(), responseMessage.getBody());
                session.notifyAll();
            }
            return;
        }
        return;
    }

}
