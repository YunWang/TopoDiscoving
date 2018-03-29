package cn.harmonycloud.bean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wangyun on 3/27/18.
 */
public class Switch implements Device{
    private final String ip;
    private final String type;
    private String mac; //mac地址
    //该交换机上的ARP表;{mac:ip}
    private HashMap<String,String> arpTable = new HashMap<String, String>();
    //该交换机上的MAC表;{mac:port}
    private HashMap<String,String> macTable = new HashMap<String, String>();
    /**
     * 与该交换机相连的设备
     */
    private HashMap<Device,String> connectedDevices = new HashMap<Device, String>();
    /**
     * 该交换机上端口对应的设备
     */
    private HashMap<String,List<Device>> portedDevices = new HashMap<String, List<Device>>();
    //与此相连的交换机;{Switch:port},Switch是相连的switch,port是该交换机上与switch相连的端口
    private HashMap<Switch,String> connectedSwitches = new HashMap<Switch, String>();
    //与此交换机相连的主机;{Computer:port},Computer是主机,port是交换机上的端口
    private HashMap<Computer,String> connectedComputers = new HashMap<Computer, String>();
    //该交换机端口下的交换机;{port:[Switch1,Switch2,……]},port是该交换机的端口,[Switch1,Switch2,……]是与该端口相连的交换机列表
    private HashMap<String,List<Switch>> portedSwitches = new HashMap<String, List<Switch>>();
    //该交换机端口下的主机;{port:[Computer1,Computer2,……]},port是该交换机的端口,[Computer1,Computer2,……]是与该端口相连的主机列表
    private HashMap<String,List<Computer>> portedComputers = new HashMap<String, List<Computer>>();

    //多可选参数的构造器
    public static class SwitchBuilder{
        //必需的参数
        private final String ip;
        private final String type;

        //可选的参数
        private String mac;
        private HashMap<String,String> arpTable = new HashMap<String, String>();
        private HashMap<String,String> macTable = new HashMap<String, String>();
        private HashMap<Device,String> connectedDevices = null;
        private HashMap<String,List<Device>> portedDevices = null;
        private HashMap<Switch,String> connectedSwitches = new HashMap<Switch, String>();
        private HashMap<Computer,String> connectedComputers = new HashMap<Computer, String>();
        private HashMap<String,List<Switch>> portedSwitches = new HashMap<String, List<Switch>>();
        private HashMap<String,List<Computer>> portedComputers = new HashMap<String, List<Computer>>();

        //构造器的构造函数
        public SwitchBuilder(String ip,String type){
            this.ip = ip;
            this.type = type;
        }

        public SwitchBuilder mac(String mac){
            this.mac = mac;
            return this;
        }
        public SwitchBuilder arpTable(HashMap<String,String> arpTable){
            this.arpTable = arpTable;
            return this;
        }
        public SwitchBuilder macTable(HashMap<String,String> macTable){
            this.macTable = macTable;
            return this;
        }
        public SwitchBuilder connectedSwitches(HashMap<Switch,String> connectedSwitches){
            this.connectedSwitches = connectedSwitches;
            return this;
        }
        public SwitchBuilder connectedComputers(HashMap<Computer,String> connectedComputers){
            this.connectedComputers = connectedComputers;
            return this;
        }
        public SwitchBuilder portedSwitches(HashMap<String,List<Switch>> portedSwitches){
            this.portedSwitches = portedSwitches;
            return this;
        }
        public SwitchBuilder portedComputers(HashMap<String,List<Computer>> portedComputers){
            this.portedComputers = portedComputers;
            return this;
        }
        public SwitchBuilder connectedDevices(HashMap<Device,String> connectedDevices){
            this.connectedDevices = connectedDevices;
            return this;
        }
        public SwitchBuilder portedDevices(HashMap<String,List<Device>> portedDevices){
            this.portedDevices = portedDevices;
            return this;
        }

        public Switch build(){
            return new Switch(this);
        }
    }

    //构造函数
    private Switch(SwitchBuilder switchBuilder){
        ip = switchBuilder.ip;
        type = switchBuilder.type;
        mac = switchBuilder.mac;
        arpTable = switchBuilder.arpTable;
        macTable = switchBuilder.macTable;
        connectedSwitches = switchBuilder.connectedSwitches;
        connectedComputers = switchBuilder.connectedComputers;
        portedSwitches = switchBuilder.portedSwitches;
        portedComputers = switchBuilder.portedComputers;
        connectedDevices = switchBuilder.connectedDevices;
        portedDevices = switchBuilder.portedDevices;
    }
    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public HashMap<String, String> getArpTable() {
        return arpTable;
    }

    public void setArpTable(HashMap<String, String> arpTable) {
        this.arpTable = arpTable;
    }

    public HashMap<String, String> getMacTable() {
        return macTable;
    }

    public void setMacTable(HashMap<String, String> macTable) {
        this.macTable = macTable;
    }

    public HashMap<Switch, String> getConnectedSwitches() {
        return connectedSwitches;
    }

    public void setConnectedSwitches(HashMap<Switch, String> connectedSwitches) {
        this.connectedSwitches = connectedSwitches;
    }

    public HashMap<Computer, String> getConnectedComputers() {
        return connectedComputers;
    }

    public void setConnectedComputers(HashMap<Computer, String> connectedComputers) {
        this.connectedComputers = connectedComputers;
    }

    public HashMap<String, List<Switch>> getPortedSwitches() {
        return portedSwitches;
    }

    public void setPortedSwitches(HashMap<String, List<Switch>> portedSwitches) {
        this.portedSwitches = portedSwitches;
    }

    public HashMap<String, List<Computer>> getPortedComputers() {
        return portedComputers;
    }

    public void setPortedComputers(HashMap<String, List<Computer>> portedComputers) {
        this.portedComputers = portedComputers;
    }

    public HashMap<Device, String> getConnectedDevices() {
        return connectedDevices;
    }

    public void setConnectedDevices(HashMap<Device, String> connectedDevices) {
        this.connectedDevices = connectedDevices;
    }

    public HashMap<String, List<Device>> getPortedDevices() {
        return portedDevices;
    }

    public void setPortedDevices(HashMap<String, List<Device>> portedDevices) {
        this.portedDevices = portedDevices;
    }
}
