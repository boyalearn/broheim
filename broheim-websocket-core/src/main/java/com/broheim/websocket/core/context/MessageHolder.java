package com.broheim.websocket.core.context;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHolder {

    private static final ConcurrentHashMap<Session, ConcurrentHashMap<Integer, Object>> sessionMap = new ConcurrentHashMap<>();


    public static void putObject(Session session, Integer serialNo, Object obj) {
        ConcurrentHashMap<Integer, Object> serialNoMap = ensureSerialNoMapExist(session);
        serialNoMap.put(serialNo, obj);
    }


    public static Object getObject(Session session, Integer serialNo) {
        ConcurrentHashMap<Integer, Object> serialNoMap = ensureSerialNoMapExist(session);
        return serialNoMap.get(serialNo);
    }

    public static void removeObject(Session session, Integer serialNo) {
        ConcurrentHashMap<Integer, Object> serialNoMap = ensureSerialNoMapExist(session);
        serialNoMap.remove(serialNo);
    }

    private static ConcurrentHashMap<Integer, Object> ensureSerialNoMapExist(Session session) {
        ConcurrentHashMap<Integer, Object> serialNoMap = sessionMap.get(session);
        if (null == serialNoMap) {
            synchronized (sessionMap) {
                serialNoMap = sessionMap.get(session);
                if (null == serialNoMap) {
                    serialNoMap = new ConcurrentHashMap<>();
                    sessionMap.put(session, serialNoMap);
                }
            }
        }
        return serialNoMap;
    }

}
