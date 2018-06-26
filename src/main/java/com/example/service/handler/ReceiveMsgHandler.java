package com.example.service.handler;

import com.example.service.utils.SpringUtils;
import com.example.service.ws.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveMsgHandler implements MessageHandler {

    private Map<String,List<String>> redisRef = new HashMap<>();

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();

        //获取报头和载荷
        String topic = String.valueOf(headers.get(MqttHeaders.RECEIVED_TOPIC));
        byte[] payload = (byte[]) message.getPayload();

        //解析载荷
        String jsonStr = parsePayload(payload);

        //从topic找到设备的sn
        String sn = topic.substring(3, 26);

        //然后根据sn,在redis的订阅关系中找到对应的手机号
        List<String> phoneList = redisRef.get(sn);

        //获取WebSocketServer
        WebSocketServer server = (WebSocketServer) SpringUtils.getBean(WebSocketServer.class);

        //根据用户的手机号，找到websocket的session
        for(String phone:phoneList){
            Session session = server.getSession(phone);
            server.sendMsg(session,jsonStr);
        }

    }

    private String parsePayload(byte[] payload) {
        return "";
    }
}
