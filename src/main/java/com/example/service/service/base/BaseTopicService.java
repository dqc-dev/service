package com.example.service.service.base;


import com.example.service.entity.device.Device;

public interface BaseTopicService {

    void processMessageArrived(String topic, byte[] payload) throws Exception;

    void doInEmptyPayload(String devSn) throws Exception;

    void doInMessageArrived(String topic, byte[] payload, Device device) throws Exception;
}
