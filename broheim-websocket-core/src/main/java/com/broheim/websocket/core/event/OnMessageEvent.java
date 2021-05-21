package com.broheim.websocket.core.event;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnMessageEvent extends AbstractEvent {

    private String message;

    public OnMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext);
        this.message = message;
    }
}
