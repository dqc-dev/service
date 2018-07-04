package com.example.service.service.base.impl;

import com.example.service.entity.device.Device;
import com.example.service.service.base.BaseTopicService;
import com.example.service.service.device.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author SoleRay
 * @Date 2016/9/26 11:51
 */
public abstract class BaseTopicServiceImpl implements BaseTopicService {


    @Autowired
    protected DeviceService deviceService;

    @Transactional
    @Override
    public void processMessageArrived(String topic, byte[] payload) throws Exception {

        doInMessageArrived(topic, payload, null);

    }

    public void doInEmptyPayload(String devSn) throws Exception {
    }

    public abstract void doInMessageArrived(String topic, byte[] payload, Device device) throws Exception;

}