package com.example.service.handler;

import com.example.service.utils.SpringUtils;
import com.example.service.utils.WsUtil;
import com.example.service.ws.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReceiveMsgHandler implements MessageHandler {

    private static final Map<String,List<String>> redisRef = new HashMap<>();

    static {
        //模拟redis
        List<String> list = new ArrayList<>();
        list.add("18058148144");
        list.add("18868707977");
        redisRef.put("france",list);
    }

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        MessageHeaders headers = message.getHeaders();

        //获取报头和载荷
        String topic = String.valueOf(headers.get(MqttHeaders.RECEIVED_TOPIC));
//
        String data = String.valueOf(message.getPayload());

        //如果是二进制的，需要解析
//        byte[] payload = (byte[]) message.getPayload();
//        String data = parsePayload(payload);

        //从topic找到发送者标识
        String sn = topic.substring(topic.lastIndexOf("/")+1);

        //然后根据发送者标识,在redis的订阅关系中找到对应的消费者
        List<String> phoneList = redisRef.get(sn);

        //根据用户的手机号，找到websocket的session
        for(String phone:phoneList){
            Session session = WsUtil.getSession(phone);
            if(session!=null){
                WsUtil.sendMsg(session,data);
            }else {
                System.out.println("no session fund for "+phone);
            }
        }

    }

    private String parsePayload(byte[] payload) {
        return "{hello:world}";
    }
}
