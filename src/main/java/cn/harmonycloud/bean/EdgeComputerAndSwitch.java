package cn.harmonycloud.bean;

/**
 * Created by wangyun on 3/27/18.
 * 交换机与主机之间的边
 */
public class EdgeComputerAndSwitch implements Edge{
    private String computerIp;
    private String switchIp;
    private String switchPort;

    public EdgeComputerAndSwitch(){}

    public EdgeComputerAndSwitch(String computerIp,String switchIp,String switchPort){
        this.computerIp = computerIp;
        this.switchIp = switchIp;
        this.switchPort = switchPort;
    }

    public String getComputerIp() {
        return computerIp;
    }

    public void setComputerIp(String computerIp) {
        this.computerIp = computerIp;
    }

    public String getSwitchIp() {
        return switchIp;
    }

    public void setSwitchIp(String switchIp) {
        this.switchIp = switchIp;
    }

    public String getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(String switchPort) {
        this.switchPort = switchPort;
    }

    public String getNode1() {
        return switchIp;
    }

    public String getNode2() {
        return computerIp;
    }
}
