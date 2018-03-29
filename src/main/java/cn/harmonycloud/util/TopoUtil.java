package cn.harmonycloud.util;

import cn.harmonycloud.bean.Computer;
import cn.harmonycloud.bean.Device;
import cn.harmonycloud.bean.Switch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wangyun on 3/28/18.
 */
public class TopoUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(TopoUtil.class);

    /**
     * 从classType类型的list中获取特定ip的Device
     * @param ip
     * @param list
     * @return
     */
    public static Device getDeviceByIp(String ip, List<Device> list){
        if (list == null){
            //此处应该抛出异常
            LOGGER.debug("Device list is null!");
            return null;
        }
        for (Device device : list){
            if (device.getIp().equals(ip)){
                return device;
            }
        }
        return null;
    }
}
