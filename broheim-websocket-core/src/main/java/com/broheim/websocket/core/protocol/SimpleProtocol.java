package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleProtocol implements Protocol<SimpleMessage> {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String encode(String appMessage) throws MessageProtocolException {
        SimpleMessage message = new SimpleMessage();
        message.setBody(appMessage);
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }

    @Override
    public SimpleMessage decode(String message) throws MessageProtocolException {
        try {
            return objectMapper.readValue(message, SimpleMessage.class);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }

    public String encode(SimpleMessage simpleMessage) throws MessageProtocolException {
        try {
            return objectMapper.writeValueAsString(simpleMessage);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }
}
