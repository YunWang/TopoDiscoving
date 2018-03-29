package cn.harmonycloud.topodiscoving;

import cn.harmonycloud.bean.*;
import cn.harmonycloud.constant.TopoConstant;
import cn.harmonycloud.util.SnmpUtil;
import cn.harmonycloud.util.TopoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangyun on 3/28/18.
 */
public class MyTopoDiscovingStrategy implements TopoDiscovingStrategy{

    private final static Logger LOGGER = LoggerFactory.getLogger(MyTopoDiscovingStrategy.class);

    /**
     * 存储arp表,{mac:ip}
     */
    private HashMap<String,String> arpTable = new HashMap<String, String>();
    /**
     * 存储mac地址表,{switchIp:{mac:port}}
     */
    private HashMap<String,HashMap<String,String>> macTable = new HashMap<String, HashMap<String, String>>();
    /**
     * 存储给定的switch的ip,认为局域网上就只有这些switch
     */
    private List<String> switchesIp = new ArrayList<String>();

    private SwitchTopo topo = new SwitchTopo();

    public MyTopoDiscovingStrategy(List<String> switchesIp){
        this.switchesIp = switchesIp;
    }

    public boolean enterSwitchesIp(){
        //从配置文件读取SwitchIp以及对应的community

        return true;
    }

    /**
     * 基于SNMP协议获取交换机上的ARP表和MAC表
     * @return
     */
    public boolean getSwitchData() {
        if (switchesIp == null){
            LOGGER.error("SwitchesIp is empty,maybe they have not been entered!");
            return false;
        }
        arpTable = SnmpUtil.getArpTable(switchesIp);
        if (arpTable == null){
            LOGGER.error("ArpTable is null,it seems we get arp table failed!");
            return false;
        }
        macTable = SnmpUtil.getMacTable(switchesIp);
        if (macTable == null){
            LOGGER.error("MacTable is null, it seems we get mac table failed!");
        }
        return true;
    }

