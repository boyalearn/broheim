package com.broheim.websocket.spring.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandMessage {

    private String cmd;

    private String body;
}

