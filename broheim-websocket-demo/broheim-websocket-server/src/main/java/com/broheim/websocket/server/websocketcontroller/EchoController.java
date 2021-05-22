package com.broheim.websocket.server.websocketcontroller;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.spring.annonation.Command;
import com.broheim.websocket.spring.annonation.WebSocketController;
import org.springframework.stereotype.Component;

@Component
@WebSocketController("/ws")
public class EchoController {

    @Command("hello")
    public Object doHandle(ChannelContext channelContext, String message) throws InterruptedException {
        System.out.println(message);
        Thread.sleep(2000);
        return message;
    }
}
