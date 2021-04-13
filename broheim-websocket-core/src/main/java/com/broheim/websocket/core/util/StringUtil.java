package com.broheim.websocket.core.util;

public class StringUtil {
    public static boolean isNotEmpty(String context) {
        return null != context && !context.isEmpty();
    }

    public static boolean isEmpty(String context) {
        return null == context || context.isEmpty();
    }
}