    /**
     * 加工初始信息,完善交换机信息
     * 加载topo结构中的结点
     * 加载交换机信息，包括macTable、connectedDevices、portedDevices、portedSwitches、portedComputers
     * @return
     */
    public boolean processData() {
        //topo结构中交换机节点创建
        if (switchesIp == null){
            LOGGER.error("SwitchesIp is empty,maybe they have not been entered!");
            return false;
        }
        //构建topo中node
        List<Device> nodes = new ArrayList<Device>();
        if (arpTable == null){
            LOGGER.error("ArpTable is null,it seems we get arp table failed!");
            return false;
        }
        Switch aSwitch = null;
        Computer computer = null;
        for (String mac:arpTable.keySet()){
            if (switchesIp.contains(arpTable.get(mac))){
                aSwitch = new Switch.SwitchBuilder(arpTable.get(mac),TopoConstant.SWITCH_TYPE).build();
                topo.getNodeSwitches().add(aSwitch);
                nodes.add(aSwitch);
            }else{
                computer = new Computer(arpTable.get(mac),mac,TopoConstant.COMPUTER_TYPE);
                topo.getNodeComputers().add(computer);
                nodes.add(computer);
            }
        }
        topo.setNodes(nodes);

        //补充Switch信息
        for (Device device : nodes){
            if (device.getType().equals(TopoConstant.SWITCH_TYPE)){
                Switch _switch = (Switch)device;
                //构造mac地址表
                HashMap<String,String> macTablePrivate = macTable.get(_switch.getIp());
                _switch.setMacTable(macTablePrivate);
                //构建connectedDevices和portedDevices
                for (String mac : macTablePrivate.keySet()){
                    //如果arp表中没有对应的ip，即该设备没有Ip（不研究没有IP的设备），或该mac对应的设备不是本交换机
                    if (arpTable.get(mac) != null || !arpTable.get(mac).equals(_switch.getIp())){
                        Device thisDevice = TopoUtil.getDeviceByIp(arpTable.get(mac),nodes);
                        //构建connectedDevices
                        _switch.getConnectedDevices().put(thisDevice,macTablePrivate.get(mac));
                        //构建portedDevices
                        if (_switch.getPortedDevices().get(macTablePrivate.get(mac)) == null){
                            List<Device> portedDevices = new ArrayList<Device>();
                            portedDevices.add(thisDevice);
                            _switch.getPortedDevices().put(macTablePrivate.get(mac),portedDevices);
                        }else{
                            _switch.getPortedDevices().get(macTablePrivate.get(mac)).add(thisDevice);
                        }
                        //构造portedSwitches和portedComputers
                        if (thisDevice.getType().equals(TopoConstant.SWITCH_TYPE)){
                            //构建portedSwitches
                            if (_switch.getPortedSwitches().get(macTablePrivate.get(mac)) == null){
                                List<Switch> portedSwitches = new ArrayList<Switch>();
                                portedSwitches.add((Switch) thisDevice);
                                _switch.getPortedSwitches().put(macTablePrivate.get(mac),portedSwitches);
                            }else{
                                _switch.getPortedSwitches().get(macTablePrivate.get(mac)).add((Switch) thisDevice);
                            }
                        }else{
                            //构建portedComputers
                            if (_switch.getPortedComputers().get(macTablePrivate.get(mac)) == null){
                                List<Computer> portedComputers = new ArrayList<Computer>();
                                portedComputers.add((Computer) thisDevice);
                                _switch.getPortedComputers().put(macTablePrivate.get(mac),portedComputers);
                            }else{
                                _switch.getPortedComputers().get(macTablePrivate.get(mac)).add((Computer) thisDevice);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean discoveTopo() {
        List<Switch> switches = topo.getNodeSwitches();
        if (switches == null){
            LOGGER.debug("Switches is empty!");
            return false;
        }
        List<Computer> computers = topo.getNodeComputers();
        if (computers == null){
            LOGGER.debug("Computers is empty!");
            return false;
        }

        List<Computer> unhandleComputers = computers;
        //第一步，基于交换机之间的互联信息以及交换机与主机之间的互联关系，尽可能确定主机直连的交换机
        //第二步，基于已经确定的主机，完善交换机之间的互联信息
        //第三步，重复第一步和第二步，直到，所有主机都确定连在哪一台交换机，或者前后两次交换机的互联信息没有变化并且后一次没有确定新的主机位置
        //      此时，会存在部分主机无法确定位置，以及交换机之间的互联信息不完整。
        unhandleComputers = iterProcessor(unhandleComputers,switches);
        if (unhandleComputers.size() == 0){
            LOGGER.info("All computers have been handled!");
        }else{
            LOGGER.info("############################################");
            LOGGER.info("The computers cannot been handle as follow:");
            for (Computer computer : unhandleComputers){
                LOGGER.info("Computer[" + computer.getIp() + "]");
            }
            LOGGER.info("############################################");
        }
        //第四步，基于交换机之间的互联信息，确定交换机之间的直连信息
        for (Switch switchOne : switches){
            for (Switch switchTwo : switches){
                if (switchOne != switchTwo){
                    discoverSwitches(switchOne,switchTwo);
                }
            }
        }
        return true;
    }

    public List<Computer> iterProcessor(List<Computer> unhandleComputers,List<Switch> switches){
        List<Computer> resultUnhandleComputers = unhandleComputers;
        if (unhandleComputers.size() != 0 ){
            List<Computer> handleComputers = new ArrayList<Computer>();
            List<Computer> unhandleTemp = new ArrayList<Computer>();
            //第一步,基于交换机之间的互联信息以及交换机与主机之间的互联关系，尽可能确定主机直连的交换机
            for (Computer computer : unhandleComputers){
                if (!locateComputers(computer,switches)){
                    unhandleTemp.add(computer);
                }else{
                    handleComputers.add(computer);
                }
            }
            //第二步,基于已经确定的主机，完善交换机之间的互联信息
            if (handleComputers.size() != 0){
                for (Computer computer : handleComputers){
                    perfectSwitches(computer,switches);
                }
                resultUnhandleComputers = iterProcessor(unhandleTemp,switches);
            }
        }
        return resultUnhandleComputers;
    }

    /**
     * 定位主机的位置
     * @param computer 主机对象
     * @param switches 所有交换机的列表
     * @return 如果定位成功返回true，如果没有定位成功返回false
     */
    public boolean locateComputers(Computer computer,List<Switch> switches){
        //与该主机关联的交换机列表
        List<Switch> connectedSwitches = new ArrayList<Switch>();
        for (Switch _switch : switches){
            for (Device tempDevice : _switch.getConnectedDevices().keySet()){
                if (tempDevice.getIp().equals(computer.getIp())){
                    connectedSwitches.add(_switch);
                }
            }
        }
        //该交换机上与主机对应的端口，不同于与其他所有交换机对应的端口
        if (connectedSwitches.size() == 1){
            //如果与主机相连的交换机只有一台，则认为该交换机与该主机直连
            Switch theSwitch = connectedSwitches.get(0);
            computer.setSwitch(theSwitch);
            theSwitch.getConnectedComputers().put(computer,theSwitch.getConnectedDevices().get(computer));
            EdgeComputerAndSwitch edge = new EdgeComputerAndSwitch(computer.getIp(),theSwitch.getIp(),theSwitch.getConnectedDevices().get(computer));
            topo.getEdges().add(edge);
            return true;
        }else if (connectedSwitches.size() > 1){
            //如果有多台，则对于每台交换机，该交换机上与主机对应的端口，不同于与其他所有交换机对应的端口，则认为
            //  该交换机与该主机直连
            outloop:
            for (Switch _switch : connectedSwitches){
                for (Switch __switch:switches){
                    if (__switch != _switch){
                        if (_switch.getConnectedDevices().get(__switch) == null || _switch.getConnectedDevices().get(computer).equals(_switch.getConnectedDevices().get(__switch))){
                            continue outloop;
                        }
                    }
                }
                computer.setSwitch(_switch);
                _switch.getConnectedComputers().put(computer,_switch.getConnectedDevices().get(computer));
                EdgeComputerAndSwitch edge = new EdgeComputerAndSwitch(computer.getIp(),_switch.getIp(),_switch.getConnectedDevices().get(computer));
                topo.getEdges().add(edge);
                return true;
            }
        }else{
            LOGGER.debug("与主机[" + computer.getIp() + "]相连的交换机个数为0");
            return false;
        }
        return false;
    }

    /**
     * 基于已经确认的主机位置，进一步完善交换机之间的互联信息
     * @param computer 已经确认位置的主机
     * @param switches 所有交换机列表
     * @return
     */
    public boolean perfectSwitches(Computer computer,List<Switch> switches){
        if (computer.getSwitch() == null){
            LOGGER.debug("The switch this computer[" + computer.getIp() + "] connected is not discovered!");
            return false;
        }
        //与该主机关联的交换机列表
        List<Switch> connectedSwitches = new ArrayList<Switch>();
        for (Switch _switch : switches){
            for (Device tempDevice : _switch.getConnectedDevices().keySet()){
                if (tempDevice.getIp().equals(computer.getIp())){
                    connectedSwitches.add(_switch);
                }
            }
        }

        //与该主机直连的交换机和主机在其他交换机的MAC表中对应的端口是一样的
        //所以通过其他交换机上该主机对应的端口，可以推断该交换机在其他交换机上对应的端口
        for (Switch aSwitch : connectedSwitches){
            if (computer.getSwitch() != aSwitch){
                if (!aSwitch.getConnectedDevices().keySet().contains(computer.getSwitch())){
                    aSwitch.getConnectedDevices().put(computer.getSwitch(),computer.getSwitch().getConnectedDevices().get(computer));
                }
            }
        }
        return true;
    }

    /**
     * 确定两个交换机之间是不是直连
     * @param switchOne
     * @param switchTwo
     * @return true:直连，false:可能不是直连
     */
    public boolean discoverSwitches(Switch switchOne,Switch switchTwo){
        if (switchOne != switchTwo){
            List<Device> intersection = getIntersection(switchOne,switchTwo);
            if (intersection == null){
                LOGGER.debug("Getting the intersection between switchOne[" + switchOne.getIp() + "] and switchTwo[" + switchTwo.getIp() + "] is failed!");
                return false;
            }else if (intersection.size() == 0){
                //交集为空,则两个交换机直连
                switchOne.getConnectedSwitches().put(switchTwo,switchOne.getConnectedDevices().get(switchTwo));
                switchTwo.getConnectedSwitches().put(switchOne,switchTwo.getConnectedDevices().get(switchOne));
                EdgeSwitches edge = new EdgeSwitches(switchOne.getIp(),switchOne.getConnectedDevices().get(switchTwo),switchTwo.getIp(),switchTwo.getConnectedDevices().get(switchOne));
                topo.getEdges().add(edge);
                return true;
            }
        }
        LOGGER.debug("It is still unknown whether Switch[" + switchOne.getIp() + "] and Switch[" + switchTwo.getIp() + "] is connected directly!");
        return false;
    }

    /**
     * 获取两个交换机对应端口上设备的交集
     * A通过a与B的b相连，那么A上a端口对应的设备与B上b端口对应的设备的交集就是所求的
     * @param switch1
     * @param switch2
     * @return null表示操作没有正常进行，intersectionDevices是两个交换机之间的交集
     */
    public List<Device> getIntersection(Switch switch1,Switch switch2){
        List<Device> intersectionDevices = new ArrayList<Device>();
        if (switch1 == switch2){
            LOGGER.debug("Switch1 is the same to switch2!");
            return null;
        }
        String port1 = switch1.getConnectedDevices().get(switch2);
        String port2 = switch2.getConnectedDevices().get(switch1);
        if (port1 == null || port2 == null){
            LOGGER.debug("Information between switch1[] and switch2[] is incomplete!");
            return null;
        }
        for (Device device : switch1.getPortedDevices().get(port1)){
            if (switch2.getPortedDevices().get(port2).contains(device)){
                intersectionDevices.add(device);
            }
        }
        return intersectionDevices;
    }

}
