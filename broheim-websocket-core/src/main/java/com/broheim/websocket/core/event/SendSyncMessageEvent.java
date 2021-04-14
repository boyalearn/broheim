package com.broheim.websocket.core.event;

import com.broheim.websocket.core.context.ChannelContext;

public class SendSyncMessageEvent extends SendMessageEvent {
    public SendSyncMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }
}
