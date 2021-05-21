package com.broheim.websocket.core.endpoint.context;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHolder {

    private static final Map<Session, ConcurrentHashMap<Integer, Object>> sessionMap = new ConcurrentHashMap<>();


    public static void putObject(Session session, Integer serialNo, Object obj) {
        Map<Integer, Object> serialNoMap = ensureSerialNoMapExist(session);
        serialNoMap.put(serialNo, obj);
    }


    public static Object getObject(Session session, Integer serialNo) {
        Map<Integer, Object> serialNoMap = ensureSerialNoMapExist(session);
        return serialNoMap.get(serialNo);
    }

    public static void removeObject(Session session, Integer serialNo) {
        Map<Integer, Object> serialNoMap = ensureSerialNoMapExist(session);
        serialNoMap.remove(serialNo);
    }

    private static Map<Integer, Object> ensureSerialNoMapExist(Session session) {
        ConcurrentHashMap<Integer, Object> serialNoMap = sessionMap.get(session);
        if (null == serialNoMap) {
            synchronized (sessionMap) {
                serialNoMap = sessionMap.get(session);
                if (null == serialNoMap) {
                    serialNoMap = new ConcurrentHashMap();
                    sessionMap.put(session, serialNoMap);
                }
            }
        }
        return serialNoMap;
    }

}
