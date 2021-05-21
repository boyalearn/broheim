package com.broheim.websocket.core.event;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorEvent extends AbstractEvent {

    private Throwable throwable;

    public ErrorEvent(ChannelContext channelContext, Throwable throwable) {
        super(channelContext);
        this.throwable = throwable;
    }
}
