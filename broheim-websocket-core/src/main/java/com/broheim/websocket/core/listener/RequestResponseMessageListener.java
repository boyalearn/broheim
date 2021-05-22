package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.event.send.RequestResponseMessageEvent;
import com.broheim.websocket.core.handler.CallableHandler;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Setter
public class RequestResponseMessageListener extends SendMessageListener implements Listener {

    private static final String SEND = "req-resp";

    private static final String ACK = "ack-req-resp";

    private SimpleProtocol simpleProtocol;

    private volatile Map<ChannelContext, MessageMetaInfo> messageMetaInfoContext = new ConcurrentHashMap<>();

    private CallableHandler callableHandler;

    public RequestResponseMessageListener() {
        this.simpleProtocol = new SimpleProtocol();
    }

    public RequestResponseMessageListener(SimpleProtocol simpleProtocol) {
        this.simpleProtocol = simpleProtocol;
    }

    @Override
    public void onEvent(Event event) throws Exception {
        if (event instanceof RequestResponseMessageEvent) {
            RequestResponseMessageEvent requestResponseMessageEvent = (RequestResponseMessageEvent) event;
            DefaultChannelContext channelContext = (DefaultChannelContext) requestResponseMessageEvent.getChannelContext();

            String message = requestResponseMessageEvent.getMessage();
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setType(SEND);
            simpleMessage.setBody(message);
            simpleMessage.setSerialNo(getSendSerialNo(channelContext));
            try {
                channelContext.sendText(simpleProtocol.encode(simpleMessage));
            } catch (Exception e) {
                log.error("send message error", e);
                throw new Exception(e);
            }
            MessageMetaInfo messageMetaInfo = getMessageMetaInfo(channelContext);
            synchronized (messageMetaInfo) {
                Long timeOut = requestResponseMessageEvent.getTimeOut();
                if (null == timeOut) {
                    timeOut = 60 * 1000L;
                }
                Long startTime = System.currentTimeMillis();
                Object acceptMessage = messageMetaInfo.getMessageBuffer().get(simpleMessage.getSerialNo());
                while (null == acceptMessage && timeOut > 0) {
                    messageMetaInfo.wait(timeOut);
                    acceptMessage = messageMetaInfo.getMessageBuffer().get(simpleMessage.getSerialNo());
                    timeOut = startTime + timeOut - System.currentTimeMillis();
                }
                if (null == acceptMessage) {
                    requestResponseMessageEvent.setException(new TimeoutException());
                }
                requestResponseMessageEvent.setResult(acceptMessage);
            }
        }

        if (event instanceof OnMessageEvent) {
            doAccept(simpleProtocol, (OnMessageEvent) event, callableHandler);
        }
    }

    protected void doAccept(SimpleProtocol simpleProtocol, OnMessageEvent onMessageEvent, CallableHandler callableHandler) throws Exception {
        SimpleMessage acceptMessage = simpleProtocol.decode(onMessageEvent.getMessage());

        if (ACK.equals(acceptMessage.getType())) {
            MessageMetaInfo messageMetaInfo = getMessageMetaInfo(onMessageEvent.getChannelContext());
            synchronized (messageMetaInfo) {
                messageMetaInfo.getMessageBuffer().put(acceptMessage.getSerialNo(), acceptMessage.getBody());
                messageMetaInfo.notifyAll();
            }
        }
        if (SEND.equals(acceptMessage.getType())) {
            DefaultChannelContext channelContext = (DefaultChannelContext) onMessageEvent.getChannelContext();
            if (null != callableHandler) {
                Object response = callableHandler.handle(channelContext, acceptMessage.getBody());
                SimpleMessage simpleMessage = new SimpleMessage();
                simpleMessage.setType(ACK);
                simpleMessage.setSerialNo(acceptMessage.getSerialNo());
                simpleMessage.setBody(String.valueOf(response));
                if (null != response) {
                    channelContext.sendText(simpleProtocol.encode(simpleMessage));
                }
            }
        }
    }
}
