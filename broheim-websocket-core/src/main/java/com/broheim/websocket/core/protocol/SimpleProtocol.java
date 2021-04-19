package com.broheim.websocket.core.protocol;


import com.broheim.websocket.core.context.ChannelContext;
import com.broheim.websocket.core.exception.MessageProtocolException;
import com.broheim.websocket.core.message.SimpleMessage;
import com.broheim.websocket.core.reactor.Reactor;
import com.broheim.websocket.core.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class SimpleProtocol implements Protocol<SimpleMessage> {

    private static final String PING = "ping";

    private static final String OK = "ok";

    private ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String encode(ChannelContext channelContext, String appMessage) throws MessageProtocolException {
        SimpleMessage message = new SimpleMessage();
        message.setBody(appMessage);
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
        //如果应答的是OK 需要通知同步等待的Send线程
        if (OK.equals(simpleMessage.getCmd())) {
            return;
        }

        //自动应答表示已经收到消息
        SimpleMessage autoRespMessage = new SimpleMessage();
        autoRespMessage.setCmd(OK);
        autoRespMessage.setBody(null);
        autoRespMessage.setSerialNo(simpleMessage.getSerialNo());
        try {
            channelContext.sendText(objectMapper.writeValueAsString(autoRespMessage));
        } catch (JsonProcessingException e) {
            log.error("auto response json processing exception error", e);
        } catch (IOException e) {
            log.error("auto response io exception error", e);
        }

        if (PING.equals(simpleMessage.getCmd())) {
            return;
        }
        if (StringUtil.isEmpty(simpleMessage.getBody())) {
            return;
        }
        reactor.dispatch(simpleMessage.getBody(), channelContext);
    }
}
