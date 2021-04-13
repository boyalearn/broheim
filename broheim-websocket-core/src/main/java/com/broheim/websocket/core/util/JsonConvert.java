package com.broheim.websocket.core.util;


import com.broheim.websocket.core.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConvert {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Message encode(String json, Class<?> clazz) throws JsonProcessingException {
        return (Message) objectMapper.readValue(json, clazz);
    }

    public static String decode(Object message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
