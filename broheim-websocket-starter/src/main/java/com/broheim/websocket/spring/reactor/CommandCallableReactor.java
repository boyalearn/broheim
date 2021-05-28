package com.broheim.websocket.spring.reactor;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.handler.CallableHandler;
import com.broheim.websocket.spring.handler.CommandCallableHandler;
import com.broheim.websocket.spring.message.CommandMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Setter
@Slf4j
public class CommandCallableReactor implements CallableHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<CommandCallableHandler> commandCallableHandlers;

    public CommandCallableReactor(){
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Object handle(ChannelContext channelContext, String message) {
        CommandMessage commandMessage;
        try {
            commandMessage = objectMapper.readValue(message, CommandMessage.class);
        } catch (JsonProcessingException e) {
            log.error("json processing exception.", e);
            return null;
        }
        for (CommandCallableHandler handler : this.commandCallableHandlers) {
            if (handler.getCmd().equals(commandMessage.getCmd())) {
                return handler.handle(channelContext, message);
            }
        }
        return null;
    }
}
