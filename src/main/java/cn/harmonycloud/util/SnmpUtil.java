package cn.harmonycloud.util;

import cn.harmonycloud.constant.SnmpConstant;
import cn.harmonycloud.snmp.SnmpManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wangyun on 3/27/18.
 */
public class SnmpUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(SnmpUtil.class);

    /**
     * 获取所有switch的mac地址表
     * @param switchIps 所有switch的ip列表
     * @return HashMap类型,存储mac地址表;{switchIp:{mac:port}}
     */
    public static HashMap<String,HashMap<String,String>> getMacTable(List<String> switchIps){
        if (switchIps == null){
            LOGGER.error("SwitchIps is null when get macTable!");
            return null;
        }
        //存储mac表,{switchIp:{mac:port}}
        HashMap<String,HashMap<String,String>> macTable = new HashMap<String, HashMap<String, String>>();
        HashMap<String,String> portAddr = null;
        SnmpManager manager = new SnmpManager(SnmpConstants.version2c);
        PDU pdu = new PDU();	// 构造报文
        OID oPort = new OID(SnmpConstant.MAC_OID1);	//获取端口
        OID oMac = new OID(SnmpConstant.MAC_OID2);	//获取端口对应的MAC
        //设置要获取的对象ID，这个OID代表远程计算机的名称
        pdu.add(new VariableBinding(oPort));
        pdu.add(new VariableBinding(oMac));
        pdu.setType(PDU.GETNEXT);	// 设置报文类型
        try {
            for (String switchIp : switchIps){
                String udpURL = SnmpConstant.PROTOCOL + ":" + switchIp + "/" + SnmpConstant.UDP_PORT;
                portAddr = manager.getMacAndPort(pdu, udpURL, SnmpConstant.INTERNET_PORT);
                if (portAddr == null){
                    LOGGER.error("Switch[%s] mac table is empty!",switchIp);
                    return null;
                }
                macTable.put(switchIp,portAddr);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return macTable;
    }

    /**
     * 获取所有switch的arp表
     * @param switchIps 所有switch的ip列表
     * @return HashMap类型,存储所有switch的arp的总和;{mac:ip}
     */
    public static HashMap<String,String> getArpTable(List<String> switchIps){
        if (switchIps == null){
            LOGGER.error("SwitchIps is null when get arp table!");
            return null;
        }
        HashMap<String,String> arpTable = new HashMap<String, String>();
        SnmpManager snmpManager = new SnmpManager(SnmpConstants.version2c);
        PDU pdu = new PDU();
        OID oid1 = new OID(SnmpConstant.IP_OID1);
        OID oid2 = new OID(SnmpConstant.IP_OID2);
        // 设置要获取的对象ID，这个OID代表远程计算机的名称
        pdu.add(new VariableBinding(oid1));
        pdu.add(new VariableBinding(oid2));
        pdu.setType(PDU.GETNEXT);	// 设置报文类型
        try {
            for (String ip : switchIps){
                String udpUrl = SnmpConstant.PROTOCOL + ":" + ip + "/" + SnmpConstant.UDP_PORT;
                HashMap<String,String> temp = snmpManager.getMacAndIp(pdu,udpUrl);
                if (temp == null){
                    LOGGER.error("Cannot get ARP table!");
                    return null;
                }
                arpTable.putAll(temp);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return arpTable;
    }
}
