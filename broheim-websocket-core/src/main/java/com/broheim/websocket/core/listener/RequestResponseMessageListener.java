package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.Event;
import com.broheim.websocket.core.event.OnMessageEvent;
import com.broheim.websocket.core.event.SendSyncMessageEvent;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RequestResponseMessageListener implements EventListener<Event> {

    private static final String SEND = "sync";

    private static final String ACK = "ack-sync";

    private SimpleProtocol simpleProtocol;

    private Map<ChannelContext, MessageMetaInfo> messageMetaInfoContext = new ConcurrentHashMap<>();


    public RequestResponseMessageListener() {
        this.simpleProtocol = new SimpleProtocol();
    }

    public RequestResponseMessageListener(SimpleProtocol simpleProtocol) {
        this.simpleProtocol = simpleProtocol;
    }

    @Override
    public Object onEvent(Event event) throws Exception {
        if (event instanceof SendSyncMessageEvent) {
            SendSyncMessageEvent sendEvent = (SendSyncMessageEvent) event;

            DefaultChannelContext channelContext = (DefaultChannelContext) sendEvent.getChannelContext();

            String message = sendEvent.getMessage();
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setType(SEND);
            simpleMessage.setBody(message);
            simpleMessage.setSerialNo(getSendSerialNo(channelContext));
            try {
                channelContext.sendText(simpleProtocol.encode(simpleMessage));
            } catch (Exception e) {
                log.error("send message error", e);
                return false;
            }
            MessageMetaInfo messageMetaInfo = getMessageMetaInfo(channelContext);
            synchronized (messageMetaInfo) {

                Object acceptMessage = messageMetaInfo.getMessageBuffer().get(simpleMessage.getSerialNo());
                while (null == acceptMessage) {
                    messageMetaInfo.wait();
                    acceptMessage = messageMetaInfo.getMessageBuffer().get(simpleMessage.getSerialNo());
                }
                return acceptMessage;

            }
        }

        if (event instanceof OnMessageEvent) {
            OnMessageEvent onMessageEvent = (OnMessageEvent) event;
            SimpleMessage acceptMessage = simpleProtocol.decode(onMessageEvent.getMessage());
            if (ACK.equals(acceptMessage.getType())) {
                DefaultChannelContext channelContext = (DefaultChannelContext) onMessageEvent.getChannelContext();
                MessageMetaInfo messageMetaInfo = getMessageMetaInfo(channelContext);
                synchronized (messageMetaInfo) {
                    messageMetaInfo.getMessageBuffer().put(acceptMessage.getSerialNo(), acceptMessage.getBody());
                    messageMetaInfo.notifyAll();
                }

            }
        }
        return null;
    }

    private MessageMetaInfo getMessageMetaInfo(ChannelContext channelContext) {
        MessageMetaInfo messageMetaInfo = messageMetaInfoContext.get(channelContext);
        if (null == messageMetaInfo) {
            synchronized (messageMetaInfoContext) {
                messageMetaInfo = messageMetaInfoContext.get(channelContext);
                if (null == messageMetaInfo) {
                    messageMetaInfo = new MessageMetaInfo();
                    messageMetaInfoContext.put(channelContext, messageMetaInfo);
                }
            }
        }
        return messageMetaInfo;
    }

    private int getSendSerialNo(ChannelContext channelContext) {
        return messageMetaInfoContext.get(channelContext).getId().incrementAndGet();
    }


    @Getter
    @Setter
    private static class MessageMetaInfo {
        private AtomicInteger id = new AtomicInteger(0);

        private Map messageBuffer = new ConcurrentHashMap<>();
    }
}
