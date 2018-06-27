package com.example.service.mqtt.send;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttMsgPublisher {

    @Autowired
    private MqttPahoMessageHandler mqttPahoMessageHandler;

    public void sendMessage(String topic,String payload){
        Message<String> message = MessageBuilder.withPayload(payload).setHeader(MqttHeaders.TOPIC, topic).build();
        mqttPahoMessageHandler.handleMessage(message);
    }

}
