package com.broheim.websocket.core.thread;

import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.endpoint.AbstractWebSocketEndpoint;
import com.broheim.websocket.core.endpoint.WebSocketEndpoint;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.protocol.Protocol;
import com.broheim.websocket.core.util.JsonConvert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class ServerHeartbeatWorker implements Runnable {

    private final static SimpleMessage PING_DATA = new SimpleMessage();

    private int loseTime;

    static {
        PING_DATA.setCmd(Protocol.PING);
    }

    private ChannelContext channelContext;

    public ServerHeartbeatWorker(ChannelContext channelContext) {
        this.channelContext = channelContext;
    }


    @Override
    public void run() {
        log.debug("ping and check connection...");
        try {
            channelContext.sendText(JsonConvert.decode(PING_DATA));
        } catch (IOException e) {
            log.error("ping error", e);
        }

        WebSocketEndpoint endpoint = channelContext.getEndpoint();
        log.debug("server close connect time {}", loseTime);
        if (this.loseTime > 4) {
            try {
                endpoint.getSession().close();
            } catch (IOException e) {
                log.error("server close connect error...", e);
            } finally {
                log.debug("server close connect...");
            }
        }
        if (endpoint instanceof AbstractWebSocketEndpoint) {
            if (new Date().getTime() - ((AbstractWebSocketEndpoint) endpoint).getLastAcceptTime() > 3 * 1000) {
                loseTime++;
            } else {
                loseTime = 0;
            }
        }
    }
}
