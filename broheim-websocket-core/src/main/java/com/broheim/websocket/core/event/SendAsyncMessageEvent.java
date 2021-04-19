package com.broheim.websocket.core.event;

import com.broheim.websocket.core.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendAsyncMessageEvent extends SendMessageEvent {

    private Long timeOut;

    public SendAsyncMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }
}
