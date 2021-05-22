package com.broheim.websocket.spring.reactor;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.handler.RunnableHandler;
import com.broheim.websocket.spring.handler.CommandRunnableHandler;
import com.broheim.websocket.spring.message.CommandMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Setter
public class CommandRunnableReactor implements RunnableHandler {


    private ObjectMapper objectMapper = new ObjectMapper();

    private List<CommandRunnableHandler> commandRunnableHandlers;

    public CommandRunnableReactor(){
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    @Override
    public void handle(ChannelContext channelContext, String message) {
        CommandMessage commandMessage;
        try {
            commandMessage = objectMapper.readValue(message, CommandMessage.class);
        } catch (JsonProcessingException e) {
            log.error("json processing exception.", e);
            return;
        }
        for (CommandRunnableHandler handler : this.commandRunnableHandlers) {
            if (handler.getCmd().equals(commandMessage.getCmd())) {
                handler.handle(channelContext, message);
                return;
            }
        }
    }
}
