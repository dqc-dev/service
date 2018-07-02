package com.example.service.entity.device;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "dqc_device")
public class Device {
    @Id
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "mqtt_client_id")
    private String mqttClientId;

    @Column(name = "mqtt_password")
    private String mqttPassword;

    private String sn;

    @Column(name = "device_model_id")
    private Long deviceModelId;

    @Column(name = "device_batch_id")
    private Long deviceBatchId;

    @Column(name = "remaining_life")
    private Long remainingLife;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "is_saled")
    private Integer isSaled;

    @Column(name = "is_fault")
    private Integer isFault;

    @Column(name = "is_waste")
    private Integer isWaste;

    @Column(name = "is_connect")
    private Integer isConnect;

    @Column(name = "init_state")
    private Integer initState;

    @Column(name = "init_time")
    private Date initTime;

    @Column(name = "fault_msg")
    private String faultMsg;

    @Column(name = "is_lock")
    private Integer isLock;

    private String latitude;

    private String longitude;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "service_type")
    private Integer serviceType;

    private String memo;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "modify_date")
    private Date modifyDate;

    @Column(name = "area_id")
    private Long areaId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "dealer_id")
    private Long dealerId;

    @Column(name = "strainer_change_time")
    private Date strainerChangeTime;

    @Column(name = "datamodule_version")
    private String datamoduleVersion;

    @Column(name = "mcu_version")
    private String mcuVersion;

    /**
     * 换网次数
     */
    @Column(name = "change_times")
    private Integer changeTimes;

    /**
     * mcu模块软件版本号
     */
    @Column(name = "mcu_soft_version")
    private String mcuSoftVersion;

    private String iccid;

    @Column(name = "device_order")
    private Integer deviceOrder;

    @Column(name = "first_use_time")
    private Date firstUseTime;

    @Column(name = "company_id")
    private Long companyId;

    /**
     * @return device_id
     */
    public Long getDeviceId() {
        return deviceId;
    }

    /**
     * @param deviceId
     */
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * @return nick_name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName
     */
    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    /**
     * @return mqtt_client_id
     */
    public String getMqttClientId() {
        return mqttClientId;
    }

    /**
     * @param mqttClientId
     */
    public void setMqttClientId(String mqttClientId) {
        this.mqttClientId = mqttClientId == null ? null : mqttClientId.trim();
    }

    /**
     * @return mqtt_password
     */
    public String getMqttPassword() {
        return mqttPassword;
    }

    /**
     * @param mqttPassword
     */
    public void setMqttPassword(String mqttPassword) {
        this.mqttPassword = mqttPassword == null ? null : mqttPassword.trim();
    }

    /**
     * @return sn
     */
    public String getSn() {
        return sn;
    }

    /**
     * @param sn
     */
    public void setSn(String sn) {
        this.sn = sn == null ? null : sn.trim();
    }

    /**
     * @return device_model_id
     */
    public Long getDeviceModelId() {
        return deviceModelId;
    }

    /**
     * @param deviceModelId
     */
    public void setDeviceModelId(Long deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    /**
     * @return device_batch_id
     */
    public Long getDeviceBatchId() {
        return deviceBatchId;
    }

    /**
     * @param deviceBatchId
     */
    public void setDeviceBatchId(Long deviceBatchId) {
        this.deviceBatchId = deviceBatchId;
    }

    /**
     * @return remaining_life
     */
    public Long getRemainingLife() {
        return remainingLife;
    }

    /**
     * @param remainingLife
     */
    public void setRemainingLife(Long remainingLife) {
        this.remainingLife = remainingLife;
    }

    /**
     * @return is_active
     */
    public Integer getIsActive() {
        return isActive;
    }

    /**
     * @param isActive
     */
    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    /**
     * @return is_saled
     */
    public Integer getIsSaled() {
        return isSaled;
    }

    /**
     * @param isSaled
     */
    public void setIsSaled(Integer isSaled) {
        this.isSaled = isSaled;
    }

    /**
     * @return is_fault
     */
    public Integer getIsFault() {
        return isFault;
    }

    /**
     * @param isFault
     */
    public void setIsFault(Integer isFault) {
        this.isFault = isFault;
    }

    /**
     * @return is_waste
     */
    public Integer getIsWaste() {
        return isWaste;
    }

    /**
     * @param isWaste
     */
    public void setIsWaste(Integer isWaste) {
        this.isWaste = isWaste;
    }

    /**
     * @return is_connect
     */
    public Integer getIsConnect() {
        return isConnect;
    }

    /**
     * @param isConnect
     */
    public void setIsConnect(Integer isConnect) {
        this.isConnect = isConnect;
    }

    /**
     * @return init_state
     */
    public Integer getInitState() {
        return initState;
    }

    /**
     * @param initState
     */
    public void setInitState(Integer initState) {
        this.initState = initState;
    }

    /**
     * @return init_time
     */
    public Date getInitTime() {
        return initTime;
    }

    /**
     * @param initTime
     */
    public void setInitTime(Date initTime) {
        this.initTime = initTime;
    }

    /**
     * @return fault_msg
     */
    public String getFaultMsg() {
        return faultMsg;
    }

    /**
     * @param faultMsg
     */
    public void setFaultMsg(String faultMsg) {
        this.faultMsg = faultMsg == null ? null : faultMsg.trim();
    }

    /**
     * @return is_lock
     */
    public Integer getIsLock() {
        return isLock;
    }

    /**
     * @param isLock
     */
    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    /**
     * @return latitude
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * @param latitude
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude == null ? null : latitude.trim();
    }

    /**
     * @return longitude
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * @param longitude
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude == null ? null : longitude.trim();
    }

    /**
     * @return full_address
     */
    public String getFullAddress() {
        return fullAddress;
    }

    /**
     * @param fullAddress
     */
    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress == null ? null : fullAddress.trim();
    }

    /**
     * @return service_type
     */
    public Integer getServiceType() {
        return serviceType;
    }

    /**
     * @param serviceType
     */
    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @return memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo
     */
    public void setMemo(String memo) {
        this.memo = memo == null ? null : memo.trim();
    }

    /**
     * @return create_date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return modify_date
     */
    public Date getModifyDate() {
        return modifyDate;
    }

    /**
     * @param modifyDate
     */
    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    /**
     * @return area_id
     */
    public Long getAreaId() {
        return areaId;
    }

    /**
     * @param areaId
     */
    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    /**
     * @return class_id
     */
    public Long getClassId() {
        return classId;
    }

    /**
     * @param classId
     */
    public void setClassId(Long classId) {
        this.classId = classId;
    }

    /**
     * @return order_id
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * @param orderId
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * @return school_id
     */
    public Long getSchoolId() {
        return schoolId;
    }

    /**
     * @param schoolId
     */
    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    /**
     * @return dealer_id
     */
    public Long getDealerId() {
        return dealerId;
    }

    /**
     * @param dealerId
     */
    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

    /**
     * @return strainer_change_time
     */
    public Date getStrainerChangeTime() {
        return strainerChangeTime;
    }

    /**
     * @param strainerChangeTime
     */
    public void setStrainerChangeTime(Date strainerChangeTime) {
        this.strainerChangeTime = strainerChangeTime;
    }

    /**
     * @return datamodule_version
     */
    public String getDatamoduleVersion() {
        return datamoduleVersion;
    }

    /**
     * @param datamoduleVersion
     */
    public void setDatamoduleVersion(String datamoduleVersion) {
        this.datamoduleVersion = datamoduleVersion == null ? null : datamoduleVersion.trim();
    }

    /**
     * @return mcu_version
     */
    public String getMcuVersion() {
        return mcuVersion;
    }

    /**
     * @param mcuVersion
     */
    public void setMcuVersion(String mcuVersion) {
        this.mcuVersion = mcuVersion == null ? null : mcuVersion.trim();
    }

    /**
     * 获取换网次数
     *
     * @return change_times - 换网次数
     */
    public Integer getChangeTimes() {
        return changeTimes;
    }

    /**
     * 设置换网次数
     *
     * @param changeTimes 换网次数
     */
    public void setChangeTimes(Integer changeTimes) {
        this.changeTimes = changeTimes;
    }

    /**
     * 获取mcu模块软件版本号
     *
     * @return mcu_soft_version - mcu模块软件版本号
     */
    public String getMcuSoftVersion() {
        return mcuSoftVersion;
    }

    /**
     * 设置mcu模块软件版本号
     *
     * @param mcuSoftVersion mcu模块软件版本号
     */
    public void setMcuSoftVersion(String mcuSoftVersion) {
        this.mcuSoftVersion = mcuSoftVersion == null ? null : mcuSoftVersion.trim();
    }

    /**
     * @return iccid
     */
    public String getIccid() {
        return iccid;
    }

    /**
     * @param iccid
     */
    public void setIccid(String iccid) {
        this.iccid = iccid == null ? null : iccid.trim();
    }

    /**
     * @return device_order
     */
    public Integer getDeviceOrder() {
        return deviceOrder;
    }

    /**
     * @param deviceOrder
     */
    public void setDeviceOrder(Integer deviceOrder) {
        this.deviceOrder = deviceOrder;
    }

    /**
     * @return first_use_time
     */
    public Date getFirstUseTime() {
        return firstUseTime;
    }

    /**
     * @param firstUseTime
     */
    public void setFirstUseTime(Date firstUseTime) {
        this.firstUseTime = firstUseTime;
    }

    /**
     * @return company_id
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}