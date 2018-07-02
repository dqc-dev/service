package com.example.service.service.device.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.service.entity.device.Device;
import com.example.service.service.device.DeviceService;
import com.globalvillage.bg.common.baseUtils.Constants;
import com.globalvillage.bg.common.baseUtils.date.LocalDateUtil;
import com.globalvillage.bg.common.baseUtils.resp.ProtocolUtils;
import com.globalvillage.bg.common.baseUtils.resp.ResponseUtil;
import com.globalvillage.bg.common.baseUtils.sn.SnUtil;
import com.globalvillage.bg.common.bean.device.AppDev;
import com.globalvillage.bg.common.bean.device.DeviceCodeBean;
import com.globalvillage.bg.common.bean.lock.RedisLockBusBean;
import com.globalvillage.bg.common.bean.page.PageList;
import com.globalvillage.bg.common.bean.result.Result;
import com.globalvillage.bg.common.dto.classes.ClassAirDTO;
import com.globalvillage.bg.common.dto.device.ClassDeviceDTO;
import com.globalvillage.bg.common.dto.device.DeviceListDTO;
import com.globalvillage.bg.common.dto.device.WebActivedDevListDTO;
import com.globalvillage.bg.common.dto.device.WebDevDistributeDTO;
import com.globalvillage.bg.common.dto.map.dist.DevCountDistDTO;
import com.globalvillage.bg.common.dto.strainer.StrainerListDTO;
import com.globalvillage.bg.common.dto.user.app.device.DeviceRelUserDTO;
import com.globalvillage.bg.common.enums.alert.AlertEnum;
import com.globalvillage.bg.common.enums.area.AreaLevel;
import com.globalvillage.bg.common.enums.common.DateFormaterStyle;
import com.globalvillage.bg.common.enums.dealer.DealerLevelEnum;
import com.globalvillage.bg.common.enums.device.*;
import com.globalvillage.bg.common.enums.push.PushTypeEnum;
import com.globalvillage.bg.common.enums.qos.QosEnum;
import com.globalvillage.bg.common.enums.redis.*;
import com.globalvillage.bg.common.enums.screen.ScreenEnum;
import com.globalvillage.bg.common.exception.BusinessException;
import com.globalvillage.bg.common.mqtt.MqttUtils;
import com.globalvillage.bg.common.mybatis.dao.device.DevLocationRefreshDao;
import com.globalvillage.bg.common.mybatis.dao.device.DeviceDao;
import com.globalvillage.bg.common.mybatis.dao.screen.AppUserPageDao;
import com.globalvillage.bg.common.mybatis.entity.area.Area;
import com.globalvillage.bg.common.mybatis.entity.batch.DeviceBatch;
import com.globalvillage.bg.common.mybatis.entity.dealer.Dealer;
import com.globalvillage.bg.common.mybatis.entity.device.DeviceDailyLifeCost;
import com.globalvillage.bg.common.mybatis.entity.model.DeviceModel;
import com.globalvillage.bg.common.mybatis.entity.relation.UserDeviceRelation;
import com.globalvillage.bg.common.mybatis.entity.screen.AppUserPage;
import com.globalvillage.bg.common.mybatis.entity.strainer.DeviceStrainer;
import com.globalvillage.bg.common.mybatis.entity.user.AppUser;
import com.globalvillage.bg.common.service.air.airCity.AirCityService;
import com.globalvillage.bg.common.service.area.AreaMService;
import com.globalvillage.bg.common.service.base.impl.IServiceImpl;
import com.globalvillage.bg.common.service.batch.DeviceBatchService;
import com.globalvillage.bg.common.service.dealer.DealerService;
import com.globalvillage.bg.common.service.device.DeviceDailyLifeCostService;
import com.globalvillage.bg.common.service.device.DeviceModelService;
import com.globalvillage.bg.common.service.lock.RedisLockService;
import com.globalvillage.bg.common.service.push.AppRegPushMarkService;
import com.globalvillage.bg.common.service.redis.RedisService;
import com.globalvillage.bg.common.service.relation.UserDeviceRelationService;
import com.globalvillage.bg.common.service.strainer.DeviceStrainerService;
import com.globalvillage.bg.common.service.user.AppUserManageService;
import com.globalvillage.bg.common.service.weather.weatherCity.WeatherCityService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * Created by Arthur on 2017/3/14 0014.
 */
@Service
public class DeviceServiceImpl extends IServiceImpl<Device> implements DeviceService {
    @Resource
    private DeviceDao deviceDao;

    @Resource
    private AppUserManageService appUserManageService;

    @Resource
    private AppUserPageDao appUserPageDao;

    @Resource
    private UserDeviceRelationService userDeviceRelationService;

    @Resource
    private DeviceStrainerService deviceStrainerService;

    @Resource
    private DevLocationRefreshDao devLocationRefreshDao;

    @Resource
    private DeviceModelService deviceModelService;

    @Resource
    private AirCityService airCityService;

    @Resource
    private WeatherCityService weatherCityService;

    @Resource
    private DeviceDailyLifeCostService deviceDailyLifeCostService;

    @Resource
    private AppRegPushMarkService appRegPushMarkService;

    @Resource
    private RedisService redisService;

    @Resource
    private RedisLockService redisLockService;

    @Resource
    private AreaMService areaMService;

    @Resource
    private DeviceBatchService deviceBatchService;

    @Resource
    private DealerService dealerService;

    @Override
    public List<ClassAirDTO> selectAllClassDevices() {
        return deviceDao.selectAllClassDevices();
    }

    @Override
    public PageList<DeviceListDTO> listUserDevices(Long appUserId) {
        PageList<DeviceListDTO> pageList = new PageList<>();
        List<DeviceListDTO> dtos = deviceDao.listUserDevices(appUserId);
        for (DeviceListDTO dto : dtos) {
            processSingleDevice(dto);
        }
        Collections.sort(dtos, new Comparator<DeviceListDTO>() {
            @Override
            public int compare(DeviceListDTO o1, DeviceListDTO o2) {
                if (o1.getIsManager() - o2.getIsManager() != 0) {
                    return o2.getIsManager() - o1.getIsManager();
                } else {
                    return o1.getServiceType() - o2.getServiceType();
                }
            }
        });

        pageList.setContent(dtos);
        return pageList;
    }

