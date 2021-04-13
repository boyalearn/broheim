package com.broheim.websocket.spring.reactor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.framework.websocket.core.context.ChannelContext;
import com.framework.websocket.core.handler.Handler;
import com.framework.websocket.core.reactor.Reactor;
import com.framework.websocket.spring.handler.CommandHandler;
import com.framework.websocket.spring.message.CommandMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandReactor implements Reactor {


    private volatile List<Handler> handlers = new ArrayList<>();

    private static ObjectMapper objectMapper = new ObjectMapper();


    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private volatile Map<String, Handler> dispatcherCenter;

    @Override
    public void addHandler(Handler handler) {
        this.handlers.add(handler);
    }

    @Override
    public void addHandlers(List<Handler> handlers) {
        this.handlers.addAll(handlers);
    }

    @Override
    public void dispatch(String message, ChannelContext context) {

        CommandMessage commandMessage = null;
        try {
            commandMessage = objectMapper.readValue(message, CommandMessage.class);
        } catch (JsonProcessingException e) {
            log.error("parse commandMessage error, server ignore this message {}", message, e);
            return;
        }
        if (null == this.dispatcherCenter) {
            Map<String, Handler> map = new HashMap<>();
            for (Handler handler : this.handlers) {
                map.put(((CommandHandler) handler).getCmd(), handler);
            }
            this.dispatcherCenter = map;
        }
        Handler handler = this.dispatcherCenter.get(commandMessage.getCmd());
        if (null != handler) {
            handler.handle(context, message);
        }
    }
}
