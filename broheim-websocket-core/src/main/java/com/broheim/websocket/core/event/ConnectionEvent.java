package com.broheim.websocket.core.event;


import com.broheim.websocket.core.endpoint.context.ChannelContext;

import javax.websocket.EndpointConfig;

public class ConnectionEvent extends AbstractEvent {

    private EndpointConfig endpointConfig;

    public ConnectionEvent(ChannelContext channelContext, EndpointConfig endpointConfig) {
        super(channelContext);
        this.endpointConfig = endpointConfig;
    }
}
