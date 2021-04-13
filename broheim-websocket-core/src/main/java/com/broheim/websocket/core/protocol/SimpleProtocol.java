package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SimpleProtocol implements Protocol<SimpleMessage> {

    private static final String PING = "ping";

    private static final String OK = "ok";

    private ObjectMapper objectMapper = new ObjectMapper();

    private static ConcurrentHashMap<Session, CopyOnWriteArraySet<Integer>> messageIdSet = new ConcurrentHashMap<>();


    @Override
    public SimpleMessage encode(String appMessage) throws MessageProtocolException {
        SimpleMessage message = new SimpleMessage();
        message.setBody(appMessage);
        return message;
    }

    @Override
    public String addProtocolHeader(String appMessage, int sendId) throws MessageProtocolException {
        SimpleMessage message = new SimpleMessage();
        message.setBody(appMessage);
        message.setSerialNo(sendId);
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }

    @Override
    public SimpleMessage decode(String message) throws MessageProtocolException {
        try {
            return objectMapper.readValue(message, SimpleMessage.class);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }

    @Override
    public String doProtocol(ChannelContext channelContext, SimpleMessage message) {

        //如果应答的是OK 需要通知同步等待的Send线程
        if (OK.equals(message.getCmd())) {
            Session session = channelContext.getEndpoint().getSession();
            synchronized (session) {
                CopyOnWriteArraySet<Integer> idSet = messageIdSet.get(session);
                if (null == idSet) {
                    messageIdSet.put(session, new CopyOnWriteArraySet<Integer>());
                }
                idSet = messageIdSet.get(session);
                idSet.add(message.getSerialNo());
                session.notifyAll();
            }
            return null;
        }

        //自动应答表示已经收到消息
        SimpleMessage simpleMessage = new SimpleMessage();
        simpleMessage.setCmd(OK);
        simpleMessage.setBody(null);
        simpleMessage.setSerialNo(message.getSerialNo());
        channelContext.sendMessage(simpleMessage);

        if (PING.equals(message.getCmd())) {
            return null;
        }
        if (StringUtil.isEmpty(message.getBody())) {
            return "";
        }
        return message.getBody();
    }

    @Override
    public void wait(int sendId, Session session) throws InterruptedException {
        session.wait();
        while (!messageIdSet.get(session).contains(sendId)) {
            session.wait();
        }
        messageIdSet.get(session).remove(sendId);
    }

    @Override
    public void wait(int sendId, Session session, long timeOut) throws InterruptedException {
        long time = System.currentTimeMillis();
        session.wait(timeOut);
        while (!messageIdSet.get(session).contains(sendId) && System.currentTimeMillis() - time < timeOut) {
            session.wait(timeOut + time - System.currentTimeMillis());
        }
        messageIdSet.get(session).remove(sendId);
    }
}
