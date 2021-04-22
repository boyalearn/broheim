package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.event.SendAsyncMessageEvent;
import com.broheim.websocket.core.event.SendSyncMessageEvent;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.protocol.SimpleProtocol;
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
            String message = asyncMessageEvent.getMessage();
            Integer serialNo = channelContext.getEndpoint().sendId().getAndIncrement();
            synchronized (session) {
                CopyOnWriteArraySet<Integer> sessionMessageIdSet = addSerialNoAddSendSet(session, serialNo);
                try {
                    String encodeMessage = ((SimpleProtocol) (channelContext.getProtocol())).encode(channelContext, message, serialNo, Protocol.ASYNC);
                    session.getBasicRemote().sendText(encodeMessage);
                    Long timeOut = asyncMessageEvent.getTimeOut();
                    //没有设置超时时间的同步发送默认设置3分钟也不可能无限制等待。
                    if (null == timeOut) {
                        timeOut = 3 * 60 * 1000L;
                    }
                    Long startTime = System.currentTimeMillis();
                    session.wait(timeOut);
                    Long leaveTimeOut = timeOut + startTime - System.currentTimeMillis();
                    while (leaveTimeOut > 0 && sessionMessageIdSet.contains(serialNo)) {
                        session.wait(leaveTimeOut);
                        leaveTimeOut = timeOut + startTime - System.currentTimeMillis();
                    }
                    if (leaveTimeOut < 0) {
                        throw new RuntimeException("send time out");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Send Error");
                } finally {
                    sessionMessageIdSet.remove(serialNo);
                }
            }
            return;
        }
        if (event instanceof OnMessageEvent) {
            String message = channelContext.getMessage();
            SimpleMessage responseMessage;
            try {
                responseMessage = (SimpleMessage) channelContext.getProtocol().decode(message);
            } catch (MessageProtocolException e) {
                log.error("parse protocol error", e);
                return;
            }
            if (null != responseMessage.getSerialNo() && Protocol.ACK.equals(responseMessage.getCmd())
                    && Protocol.ASYNC.equals(responseMessage.getBody())) {
                synchronized (session) {
                    removeSerialNoFromSessionMessageIdSet(session, responseMessage.getSerialNo());
                    session.notifyAll();
                }
                return;
            }
            return;
        }
    }

    private CopyOnWriteArraySet<Integer> addSerialNoAddSendSet(Session session, Integer serialNo) {
        CopyOnWriteArraySet<Integer> idSet = messageIdSet.get(session);
        if (null == idSet) {
            messageIdSet.put(session, new CopyOnWriteArraySet());
        }
        idSet = messageIdSet.get(session);
        idSet.add(serialNo);
        return idSet;
    }

    private void removeSerialNoFromSessionMessageIdSet(Session session, Integer serialNo) {
        CopyOnWriteArraySet<Integer> idSet = messageIdSet.get(session);
        if (null == idSet) {
            messageIdSet.put(session, new CopyOnWriteArraySet());
        }
        idSet = messageIdSet.get(session);
        idSet.remove(serialNo);
    }
}
