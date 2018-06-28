package com.example.service.mqtt.receive;

import com.example.service.utils.RedisSimulateUtil;
import com.example.service.utils.SpringUtil;
import com.example.service.utils.WebSocketSessionUtil;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import javax.websocket.Session;
import java.util.List;

public class MsgArrivedTask implements Runnable {

    private Message<?> message;

    public MsgArrivedTask(Message<?> message){
        this.message = message;
    }

    @Override
    public void run() {
        MessageHeaders headers = message.getHeaders();

        //获取报头和载荷
        String topic = String.valueOf(headers.get(MqttHeaders.RECEIVED_TOPIC));
//
        String data = String.valueOf(message.getPayload());

        //如果是二进制的，需要解析
//        byte[] payload = (byte[]) message.getPayload();
//        String data = parsePayload(payload);

        //从topic找到发送者标识
        String sn = topic.substring(topic.lastIndexOf("/") + 1);

        //然后根据发送者标识,在redis的订阅关系中找到对应的消费者
        List<String> phoneList = RedisSimulateUtil.get(sn);

        SimpMessageSendingOperations operations = SpringUtil.getBean(SimpMessageSendingOperations.class);

        //根据用户的手机号，找到websocket的session
        for (String phone : phoneList) {
            operations.convertAndSendToUser(phone,"/message","");
        }
    }

    private String parsePayload(byte[] payload) {
        return "{hello:world}";
    }
}
