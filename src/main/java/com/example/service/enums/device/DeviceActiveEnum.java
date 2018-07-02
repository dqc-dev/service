package com.example.service.enums.device;

/**
 * Created by Arthur on 2016/10/18 0018.
 */
public enum DeviceActiveEnum {

    INACTIVE(0,"未激活"),
    ACTIVING(-1,"待激活"),
    ACTIVED(1,"已激活");

    private int value;

    private String name;

    private DeviceActiveEnum(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getNameByValue(int value){
        for(DeviceActiveEnum activeEnum : DeviceActiveEnum.values()){
            if(activeEnum.getValue()==value){
                return activeEnum.getName();
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
