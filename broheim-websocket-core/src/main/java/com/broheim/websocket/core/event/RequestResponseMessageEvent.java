package com.broheim.websocket.core.event;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResponseMessageEvent extends SendMessageEvent {

    private Long timeOut;

    public RequestResponseMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }
}
