package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.util.JsonConvert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class HeartbeatWorker implements Runnable {


    private final static SimpleMessage PONG_DATA = new SimpleMessage();

    private final static SimpleMessage PING_DATA = new SimpleMessage();

    static {
        PONG_DATA.setCmd("pong");
        PING_DATA.setCmd("ping");
    }

    private ChannelContext channelContext;

    public HeartbeatWorker(ChannelContext channelContext) {
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
