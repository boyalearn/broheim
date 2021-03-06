package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.client.WebSocketClient;
import com.broheim.websocket.core.context.ChannelContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class ClientHeartbeatWorker implements Runnable {

    private ChannelContext channelContext;

    private int loseTime;

    public ClientHeartbeatWorker(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }

    @Override
    public void run() {
        log.debug("lose time is {},check connect...", this.loseTime);
        WebSocketClient endpoint = (WebSocketClient) channelContext.getEndpoint();
        if (loseTime >= 3) {
            try {
                endpoint.getSession().close();
            } catch (IOException e) {
                log.error("client close connect error...", e);
            } finally {
                log.debug("client close connect...");
            }
        }
        if (new Date().getTime() - endpoint.getLastAcceptTime() > 3 * 1000) {
            loseTime++;
        } else {
            loseTime = 0;
        }
    }
}
