package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.handler.CallableHandler;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SendMessageListener {

    protected Map<ChannelContext, MessageMetaInfo> messageMetaInfoContext = new ConcurrentHashMap<>();


    protected MessageMetaInfo getMessageMetaInfo(ChannelContext channelContext) {
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

    protected int getSendSerialNo(ChannelContext channelContext) {
        return getMessageMetaInfo(channelContext).getId().incrementAndGet();
    }



    @Getter
    @Setter
    protected static class MessageMetaInfo {
        private AtomicInteger id = new AtomicInteger(0);

        private Map messageBuffer = new ConcurrentHashMap<>();
    }
}
