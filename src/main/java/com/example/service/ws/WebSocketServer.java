package com.example.service.ws;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket")
@Component
public class WebSocketServer {

    private final ConcurrentHashMap<String,Session> sessionMap = new ConcurrentHashMap<>();

    //连接
    @OnOpen
    public void onOpen(Session session) {
        //获取用户的链接账号，一般用的是比如手机之类的
        String phone = session.getQueryString();

        //把会话保存起来
        sessionMap.put(phone,session);
        System.out.println("connected...");
    }

    //关闭
    @OnClose
    public void onClose(Session session) {
        System.out.println("closed...");
    }

    //接收消息   客户端发送过来的消息
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("【" + session.getId() + "】客户端的发送消息======内容【" + message + "】");
        sendMsg(session,"hello!!!!");
    }

    //异常
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("发生异常!");
        throwable.printStackTrace();
    }

    //统一的发送消息方法
    public synchronized void sendMsg(Session session, String msg) {
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Session getSession(String key){
        return sessionMap.get(key);
    }
}