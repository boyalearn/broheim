package com.broheim.websocket.core.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SimpleMessage implements Message {

    private Integer serialNo;

    private String cmd;

    private String body;
}
