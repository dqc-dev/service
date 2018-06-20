package com.example.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Subscribe {
    public static final String HOST = "tcp://192.168.2.181:1883";
    public static final String TOPIC = "topic2";
    private static final String clientid = "client1";
    private MqttClient client;
    private MqttConnectOptions options;
    private String username = "test";
    private String password = "test";

    public static void main(String[] args) throws MqttException {
        Subscribe client = new Subscribe();
        client.start();
    }

    private void start() {
        try {
            client = new MqttClient(HOST, clientid, new MemoryPersistence());

            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new PushCallback());
            MqttTopic topic = client.getTopic(TOPIC);
            options.setWill(topic, "close".getBytes(), 2, true);

            client.connect(options);
            int[] Qos = {1};
            String[] topic1 = {TOPIC};
            client.subscribe(topic1, Qos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
