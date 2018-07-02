package com.example.service.service.device;

import com.globalvillage.bg.common.bean.page.PageList;
import com.globalvillage.bg.common.dto.device.DeviceListDTO;
import com.globalvillage.bg.common.dto.device.WebActivedDevListDTO;
import com.globalvillage.bg.common.mybatis.entity.device.Device;
import com.globalvillage.bg.common.service.base.IService;

import java.util.List;

/**
 * Created by Arthur on 2017/3/14 0014.
 */
public interface DeviceService extends IService<Device> {

    PageList<DeviceListDTO> listUserDevices(Long id);

    void changeStrainer(String userToken, String deviceSn, String newStrainerSn, String strainerModelCode) throws Exception;

    PageList<WebActivedDevListDTO> listActivedDevice(long categoryId, Long companyId, String deviceSn, long start, int limit);

    void scanPage(Long appUserId, String deviceSn, String pageId) throws Exception;

    DeviceListDTO getDeviceDetail(long deviceId, Long appUserId);

    Device selectBySn(String devSn);

    void clearAddress(long deviceId);

    void activeDevice(Long deviceId, Long deviceBatchId);

    List<Device> checkActive(List<Device> devList);


    List<Device> selectActivedDevice();

    List<Device> selectActivedAndOnlineDevice();

    String getDeviceCode(String sn) throws Exception;
}
