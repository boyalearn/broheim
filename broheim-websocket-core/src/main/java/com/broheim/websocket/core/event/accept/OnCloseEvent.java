package com.broheim.websocket.core.event.accept;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

import javax.websocket.CloseReason;

@Getter
@Setter
public class OnCloseEvent extends AbstractEvent {

    private CloseReason closeReason;

    public OnCloseEvent(ChannelContext channelContext, CloseReason closeReason) {
        super(channelContext);
        this.closeReason = closeReason;
    }
}