    @Override
    public DeviceListDTO getDeviceDetail(long deviceId, Long appUserId) {
        DeviceListDTO dto = deviceDao.getDeviceDetail(deviceId, appUserId);
        processArea(dto);
        processSingleDevice(dto);
        return dto;
    }

    private void processArea(DeviceListDTO dto) {

        String fullName = "";

        Area area = areaMService.selectByPrimaryKey(dto.getAreaId());

        if (area != null) {
            fullName = fullName + area.getAreaname();

            if (AreaLevel.DISTRICT.getValue() == area.getLevel()) {
                Area city = areaMService.selectByPrimaryKey(area.getParentid());
                if (city != null) {

                    fullName = city.getAreaname() + fullName;

                    Area province = areaMService.selectByPrimaryKey(city.getParentid());
                    if (province != null) {
                        fullName = province.getAreaname() + fullName;
                    }

                    dto.setAreaName(city.getAreaname());
                }
            } else if (AreaLevel.CITY.getValue() == area.getLevel()) {
                Area province = areaMService.selectByPrimaryKey(area.getParentid());
                if (province != null) {
                    fullName = province.getAreaname() + fullName;
                }

                dto.setAreaName(area.getAreaname());
            }
        }

        dto.setFullName(fullName);


    }

    @Override
    public DeviceListDTO getClassDeviceDetail(long classId, Long appUserId) {

        DeviceListDTO dto = deviceDao.getClassDevice(classId, appUserId);
        processSingleDevice(dto);
        return dto;
    }

    //处理额外的来自redis的一些数据
    private void processSingleDevice(DeviceListDTO dto) {
        //获取室内空气质量
        obtainIndoorAir(dto);

        //获取室外空气质量
        obtainOutdoorAir(dto);

        //获取连接状态
        obtainConnectStatus(dto);

        //获取设备运行状态
        obtainDevRunStatus(dto);

        //计算剩余天数
        calRemainDays(dto);

        //计算净化空气量（计算净化空气量）
        if (DeviceCategoryType.DEVICE.getCode().equals(dto.getCategoryCode())) {
            calCleanedAir(dto);
        }

        //空气复原机和新风机可以
        if (DeviceCategoryType.DEVICE.getCode().equals(dto.getCategoryCode())
                || DeviceCategoryType.NEW_WIND.getCode().equals(dto.getCategoryCode())) {
            if (ServiceTypeEnum.RETAIL.getValue() == dto.getServiceType()) {
                checkStrainerAlert(dto);
            }
        }

        calWeeklyCleanedAir(dto);

        //额外功能
        processBanner(dto);
    }

