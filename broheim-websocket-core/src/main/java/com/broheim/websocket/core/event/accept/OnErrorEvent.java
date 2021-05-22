package com.broheim.websocket.core.event.accept;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnErrorEvent extends AbstractEvent {

    private Throwable throwable;

    public OnErrorEvent(ChannelContext channelContext, Throwable throwable) {
        super(channelContext);
        this.throwable = throwable;
    }
}
