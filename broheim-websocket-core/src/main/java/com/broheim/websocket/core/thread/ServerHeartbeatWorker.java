package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.util.JsonConvert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ServerHeartbeatWorker implements Runnable {

    private final static SimpleMessage PING_DATA = new SimpleMessage();

    static {
        PING_DATA.setCmd(Protocol.PING);
    }

    private ChannelContext channelContext;

    public ServerHeartbeatWorker(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }


    @Override
    public void run() {
        try {
            channelContext.sendText(JsonConvert.decode(PING_DATA));
        } catch (IOException e) {
            log.error("ping error", e);
        }
    }
}
