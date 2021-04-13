package com.broheim.websocket.spring.controller;

import com.framework.websocket.core.context.ChannelContext;
import com.framework.websocket.spring.annonation.Command;
import com.framework.websocket.spring.annonation.WebSocketController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@WebSocketController("/ws")
public class DemoController {

    @Command("hello")
    public void hello(ChannelContext channelContext, String message) {
        log.error(message);
        //channelContext.sendMessage("232323");
    }
}
