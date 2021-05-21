package com.broheim.websocket.core.event;


import com.broheim.websocket.core.endpoint.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

import javax.websocket.CloseReason;

@Getter
@Setter
public class CloseEvent extends AbstractEvent {

    private CloseReason closeReason;

    public CloseEvent(ChannelContext channelContext, CloseReason closeReason) {
        super(channelContext);
        this.closeReason = closeReason;
    }
}
