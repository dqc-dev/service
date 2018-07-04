package com.example.service.service.mqtt.impl;

import com.example.service.constants.MqttConstants;
import com.example.service.entity.device.Device;
import com.example.service.enums.device.DeviceActiveEnum;
import com.example.service.enums.device.DeviceConnectEnum;
import com.example.service.mqtt.send.MqttMsgPublisher;
import com.example.service.service.base.impl.BaseTopicServiceImpl;
import com.example.service.service.mqtt.ConnTopicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.globalvillage.bg.common.entity.Device;

/**
 * @Author 李可可
 * @Date 2016/9/26 11:53
 */
@Service(MqttConstants.MQTT_TOPIC_DEV_UP_CONN)
public class ConnTopicServiceImpl extends BaseTopicServiceImpl implements ConnTopicService {

    @Autowired
    private MqttMsgPublisher mqttMsgPublisher;

    @Transactional
    @Override
    public void doInMessageArrived(String topic, byte[] payload, Device device) {

        int connState = MqttConstants.DEV_CONN_MQTT == payload[0] ? DeviceConnectEnum.CONNECT.getValue() : DeviceConnectEnum.UN_CONNECT.getValue();

        //设备联网时校验是否已上报过坐标信息，若无，发送上报坐标报文
        if (DeviceConnectEnum.CONNECT.getValue() == connState) {

            if (device.getIsActive() == DeviceActiveEnum.ACTIVED.getValue()) {
                if (StringUtils.isBlank(device.getMcuVersion()) && StringUtils.isBlank(device.getDatamoduleVersion())) { //如果设备记录中无mcu固件版本信息，则下发查询版本信息报文
                    mqttMsgPublisher.sendMessage(MqttConstants.MQTT_TOPIC_DEV_DOWN_VER + device.getSn(), null);
                }
            }
            device.setIsConnect(DeviceConnectEnum.CONNECT.getValue());
        } else {
            device.setIsConnect(DeviceConnectEnum.UN_CONNECT.getValue());
        }

        //redis的更新由mqtt appserver控制，这里只更新数据库
        deviceService.updateByPrimaryKeySelective(device);
    }

}
