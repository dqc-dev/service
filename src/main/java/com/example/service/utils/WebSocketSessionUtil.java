package com.example.service.utils;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionUtil {

    private static final ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();

    public static void putSession(String key,Session session){
        sessionMap.put(key,session);
    }

    public static Session getSession(String key){
        return sessionMap.get(key);
    }

    public static void sendMsg(Session session,String msg){
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
