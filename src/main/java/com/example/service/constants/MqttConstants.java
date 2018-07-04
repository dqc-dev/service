package com.example.service.constants;

public interface MqttConstants {

    String MQTT_HOST = "tcp://192.168.1.113:1883";

    String MQTT_CLIENT_ID = "testClient";

    /**设备上报*/
    String MQTT_TOPIC_DEV_UP_PREFIX = "dev/up/#";

    /**app用户添加设备报文*/
    String MQTT_TOPIC_ADD_DEV = "user/dev/add";
    /**app用户删除设备报文*/
    String MQTT_TOPIC_DEL_DEV = "user/dev/del";
    /**控制设备报文前缀*/
    String MQTT_TOPIC_CTR_DEV = "dev/down/control/";
    /**上报设备的联网状态主题名*/
    String MQTT_TOPIC_DEV_UP_CONN = "dev/up/conn/";
    /**上报设备的运行状体主题*/
    String MQTT_TOPIC_DEV_UP_STA = "dev/up/sta/";
    /**上报设备的故障状态主题名*/
    String MQTT_TOPIC_DEV_UP_ERR = "dev/up/err/";
    /**上报设备的经纬度主题名*/
    String MQTT_TOPIC_DEV_UP_GEO = "dev/up/geo/";
    /**上报设备的mcu固件版本主题名*/
    String MQTT_TOPIC_DEV_UP_VER = "dev/up/ver/";
    /**M6311升级成功后的结果报文*/
    String MQTT_TOPIC_M6311_UPDATE_RESULT = "dev/up/upgradeMStatus/";
    /**设备完成升级或者升级失败后上报此主题名的报文*/
    String MQTT_TOPIC_DEV_UP_UPDATEVER = "dev/up/updateResult/";
    /**查看设备的地理位置信息*/
    String MQTT_TOPIC_DEV_DOWN_GEO = "dev/down/geo/";
    /**查看设备的mcu固件版本信息*/
    String MQTT_TOPIC_DEV_DOWN_VER = "dev/down/ver/";
    /**查看设备运行状态主题名（包括空气质量信息）*/
    String MQTT_TOPIC_DEV_DOWN_STA = "dev/down/sta/";
    /**发布设备固件新版本*/
    String MQTT_TOPIC_DEV_DOWN_NEW_VER = "dev/down/newVer/";
    /**发布设备M6311 data module 固件新版本*/
    String MQTT_TOPIC_DEV_DOWN_NEW_MVER = "dev/down/newMVer/";
    /**设置定时器*/
    String MQTT_TOPIC_DEV_DOWN_EDITTIMER = "dev/down/editTimer/";
    /**查看设备连接移动网络的基站信息*/
    String MQTT_TOPIC_DEV_DOWN_SINFOR = "dev/down/sInfor/";
    /**设备上报移动网络的基站信息*/
    String MQTT_TOPIC_DEV_UP_SINFOR = "dev/up/sInfor/";

    /**设备联网payload内容*/
    public static final byte DEV_CONN_MQTT = 0x01;
}
