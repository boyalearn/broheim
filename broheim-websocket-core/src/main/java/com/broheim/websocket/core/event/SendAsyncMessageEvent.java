package com.broheim.websocket.core.event;

import com.broheim.websocket.core.context.ChannelContext;
import lombok.Getter;

@Getter
public class SendAsyncMessageEvent extends SendMessageEvent {


    public SendAsyncMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }
}
