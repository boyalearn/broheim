package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class AsyncMessageSendListener implements EventListener<Event> {

    private static ConcurrentHashMap<Session, CopyOnWriteArraySet<Integer>> messageIdSet = new ConcurrentHashMap<>();

    @Override
    public void onEvent(Event event) {
        ChannelContext channelContext = event.getChannelContext();
        Session session = channelContext.getEndpoint().getSession();
        if (event instanceof SendAsyncMessageEvent) {
            SendAsyncMessageEvent asyncMessageEvent = (SendAsyncMessageEvent) event;
            String sendMessage = asyncMessageEvent.getMessage();
            try {
                Integer serialNo = ((SimpleMessage) (channelContext.getProtocol().decode(sendMessage))).getSerialNo();
                synchronized (session) {

                    session.getBasicRemote().sendText(sendMessage);
                    session.wait();
                    while (!messageIdSet.contains(serialNo)) {
                        session.wait();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Send Error");
            }
            return;
        }
        if (event instanceof OnMessageEvent) {
            String message = channelContext.getMessage();
            SimpleMessage messageObj;
            try {
                messageObj = (SimpleMessage) channelContext.getProtocol().decode(message);
            } catch (MessageProtocolException e) {
                log.error("parse protocol error", e);
                return;
            }
            synchronized (session) {
                CopyOnWriteArraySet<Integer> idSet = messageIdSet.get(session);
                if (null == idSet) {
                    messageIdSet.put(session, new CopyOnWriteArraySet());
                }
                idSet = messageIdSet.get(session);
                idSet.add(messageObj.getSerialNo());
                session.notifyAll();
            }
            return;
        }
    }
}
