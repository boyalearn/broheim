package com.broheim.websocket.core.event;

import com.broheim.websocket.core.context.ChannelContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResponseMessageEvent extends SendMessageEvent {

    public static final ThreadLocal<Object> MESSAGE_THREAD_LOCAL = new ThreadLocal<>();

    private Long timeOut;

    private Object result;

    public RequestResponseMessageEvent(ChannelContext channelContext, String message) {
        super(channelContext, message);
    }

    public void setResult(Object obj) {
        this.result=obj;
    }

    public Object getResult() {
        return this.result;
    }
}
