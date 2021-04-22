package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.reactor.Reactor;
import com.broheim.websocket.core.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleProtocol implements Protocol<SimpleMessage> {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String encode(ChannelContext channelContext, String appMessage) throws MessageProtocolException {
        SimpleMessage message = new SimpleMessage();
        message.setBody(appMessage);
        message.setCmd(Protocol.SYNC);
        message.setSerialNo(channelContext.getEndpoint().sendId().getAndIncrement());
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }

    @Override
    public String encode(ChannelContext channelContext, String message, Integer serialNo) throws MessageProtocolException {
        SimpleMessage simpleMessage = new SimpleMessage();
        simpleMessage.setBody(message);
        simpleMessage.setSerialNo(serialNo);
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new MessageProtocolException();
        }
    }

    public String encode(ChannelContext channelContext, String message, Integer serialNo, String cmd) throws MessageProtocolException {
        SimpleMessage simpleMessage = new SimpleMessage();
        simpleMessage.setBody(message);
        simpleMessage.setCmd(cmd);
        simpleMessage.setSerialNo(serialNo);
        try {
            return objectMapper.writeValueAsString(simpleMessage);
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

    @Override
    public void service(ChannelContext channelContext, String message, Reactor reactor) throws MessageProtocolException {
        SimpleMessage simpleMessage = decode(message);
        if (Protocol.ACK.equals(simpleMessage.getCmd())) {
            return;
        }
        if (Protocol.PING.equals(simpleMessage.getCmd())) {
            return;
        }
        if (Protocol.REQ_RESP.equals(simpleMessage.getCmd())) {
            return;
        }
        if (StringUtil.isEmpty(simpleMessage.getBody())) {
            return;
        }
        reactor.dispatch(simpleMessage.getBody(), channelContext);
    }
}
