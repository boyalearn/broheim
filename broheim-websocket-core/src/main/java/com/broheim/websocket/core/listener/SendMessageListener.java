package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class SendMessageListener {

    protected volatile Map<ChannelContext, MessageMetaInfo> messageMetaInfoContext = new ConcurrentHashMap<>();


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
        private volatile AtomicInteger id = new AtomicInteger(0);

        private volatile Map messageBuffer = new ConcurrentHashMap<>();
    }
}
