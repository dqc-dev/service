package com.example.service.service.device;


import com.example.service.entity.device.Device;
import com.example.service.service.base.IService;

/**
 * Created by Arthur on 2017/3/14 0014.
 */
public interface DeviceService extends IService<Device> {

    Device selectBySn(String sn);
}
