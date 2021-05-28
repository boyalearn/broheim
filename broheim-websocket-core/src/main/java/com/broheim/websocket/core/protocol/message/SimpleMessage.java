package com.broheim.websocket.core.protocol.message;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SimpleMessage implements Message {
    private Integer serialNo;
    private String type;
    private String body;
}
