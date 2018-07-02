package com.example.service.enums.device;

/**
 * ClassName: DeviceConnectEnum.java
 * @Description: 
 * @author Terry
 * @date Aug 5, 2016 11:12:50 AM
 * @version 1.0
 */
public enum DeviceConnectEnum {

	HIDDEN(-1,"未使用"),
	CONNECT(1,"联网"),
	UN_CONNECT(0,"未联网");

	private int value;
	
	private String name;

	private DeviceConnectEnum(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public static String getNameByValue(int value){
		for(DeviceConnectEnum connectEnum : DeviceConnectEnum.values()){
			if(connectEnum.getValue()==value){
				return connectEnum.getName();
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
