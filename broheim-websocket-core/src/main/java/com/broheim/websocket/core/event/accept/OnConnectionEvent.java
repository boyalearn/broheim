package com.broheim.websocket.core.event.accept;


import com.broheim.websocket.core.endpoint.context.ChannelContext;

import javax.websocket.EndpointConfig;

public class OnConnectionEvent extends AbstractEvent {

    private EndpointConfig endpointConfig;

    public OnConnectionEvent(ChannelContext channelContext, EndpointConfig endpointConfig) {
        super(channelContext);
        this.endpointConfig = endpointConfig;
    }
}
