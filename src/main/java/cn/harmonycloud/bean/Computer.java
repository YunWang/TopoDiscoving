package cn.harmonycloud.bean;

/**
 * Created by wangyun on 3/27/18.
 */
public class Computer implements Device{
    private final String ip;
    private final String mac; //mac地址
    private final String type; //类型
    private Switch aSwitch;

    //构造函数
    public Computer(String ip,String mac,String type){
        this.ip = ip;
        this.mac = mac;
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public String getIp() {
        return ip;
    }

    public Switch getSwitch() {
        return aSwitch;
    }

    public void setSwitch(Switch aSwitch) {
        this.aSwitch = aSwitch;
    }

    public String getType() {
        return type;
    }


}
