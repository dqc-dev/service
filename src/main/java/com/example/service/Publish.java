package com.example.service;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Publish {
    public static final String HOST = "tcp://192.168.6.123:1883";
    public static final String TOPIC = "topic2";
    private static final String clientid = "server12";

    private MqttClient client;
    private MqttTopic topic;
    private String username = "test";
    private String password = "test";

    private MqttMessage message;

    public Publish() throws MqttException {
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        connect();
    }

    public static void main(String[] args) throws MqttException {
        Publish server = new Publish();

        server.message = new MqttMessage();
        server.message.setQos(1);
        server.message.setRetained(true);
        server.message.setPayload("hello world!".getBytes());
        MqttDeliveryToken token = server.topic.publish(server.message);
        token.waitForCompletion();
        System.out.println("published completely: " + token.isComplete());
        System.out.println("ratained状态: " + server.message.isRetained());
    }

    /**
     * 连接服务器
     */
    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        try {
            client.setCallback(new PushCallback());
            client.connect(options);

            topic = client.getTopic(TOPIC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