    private void processBanner(DeviceListDTO dto) {
        if (dto != null && dto.getSchoolId() != null) {
            String banner = redisService.hGet(RedisDBIndex.INDEX_1.getValue(), Constants.REDIS_APP_DEV_DETAIL_BANNER, String.valueOf(dto.getSchoolId()));

            if (StringUtils.isNotBlank(banner)) {
                try {
                    dto.setBanner(new String(banner.getBytes(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void calWeeklyCleanedAir(DeviceListDTO dto) {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(ChronoField.DAY_OF_WEEK, 1L);
        LocalDate end = now;

        DeviceDailyLifeCost cost = deviceDailyLifeCostService.sumByDevSnAndDateRange(dto.getSn(), start, end);
        if (cost == null) {
            return;
        }
        long weeklyCleanedAir = 0;

        if (DeviceCategoryType.DEVICE.getCode().equals(dto.getCategoryCode())) {
            weeklyCleanedAir = cost.getLifeCost() * Constants.CADR;
        } else if (DeviceCategoryType.NEW_WIND.getCode().equals(dto.getCategoryCode())) {
            weeklyCleanedAir = cost.getWindTimeCost();
        }
        dto.setWeeklyCleanedAir(weeklyCleanedAir);

    }

    //获取室内空气质量
    private void obtainIndoorAir(DeviceListDTO dto) {

        Map<String, Object> indoorAirMap = new HashMap<>();
        //如果serviceType=2（非个人设备），那么class:defeat:rank中获取对应sn的数据
        if (ServiceTypeEnum.INDUSTRY.getValue() == dto.getServiceType()) {

            String defeatRankStr = redisService.hGet(Constants.REDIS_CLASS_DEFEAT_RANK, dto.getSn());
            String devAirStr = redisService.lIndex(Constants.REDIS_DEV_AIR + dto.getSn(), 0);
            if (StringUtils.isNotBlank(devAirStr)) {
                ClassAirDTO devAirDTO = JSON.parseObject(devAirStr, ClassAirDTO.class);
                indoorAirMap.put(RedisDevAirKeyEnum.PM25.getName(), devAirDTO.getPm25());

                //如果是新风机，则还有二氧化碳、温度、湿度的指标
                if (DeviceCategoryType.NEW_WIND.getCode().equals(dto.getCategoryCode())) {
                    indoorAirMap.put(RedisDevAirKeyEnum.CO2.getName(), devAirDTO.getCo2());
                    indoorAirMap.put(RedisDevAirKeyEnum.HUMIDITY.getName(), devAirDTO.getHumidity());
                    indoorAirMap.put(RedisDevAirKeyEnum.TEMPERATURE.getName(), devAirDTO.getTemperature());
                }

                if (StringUtils.isNotBlank(defeatRankStr)) {
                    ClassAirDTO defRankDTO = JSON.parseObject(defeatRankStr, ClassAirDTO.class);
                    indoorAirMap.put("defeat", defRankDTO.getDefeat());
                }
            } else {
                indoorAirMap = null;
            }
        } else {
            //如果是个人设备，则从devAir:sn中获取数据
            String indoorAirStr = redisService.lIndex(Constants.REDIS_DEV_AIR + dto.getSn(), 0);
            if (StringUtils.isNotBlank(indoorAirStr)) {
                indoorAirMap = (Map<String, Object>) JSON.parse(indoorAirStr);
//                indoorAirMap.put(RedisDevAirKeyEnum.PM25.getName(),object.get(RedisDevAirKeyEnum.PM25.getName()));
//
//                //如果是新风机，则还有二氧化碳、温度、湿度的指标
//                if(DeviceCategoryType.NEW_WIND.getCode().equals(dto.getCategoryCode())) {
//                    indoorAirMap.put(RedisDevAirKeyEnum.CO2.getName(),object.get(RedisDevAirKeyEnum.CO2.getName()));
//                    indoorAirMap.put(RedisDevAirKeyEnum.HUMIDITY.getName(),object.get(RedisDevAirKeyEnum.HUMIDITY.getName()));
//                    indoorAirMap.put(RedisDevAirKeyEnum.TEMPERATURE.getName(),object.get(RedisDevAirKeyEnum.TEMPERATURE.getName()));
//                }
            } else {
                indoorAirMap = null;
            }
        }
        dto.setIndoorAirMap(indoorAirMap);
    }

    //获取室外空气质量（包含天气情况）
    private void obtainOutdoorAir(DeviceListDTO dto) {
        Map<String, Object> outdoorAirMap = new HashMap<>();

        if (dto.getAreaId() == null || dto.getAreaId() == 0) {
            return;
        }
        Long cityCode = dto.getAreaId() / 100 * 100;
        Map<String, Object> outDoorAirObj = airCityService.getAirByArea(String.valueOf(dto.getAreaId()));

        //天气改成拿本区的,区拿不到，再拿市
        Map<String, Object> weatherObj = weatherCityService.getWeatherByAreaCode(String.valueOf(dto.getAreaId()));
        if (weatherObj == null || weatherObj.isEmpty()) {
            weatherObj = weatherCityService.getWeatherByAreaCode(String.valueOf(cityCode));
        }

//        String keyDateStr = LocalDateUtil.format(LocalDate.now(), DateFormaterStyle.NO_SYMBOL.getValue());
//        String airKey = Constants.REDIS_CITY_AIR + cityCode + ":" + keyDateStr;
//        String weatherKey = Constants.REDIS_CITY_WEATHER + cityCode + ":" + keyDateStr;
//
//        String airStr = redisService.lIndex(airKey, 0);
//        String weatherStr = redisService.lIndex(weatherKey, 0);
//
//        JSONObject outDoorAirObj = (JSONObject) JSON.parse(airStr);
//        JSONObject weatherObj = (JSONObject) JSON.parse(weatherStr);

        if (outDoorAirObj == null) {
            return;
        }

        outdoorAirMap.put(RedisCityAirKeyEnum.PM25.getName(), outDoorAirObj.get(RedisCityAirKeyEnum.PM25.getName()));
        outdoorAirMap.put(RedisCityAirKeyEnum.AQI.getName(), outDoorAirObj.get(RedisCityAirKeyEnum.AQI.getName()));
        outdoorAirMap.put(RedisCityAirKeyEnum.PROVINCE.getName(), outDoorAirObj.get(RedisCityAirKeyEnum.PROVINCE.getName()));
        outdoorAirMap.put(RedisCityAirKeyEnum.CITY_NAME.getName(), outDoorAirObj.get(RedisCityAirKeyEnum.CITY_NAME.getName()));

        if (weatherObj != null) {
            outdoorAirMap.put(RedisCityWeatherKeyEnum.TEMPERATURE.getName(), weatherObj.get(RedisCityWeatherKeyEnum.TEMPERATURE.getName()));
            outdoorAirMap.put(RedisCityWeatherKeyEnum.HUMIDITY.getName(), weatherObj.get(RedisCityWeatherKeyEnum.HUMIDITY.getName()));
            outdoorAirMap.put(RedisCityWeatherKeyEnum.CODE.getName(), weatherObj.get(RedisCityWeatherKeyEnum.CODE.getName()));
        }

        dto.setOutdoorAirMap(outdoorAirMap);
    }

    //获取连接状态
    private void obtainConnectStatus(DeviceListDTO dto) {
        String clientKey = Constants.REDIS_MQTT_QCCOUNT_APP_PRE + dto.getSn();
        Map<String, Object> connStatusMap = new HashMap<>();
        connStatusMap.put("connected", redisService.hGet(clientKey, "connected"));
        dto.setConnStatusMap(connStatusMap);
    }

    //获取设备运行状态
    private void obtainDevRunStatus(DeviceListDTO dto) {
        String devKey = Constants.REDIS_DEV_SATTUS + dto.getSn();
        Map<String, Object> runStatusMap = redisService.hGetAll(devKey);
        dto.setRunStatusMap(runStatusMap);

        //童锁？
    }

    //计算剩余天数
    private void calRemainDays(DeviceListDTO dto) {
        Map<String, Object> classServiceMap = dto.getClassServiceMap();
        if (classServiceMap != null && !classServiceMap.isEmpty()) {
            Date serviceEnd = (Date) classServiceMap.get("serviceEnd");
            LocalDate endDate = LocalDateUtil.convertToLocalDate(serviceEnd);
            Long remainDays = LocalDateUtil.diffDays(LocalDate.now(), endDate);
            classServiceMap.put("remainDays", remainDays);
            classServiceMap.put("isAlert", AlertEnum.NONE.getValue());
            String remindStr = redisService.get(Constants.SCHOOL_WIND_REMIND);
            if (StringUtils.isNotBlank(remindStr)) {
                List<String> remindList = Arrays.asList(remindStr.split(","));
                if (remindList.contains(String.valueOf(remainDays))) {
                    classServiceMap.put("isAlert", AlertEnum.ALERT.getValue());
                }
            }
        }

    }

    //计算清洁空气量
    private void calCleanedAir(DeviceListDTO dto) {
        List<StrainerListDTO> strainers = dto.getStrainers();
        if (strainers != null && !strainers.isEmpty()) {
            StrainerListDTO strainerDTO = strainers.get(0);

            Integer remainLife = strainerDTO.getRemainLife();
            Integer maxLife = strainerDTO.getMaxLife();
            if (remainLife != null && maxLife != null) {
                int usedLife = maxLife - remainLife;
                dto.setCleanedAir(Constants.CADR * usedLife);
            }
        }
    }

    private void checkStrainerAlert(DeviceListDTO dto) {
        double alertRate = Double.parseDouble(redisService.get(RedisDBIndex.INDEX_2.getValue(), Constants.REDIS_STRAINER_ALERT_RATE));

        List<StrainerListDTO> strainers = dto.getStrainers();
        for (StrainerListDTO strainerDTO : strainers) {
            double strainerRate = strainerDTO.getRemainLife() * 1.0 / strainerDTO.getMaxLife();
            if (strainerRate < alertRate) {
                dto.setStrainerAlert(AlertEnum.ALERT.getValue());
                break;
            }
        }
    }

    @Transactional
    @Override
    public void changeStrainer(String userToken, String deviceSn, String newStrainerSn, String strainerModelCode) throws Exception {

        logger.info("设备sn=" + deviceSn + "开始更换滤网...新的滤网sn=" + newStrainerSn);

        AppUser appUser = null;

        //1.检查要更换的滤网类型是否正确
        checkSn(newStrainerSn, strainerModelCode);

        //2.查询设备是否存在
        Device device = checkDevice(deviceSn);

        //3.校验设备是否属于该用户（个人设备会传token，所以只有个人设备需要校验）
        if (StringUtils.isNotBlank(userToken)) {
            appUser = checkUserBelong(userToken, device);
        }

        //4.查询滤网是否存在
        DeviceStrainer strainer = checkStrainer(newStrainerSn);

        //5.查询设备是否在线
        checkIsOnline(device);

        //6.处理设备要更换的旧滤网
        processOldStrainer(device.getDeviceId(), strainerModelCode);

        //7.处理新滤网
        processNewStrainer(device.getDeviceId(), strainer);

        //8.处理设备更换次数
        processDeviceChangeTimes(device);

        //9.发送MQTT报文重置设备滤网计时
        recoveryToMaxLife(device, strainerModelCode);

        //10 清零滤网提醒level
        if (appUser != null) {
            processClearPushLevel(appUser.getId(), device.getDeviceId());
        }
    }

    private void processClearPushLevel(Long appUserId, Long deviceId) {
        appRegPushMarkService.clearLevelByAppUserDeviceAndPushType(appUserId, deviceId, PushTypeEnum.STRAINER_LIFE_ALERT.getValue());
    }

    private AppUser checkUserBelong(String userToken, Device device) {
        AppUser appUser = appUserManageService.selectByUserToken(userToken);
        if (appUser == null) {
            throw new BusinessException("用户不存在！");
        }

        UserDeviceRelation relation = userDeviceRelationService.selectByUserIdAndDeviceId(appUser.getId(), device.getDeviceId());
        if (relation == null) {
            throw new BusinessException("该用户没有该设备的授权！");
        }

        return appUser;
    }

    private void checkSn(String newStrainerSn, String strainerModelCode) {
        if (StringUtils.isBlank(newStrainerSn)) {
            throw new BusinessException("您扫描了错误的二维码！");
        }

        String modelCode = SnUtil.getModelCode(newStrainerSn);

        if (!modelCode.equals(strainerModelCode)) {
            throw new BusinessException("您扫描的滤网类型不正确！");
        }

        String categoryCode = SnUtil.getCategoryCode(newStrainerSn);

        if (!DeviceCategoryType.STRAINER.getCode().equals(categoryCode)) {
            throw new BusinessException("您扫描了错误的二维码！");
        }

    }


    private Device checkDevice(String deviceSn) {
        Device selDevice = new Device();
        selDevice.setSn(deviceSn);
        Device device = deviceDao.selectOne(selDevice);
        if (device == null) {
            throw new BusinessException("您扫描了错误的二维码！");
        }
        return device;
    }

    private DeviceStrainer checkStrainer(String strainerSn) {
        DeviceStrainer selStrainer = new DeviceStrainer();
        selStrainer.setSn(strainerSn);

        DeviceStrainer strainer = deviceStrainerService.selectOne(selStrainer);
        if (strainer == null) {
            throw new BusinessException("您扫描了错误的二维码！");
        }
        if (DeviceSaleStatusEnum.SALED.getValue() != strainer.getIsSaled()) {
            throw new BusinessException("您扫描了错误的二维码！");
        }
        if (DeviceBindEnum.BINDED.getValue() == strainer.getIsBind()
                || strainer.getDeviceId() != null) {
            throw new BusinessException("该滤网已经绑定了其它设备！");
        }
        if (DeviceExpireEnum.EXPIRED.getValue() == strainer.getIsExpire()) {
            throw new BusinessException("该滤网已过期！");
        }

        return strainer;
    }

    private void checkIsOnline(Device device) {
        String connStr = redisService.hGet(Constants.REDIS_MQTT_QCCOUNT_APP_PRE + device.getSn(), Constants.REDIS_CLIENT_CONN);
        if (!StringUtils.isNotBlank(connStr) || !Boolean.parseBoolean(connStr)) {
            throw new BusinessException("设备处于离线状态！");
        }
    }

    /**
     * 旧滤网处理
     * 1.设置旧设备ID
     * 2.设备ID置空
     * 3.设置已过期
     * 4.设置更换时间
     */
    private void processOldStrainer(Long deviceId, String strainerModelCode) {
        DeviceModel model = deviceModelService.findByFullCode(DeviceCategoryType.STRAINER.getCode() + strainerModelCode);

        if (model == null) {
            throw new BusinessException("滤网类型不存在：strainerModelCode=" + strainerModelCode);
        }

        //不用deviceId和modelCode做联合查询，是因为这个步骤涉及表的行级锁和表级锁的问题，只用deviceId,只锁行，用modelcode联合查，会锁表
        List<DeviceStrainer> strainers = deviceStrainerService.selectByDeviceIdForUpdate(deviceId);

        if (strainers != null && !strainers.isEmpty()) {
            for (DeviceStrainer oldStrainer : strainers) {
                if (oldStrainer.getDeviceModelId().longValue() == model.getModelId().longValue()) {
                    logger.info("查询到了设备deviceId=" + deviceId + "要更换的旧滤网，旧滤网sn=" + oldStrainer.getSn());
                    oldStrainer.setOldDeviceId(oldStrainer.getDeviceId());
                    oldStrainer.setDeviceId(null);
                    oldStrainer.setIsExpire(DeviceExpireEnum.EXPIRED.getValue());
                    oldStrainer.setReplacedTime(new Date());
                    deviceStrainerService.updateByPrimaryKey(oldStrainer);
                }
            }
        }
    }

    /**
     * 新滤网处理
     * 1.设置设备ID
     * 2.设置已绑定
     */
    private void processNewStrainer(Long deviceId, DeviceStrainer strainer) {
        strainer.setDeviceId(deviceId);
        strainer.setIsBind(DeviceBindEnum.BINDED.getValue());
        deviceStrainerService.updateByPrimaryKey(strainer);
    }

    /**
     * 设备更换滤网次数和时间的处理
     * 1.更新更换次数
     * 2.更新更换时间
     */
    private void processDeviceChangeTimes(Device device) {
        device.setChangeTimes(device.getChangeTimes() + 1);
        device.setStrainerChangeTime(new Date());
        deviceDao.updateByPrimaryKey(device);
    }

    @Override
    public void recoveryToMaxLife(Device device, String strainerModelCode) throws Exception {

        int ctrlType = 0;
        String strainerKeyName = null;
        String windTimeCostKeyName = null;
        String fullStrainerModelCode = DeviceCategoryType.STRAINER.getCode() + strainerModelCode;

        if (DeviceModelType.JF_LW.getCode().equals(fullStrainerModelCode)) {
            ctrlType = Constants.CMD_CONTROL_UNLOCK_NEWSTRAINER;
            strainerKeyName = RedisDevKeyFiledEnum.FILTER_REMAINDER_LIFE.getName();
        }

        if (DeviceModelType.LOWER_STRAINER.getCode().equals(fullStrainerModelCode)) {
            ctrlType = Constants.MAX_LOWER_STRAINER;
            strainerKeyName = RedisDevKeyFiledEnum.LOWER_STRAINER_LIFE.getName();
        }

        if (DeviceModelType.MEDIUM_STRAINER.getCode().equals(fullStrainerModelCode)) {
            ctrlType = Constants.MAX_MEDIUM_STRAINER;
            strainerKeyName = RedisDevKeyFiledEnum.MEDIUM_STRAINER_LIFE.getName();
            windTimeCostKeyName = RedisDevKeyFiledEnum.MS_WIND_TIME_COST.getName();
        }

        if (DeviceModelType.HIGHER_STRAINER.getCode().equals(fullStrainerModelCode)) {
            ctrlType = Constants.MAX_HIGHER_STRAINER;
            strainerKeyName = RedisDevKeyFiledEnum.HIGHER_STRAINER_LIFE.getName();
            windTimeCostKeyName = RedisDevKeyFiledEnum.HS_WIND_TIME_COST.getName();
        }

        if (DeviceModelType.CARBON_STRAINER.getCode().equals(fullStrainerModelCode)) {
            ctrlType = Constants.MAX_CARBON_STRAINER;
            strainerKeyName = RedisDevKeyFiledEnum.CARBON_STRAINER_LIFE.getName();
        }

        if (DeviceModelType.UV_LIGHT.getCode().equals(fullStrainerModelCode)) {
            ctrlType = Constants.MAX_UV_LIGHT;
            strainerKeyName = RedisDevKeyFiledEnum.UV_LIFE.getName();
        }

        clearCost(device.getSn(), strainerKeyName, windTimeCostKeyName, fullStrainerModelCode);

        if (ctrlType != 0) {
            if (ctrlType == Constants.CMD_CONTROL_UNLOCK_NEWSTRAINER) {
                MqttUtils.pubCtrlDevPayload(Constants.CMD_CONTROL_LOCK, device.getMqttClientId(), QosEnum.EXACTLY_ONCE.getValue());
                logger.info("sn=" + device.getSn() + "的飓风设备更换滤网，锁定设备指令完毕！");
            }
            Thread.sleep(2000);
            MqttUtils.pubCtrlDevPayload(ctrlType, device.getMqttClientId(), QosEnum.EXACTLY_ONCE.getValue());
            logger.info("sn=" + device.getSn() + "的设备更换滤网，发送恢复滤网最大寿命指令完毕！");
        }

    }

    /**
     * @param deviceSn              ——设备sn
     * @param strainerKeyName       ——滤网型号的key的名称（在redis中的key名称）
     * @param windTimeCostKeyName   ——该滤网的风量*时长的key的名称（在redis中的key名称）
     * @param fullStrainerModelCode
     */
    private void clearCost(String deviceSn, String strainerKeyName, String windTimeCostKeyName, String fullStrainerModelCode) {
        Map<String, Object> devMap = new HashMap<>();
        DeviceModel strainerModel = deviceModelService.findByFullCode(fullStrainerModelCode);
        devMap.put(strainerKeyName, strainerModel.getMaxLife());
        if (StringUtils.isNotBlank(windTimeCostKeyName)) {
            devMap.put(windTimeCostKeyName, 0);
        }
        redisService.hMSet(Constants.REDIS_DEV_SATTUS + deviceSn, devMap, null);
    }

    @Override
    public PageList<WebActivedDevListDTO> listActivedDevice(long categoryId, Long companyId, String deviceSn, long start, int limit) {

        PageList<WebActivedDevListDTO> pageList = new PageList<>();
        List<WebActivedDevListDTO> dtos = deviceDao.listActivedDevice(categoryId, companyId, deviceSn, start, limit);
        long count = deviceDao.countActivedDevice(categoryId, companyId, deviceSn);

        pageList.setContent(dtos);
        pageList.setCount(count);
        return pageList;
    }

    @Transactional
    @Override
    public void scanPage(Long appUserId, String deviceSn, String pageId) throws Exception {

        Date now = new Date();

        AppUser appUser = appUserManageService.selectByPrimaryKey(appUserId);

        //检查设备是否存在
        Device device = checkDevice(deviceSn);

        //检查该设备是否属于该用户
        checkUserDeviceRel(appUserId, device.getDeviceId());

        //排查pageId是否合法
        checkPageId(pageId, appUserId, device.getDeviceId(), now);

        //处理绑定记录
        processUserPage(appUserId, pageId, device, now);

        //创建大屏帐号跟设备的绑定关系
        createBgAccountBindRel(appUser.getPhone(), device.getSn());
    }


    private void checkUserDeviceRel(Long appUserId, Long deviceId) {
        if (!userDeviceRelationService.checkHasBind(appUserId, deviceId)) {
            throw new BusinessException("您没有该设备的授权！");
        }
    }

    private void checkPageId(String pageId, long appUserId, long deviceId, Date now) {
        String keyDateStr = DateFormatUtils.format(now, DateFormaterStyle.NO_SYMBOL.getValue());
        String key = ScreenEnum.REDIS_PAGE_POP_ID_PRE.getValue() + keyDateStr;


        Boolean isMember = redisService.sIsMember(key, pageId);
        if (isMember == null || !isMember.booleanValue()) {
            logger.error("非法或者已经失效的pageId=" + pageId + ",时间：" + keyDateStr);
            throw new BusinessException("您扫描的页面不合法或者页面已经失效！");
        }

        AppUserPage selUserPage = new AppUserPage();
        selUserPage.setPageId(pageId);
        AppUserPage appUserPage = appUserPageDao.selectOne(selUserPage);
        if (appUserPage != null) {
            if (appUserPage.getAppUserId().longValue() != appUserId
                    || appUserPage.getDeviceId().longValue() != deviceId) {
                throw new BusinessException("您扫描的页面已经被他人使用！");
            }
        }
    }

    private void processUserPage(Long appUserId, String pageId, Device device, Date now) {
        AppUserPage newUserPage = new AppUserPage();
        newUserPage.setAppUserId(appUserId);
        newUserPage.setDeviceId(device.getDeviceId());
        AppUserPage dbUserPage = appUserPageDao.selectOne(newUserPage);

        if (dbUserPage == null) {
            newUserPage.setPageId(pageId);
            newUserPage.setBindTime(now);
            appUserPageDao.insert(newUserPage);
        } else {
            dbUserPage.setPageId(pageId);
            dbUserPage.setBindTime(now);
            appUserPageDao.updateByPrimaryKey(dbUserPage);
        }
    }

    private void createBgAccountBindRel(String appUserPhone, String deviceSn) throws Exception {
        String account = Constants.REDIS_MQTT_WS + "_" + appUserPhone + "_" + deviceSn;
        String bindKey = Constants.REDIS_APP_DEV_REL + account;

        AppDev appdev = new AppDev();
        appdev.setAccount(account);
        appdev.setDevSN(deviceSn);
        appdev.setIsMgr(true);
        String bindRelValue = JSONObject.toJSONString(appdev);

        //不管有没有，先删除再添加
        redisService.sRem(bindKey, bindRelValue);
//        MqttUtils.pubDelDevPayload(deviceSn,appdev.getAccount());

        //再添加ture的
        redisService.sAdd(bindKey, bindRelValue);
        MqttUtils.pubAddDevPayload(Constants.MANAGER_STATU_TRUE, deviceSn, appdev.getAccount());
    }

    @Override
    public List<ClassDeviceDTO> getAllClassDevices() {
        return deviceDao.getAllClassDevices();
    }

    @Override
    public List<WebDevDistributeDTO> listDeviceDistribute(Long dealerId, String categoryCode) {

        List<WebDevDistributeDTO> dtos = deviceDao.listDeviceDistribute(dealerId, categoryCode);

        for (WebDevDistributeDTO dto : dtos) {

            if (dto.getIsConnect() != null && DeviceConnectEnum.CONNECT.getValue() == dto.getIsConnect()) {
                String runStr = redisService.hGet(Constants.REDIS_DEV_SATTUS + dto.getSn(), RedisDevKeyFiledEnum.IS_RUNNING.getName());
                if (StringUtils.isNotBlank(runStr)) {
                    boolean isRun = Boolean.parseBoolean(runStr);
                    if (isRun) {
                        dto.setIsRun(DeviceOpenEnum.ON.getValue());
                    } else {
                        dto.setIsRun(DeviceOpenEnum.OFF.getValue());
                    }
                } else {
                    logger.error("在线的设备，没有运行状态；sn=" + dto.getSn());
                }

            }
        }
        return dtos;
    }

//    @Transactional(isolation = Isolation.READ_COMMITTED)
//    @Override
//    public Result refreshLocation(String deviceSn) throws Exception {
//
//        Result result = new Result();
//
//        if(StringUtils.isBlank(deviceSn)){
//            throw new BusinessException("设备sn不能为空!");
//        }
//
//        Device selDev = new Device();
//        selDev.setSn(deviceSn);
//        Device device = deviceDao.selectOne(selDev);
//        if(device==null){
//            throw new BusinessException("设备sn="+deviceSn+"的设备不存在!");
//        }
//
//        int refreshLimit = Integer.parseInt(redisService.get(RedisDBIndex.INDEX_2.getValue(),Constants.REDIS_LOCATION_REFRESH_LIMIT));
//
//        Date now = new Date();
//
//        DevLocationRefresh locationRefresh = devLocationRefreshDao.selectBySnForUpdate(deviceSn);
//        if(locationRefresh==null){
//            DevLocationRefresh newRefresh = new DevLocationRefresh();
//            newRefresh.setDeviceSn(deviceSn);
//            newRefresh.setRefreshCount(1);
//            newRefresh.setLastRefreshTime(now);
//            devLocationRefreshDao.insert(newRefresh);
//            MqttUtils.pubGeoDevPayload(deviceSn);
//        }else {
//            boolean isOver = false;
//
//            if(locationRefresh.getRefreshCount()<refreshLimit){
//                MqttUtils.pubGeoDevPayload(deviceSn);
//            }else {
//                isOver = true;
//            }
//
//            locationRefresh.setRefreshCount(locationRefresh.getRefreshCount()+1);
//            locationRefresh.setLastRefreshTime(now);
//            devLocationRefreshDao.updateByPrimaryKey(locationRefresh);
//
//            if(isOver){
//                result.setCode(ProtocolUtils.RESP_CODE_FAIL);
//                result.setMsg("自动刷新次数已经超过"+refreshLimit+"次上限");
//                return result;
//            }
//        }
//
//        int catchDelay = Integer.parseInt(redisService.get(RedisDBIndex.INDEX_2.getValue(),Constants.REDIS_TIMER_DEV_LOC_CATCH_DELAY));
//        Thread.sleep(catchDelay);
//
//        Device dev = deviceDao.selectOne(selDev);
//        if(dev.getAreaId()==null){
//            result.setCode(ProtocolUtils.RESP_CODE_FAIL);
//            result.setMsg("未获取到地理位置信息!");
//            return result;
//        }
//
//        result.setCode(ProtocolUtils.RESP_CODE_SUCCESS);
//        result.setMsg("获取设备地理位置信息成功！");
//        return result;
//    }


    @Override
    public Result refreshLocation(String deviceSn) throws Exception {

        Result result = new Result();

        if (StringUtils.isBlank(deviceSn)) {
            throw new BusinessException("设备sn不能为空!");
        }

        Device selDev = new Device();
        selDev.setSn(deviceSn);
        Device device = deviceDao.selectOne(selDev);
        if (device == null) {
            throw new BusinessException("设备sn=" + deviceSn + "的设备不存在!");
        }

        MqttUtils.pubGeoDevPayload(deviceSn);

        int catchDelay = Integer.parseInt(redisService.get(RedisDBIndex.INDEX_2.getValue(), Constants.REDIS_TIMER_DEV_LOC_CATCH_DELAY));
        Thread.sleep(catchDelay);

        Device dev = deviceDao.selectOne(selDev);
        if (dev.getAreaId() == null) {
            result.setCode(ProtocolUtils.RESP_CODE_FAIL);
            result.setMsg("未获取到地理位置信息!");
            return result;
        }

        result.setCode(ProtocolUtils.RESP_CODE_SUCCESS);
        result.setMsg("获取设备地理位置信息成功！");
        return result;
    }


    @Override
    public boolean hasLocation(String devSn) {
        Device selDev = new Device();
        selDev.setSn(devSn);
        Device device = deviceDao.selectOne(selDev);
        if (device != null && device.getAreaId() != null) {
            return true;
        }

        return false;
    }

    @Override
    public List<Device> selectSpecifyOnLineDev(Set<String> unCatchedSns) {

        return deviceDao.selectSpecifyOnLineDev(unCatchedSns);
    }

    @Override
    public List<Device> selectBySnsAndCatchState(Set<String> sns, int catchState) {
        if (sns == null || sns.isEmpty()) {
            return null;
        }
        return deviceDao.selectBySnsAndCatchState(sns, catchState);
    }

    @Override
    public List<Map<String, Object>> getZeroRemainLifes() {
        return deviceDao.getZeroRemainLifes();
    }

    @Override
    public PageList<DeviceRelUserDTO> listDeviceUsers(Long companyId, Long dealerId, String appUserPhone, Integer osType, int remainLifeSort, long start, int limit) {
        PageList<DeviceRelUserDTO> pageList = new PageList<>();
        List<DeviceRelUserDTO> content = deviceDao.listDeviceUsers(companyId, dealerId, appUserPhone, osType, remainLifeSort, start, limit);

        long count = deviceDao.countDeviceUsers(companyId, dealerId, appUserPhone, osType, remainLifeSort);

        pageList.setContent(content);
        pageList.setCount(count);

        return pageList;
    }

    @Override
    public Device selectBatchFirstDevice(Long batchId) {
        return deviceDao.selectBatchFirstDevice(batchId);
    }

    @Transactional
    @Override
    public void batchInsert(List<Device> deviceList) {
        deviceDao.batchInsert(deviceList);
    }

    @Override
    public Device selectBySn(String devSn) {
        Device device = new Device();
        device.setSn(devSn);
        return deviceDao.selectOne(device);
    }

    @Override
    public List<Device> selectSchoolOptionalMainDevice(Long schoolId, int deviceOrder) {
        return deviceDao.selectSchoolOptionalMainDevice(schoolId, deviceOrder);
    }

    @Override
    public List<DevCountDistDTO> countDeviceForCityLevel(Long areaId) {
        return deviceDao.countDeviceForCityLevel(areaId);
    }

    @Override
    public List<DevCountDistDTO> countDeviceForNationLevel(Long areaId) {
        return deviceDao.countDeviceForNationLevel(areaId);
    }

    @Override
    public List<Device> findDeviceByInitStateInFactoryAndRange(String factoryCode, int initState, String snStart, String snEnd) {
        return deviceDao.findDeviceByInitStateInFactoryAndRange(factoryCode, initState, snStart, snEnd);
    }

    @Override
    public List<Device> findDeviceByInitStateInFactoryAndModel(String factoryCode, Long modelId, int initState) {
        return deviceDao.findDeviceByInitStateInFactoryAndModel(factoryCode, modelId, initState);
    }

    @Override
    public void recycleSn(LocalDateTime initTime) {
        deviceDao.recycleSn(initTime);
    }

    @Transactional
    @Override
    public void activeDevice(Long deviceId, Long deviceBatchId) {
        DeviceBatch deviceBatch = deviceBatchService.selectByPrimaryKey(deviceBatchId);
        Dealer dealer = dealerService.selectByCompanyAndLevel(deviceBatch.getCompanyId(), DealerLevelEnum.LEVEL_1.getValue());

        Device device = deviceDao.selectByPrimaryKey(deviceId);
        device.setDeviceId(deviceId);
        device.setIsActive(DeviceActiveEnum.ACTIVED.getValue());
        device.setIsSaled(DeviceSaleStatusEnum.SALED.getValue());
        device.setAreaId(null);
        device.setLatitude(null);
        device.setLongitude(null);
        device.setFullAddress(null);
        device.setDealerId(dealer.getDealerId());
        device.setServiceType(ServiceTypeEnum.RETAIL.getValue());
        deviceDao.updateByPrimaryKey(device);
    }

    @Override
    public void clearAddress(long deviceId) {
        deviceDao.clearAddress(deviceId);
    }

    @Override
    public List<Device> selectBySchool(Long schoolId) {
        Device device = new Device();
        device.setSchoolId(schoolId);
        return deviceDao.select(device);
    }

    @Override
    public List<Device> checkActive(List<Device> devList) {

        List<Device> unActiveList = deviceDao.checkActive(devList);
        return unActiveList;
    }

    @Override
    public void clearFactoryAddress(long deviceBatchId) {
        deviceDao.clearFactoryAddress(deviceBatchId);
    }

    @Override
    public List<Device> selectByClassId(Long classId) {
        Device device = new Device();
        device.setClassId(classId);
        return deviceDao.select(device);
    }

    @Override
    public List<Device> selectActivedDevice() {
        Device device = new Device();
        device.setIsActive(DeviceActiveEnum.ACTIVED.getValue());
        return deviceDao.select(device);
    }

    @Override
    public List<Device> selectActivedAndOnlineDevice() {
        Device device = new Device();
        device.setIsActive(DeviceActiveEnum.ACTIVED.getValue());
        device.setIsConnect(DeviceConnectEnum.CONNECT.getValue());
        return deviceDao.select(device);
    }

    @Override
    public String getDeviceCode(String sn) throws Exception {
        if (StringUtils.isBlank(sn)) {
            throw new BusinessException("设备sn不能为空");
        }

        DeviceCodeBean deviceCodeBean = new DeviceCodeBean();

        String snToCodeKey = Constants.REDIS_DEV_SN_TO_CODE + sn;
        String devCode = redisService.get(RedisDBIndex.INDEX_1.getValue(), snToCodeKey);

        deviceCodeBean.setDeviceCode(devCode);

        if (StringUtils.isBlank(devCode)) {

            logger.info("snToCode is not exist,will create...");

            //根据sn找不到devCode的话，则创建sn到code的映射
            devCode = redisService.sPop(RedisDBIndex.INDEX_1.getValue(), Constants.REDIS_DEV_CODE_POOL);
            deviceCodeBean.setDeviceCode(devCode);
            long keyExpire = 86400L;//snToCodeKey的过期时间是86400s，就是一天

            redisLockService.doLock(RedisDBIndex.INDEX_1.getValue(), snToCodeKey, devCode, keyExpire, new RedisLockBusBean() {
                @Override
                public Result doInLock() throws Exception {
                    //sn到code的映射创建成功后，再建立code到sn的映射，这样才能根据devCode找到对应的sn
                    String codeToSnKey = Constants.REDIS_DEV_CODE_TO_SN + deviceCodeBean.getDeviceCode();
                    redisService.set(RedisDBIndex.INDEX_1.getValue(), codeToSnKey, sn);
                    redisService.expire(RedisDBIndex.INDEX_1.getValue(), codeToSnKey, keyExpire);

                    logger.info("successfully create snToCode,device code=" + deviceCodeBean.getDeviceCode());

                    return ResponseUtil.setDefaultSuccessResponse();
                }

                @Override
                public void doInCrash(String currentValue) throws Exception {

                    String poolDevCode = deviceCodeBean.getDeviceCode();
                    logger.info("create snToCode crashed,fetch code=" + poolDevCode + " from pool,but snToCode has created value=" + currentValue);

                    //冲突的情况下，返回实际devCodeKey的value，并且把从pool中拿到的devCode返还给pool
                    redisService.sAdd(RedisDBIndex.INDEX_1.getValue(), Constants.REDIS_DEV_CODE_POOL, poolDevCode);
                    deviceCodeBean.setDeviceCode(currentValue);

                    logger.info("return code=" + poolDevCode + " to pool");

                }
            });
        }

        logger.info("return deviceCode=" + deviceCodeBean.getDeviceCode());
        return deviceCodeBean.getDeviceCode();
    }

    @Override
    public List<Device> selectByCategoryCode(String categoryCode) {
        return deviceDao.selectByCategoryCode(categoryCode);
    }

    //    @Override
//    public void relDeviceWithStrainer(Device device, DeviceStrainer strainer) {
//        deviceDao.updateWithStrainerId(device.getDeviceId(),strainer.getStrainerId());
//        deviceDao.updateStrainerWithDevice(device.getDeviceId(),strainer.getStrainerId());
//    }
//
//    @Override
//    public List<Device> selectByCondition() {
//        return deviceDao.selectByCondition();
//    }
}
