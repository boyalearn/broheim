package com.broheim.websocket.core.event.send;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendSyncMessageEvent extends SendMessageEvent {

    private Long timeOut;

    public SendSyncMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }
}
