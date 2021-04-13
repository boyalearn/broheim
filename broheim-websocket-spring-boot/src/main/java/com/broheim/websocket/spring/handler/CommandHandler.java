package com.broheim.websocket.spring.handler;


import com.broheim.websocket.core.handler.Handler;

public interface CommandHandler extends Handler {

    String getCmd();
}
