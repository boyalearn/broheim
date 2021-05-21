package com.broheim.websocket.core.event;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendAsyncMessageEvent extends SendMessageEvent {

    public SendAsyncMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }
}
