package com.broheim.websocket.core.listener;

import com.broheim.websocket.core.endpoint.context.ChannelContext;
import com.broheim.websocket.core.endpoint.context.DefaultChannelContext;
import com.broheim.websocket.core.event.accept.Event;
import com.broheim.websocket.core.event.accept.OnCloseEvent;
import com.broheim.websocket.core.event.accept.OnConnectionEvent;
import com.broheim.websocket.core.event.accept.OnMessageEvent;
import com.broheim.websocket.core.event.send.SendSyncMessageEvent;
import com.broheim.websocket.core.handler.RunnableHandler;
import com.broheim.websocket.core.protocol.SimpleProtocol;
import com.broheim.websocket.core.protocol.message.SimpleMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeoutException;

/**
 * 同步消息
 */
@Slf4j
@Setter
public class SyncMessageSendListener extends MessageSendListener implements Listener {

    public static final String SEND = "sync";

    public static final String ACK = "ack-sync";

    private SimpleProtocol simpleProtocol;


    public SyncMessageSendListener() {
        this.simpleProtocol = new SimpleProtocol();
    }

    private RunnableHandler runnableHandler;

    @Override
    public void onEvent(Event event) throws Exception {
        if (event instanceof SendSyncMessageEvent) {
            SendSyncMessageEvent syncMessageEvent = (SendSyncMessageEvent) event;
            DefaultChannelContext channelContext = (DefaultChannelContext) syncMessageEvent.getChannelContext();

            String message = syncMessageEvent.getMessage();
            SimpleMessage simpleMessage = new SimpleMessage();
            simpleMessage.setType(SEND);
            simpleMessage.setBody(message);
            simpleMessage.setSerialNo(getSendSerialNo(channelContext));
            try {
                channelContext.sendText(simpleProtocol.encode(simpleMessage));
            } catch (Exception e) {
                log.error("send message error", e);
            }
            MessageMetaInfo messageMetaInfo = getMessageMetaInfo(channelContext);
            synchronized (messageMetaInfo) {
                Long timeOut = syncMessageEvent.getTimeOut();
                Long startTime = System.currentTimeMillis();
                if (null == timeOut) {
                    timeOut = 60 * 1000L;
                }
                long leaveTime = timeOut;
                Object acceptMessage = messageMetaInfo.getMessageBuffer().get(simpleMessage.getSerialNo());
                syncMessageEvent.setResult(false);
                while (null == acceptMessage && leaveTime > 0) {
                    messageMetaInfo.wait(leaveTime);
                    acceptMessage = messageMetaInfo.getMessageBuffer().get(simpleMessage.getSerialNo());
                    leaveTime = startTime + timeOut - System.currentTimeMillis();
                }
                if (null == acceptMessage) {
                    syncMessageEvent.setException(new TimeoutException());
                }
                syncMessageEvent.setResult(true);
            }
        }

        if (event instanceof OnConnectionEvent) {
            OnConnectionEvent connectionEvent = (OnConnectionEvent) event;
            ChannelContext channelContext = connectionEvent.getChannelContext();
            messageMetaInfoContext.put(channelContext, new MessageMetaInfo());
        }

        if (event instanceof OnMessageEvent) {
            OnMessageEvent onMessageEvent = (OnMessageEvent) event;
            SimpleMessage acceptMessage = this.simpleProtocol.decode(onMessageEvent.getMessage());
            this.doAccept(this.simpleProtocol, acceptMessage, onMessageEvent.getChannelContext(), runnableHandler);
        }

        if (event instanceof OnCloseEvent) {
            OnCloseEvent closeEvent = (OnCloseEvent) event;
            ChannelContext channelContext = closeEvent.getChannelContext();
            messageMetaInfoContext.remove(channelContext);
        }
    }

    private void doAccept(SimpleProtocol simpleProtocol, SimpleMessage acceptMessage, ChannelContext channelContext, RunnableHandler runnableHandler) throws Exception {
        if (SEND.equals(acceptMessage.getType())) {
            acceptMessage.setType(ACK);
            channelContext.sendText(simpleProtocol.encode(acceptMessage));
            return;
        }
        if (ACK.equals(acceptMessage.getType())) {
            MessageMetaInfo messageMetaInfo = getMessageMetaInfo(channelContext);
            synchronized (messageMetaInfo) {
                messageMetaInfo.getMessageBuffer().put(acceptMessage.getSerialNo(), acceptMessage.getBody());
                messageMetaInfo.notifyAll();
            }
            return;
        }

        if (SEND.equals(acceptMessage.getType()) && null != runnableHandler) {
            runnableHandler.handle(channelContext, acceptMessage.getBody());
        }
    }


}
