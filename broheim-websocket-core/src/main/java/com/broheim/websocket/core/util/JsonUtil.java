package com.broheim.websocket.core.util;


import com.broheim.websocket.core.message.Message;

public class JsonUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Message encode(String json, Class<?> clazz) throws JsonProcessingException {
        return (Message) objectMapper.readValue(json, clazz);
    }

    public static String decode(Object message) throws JsonProcessingException {
        return objectMapper.writeValueAsString(message);
    }
}
