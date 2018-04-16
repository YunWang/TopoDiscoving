package cn.harmonycloud.snmp;

import cn.harmonycloud.constant.SnmpConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.*;

/**
 * Created by wangyun on 3/27/18.
 */
public class SnmpManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnmpManager.class);

    public Snmp snmp = null;
    public int version;

    /**
     * 存储MAC地址表数据
     * {mac:port}
     */
    public HashMap<String,String> macTable = new HashMap<String, String>();
    /**
     * 存储ARP表数据
     * {mac:ip}
     */
    public HashMap<String,String> arpTable = new HashMap<String, String>();

    /**
     * 创建snmp管理对象
     *
     * @param version snmp版本
     */
    public SnmpManager(int version){
        try {
            this.version = version;
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            if (version == SnmpConstants.version3) {
                // 设置安全模式
                USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);
            }
            // 开始监听消息
            transport.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取MAC地址表
     * @param pdu snmp数据包
     * @param address 地址
     * @param port 端口
     * @return MAC地址表,HashMap类型,{mac:port}
     * @throws IOException
     */
    public HashMap<String,String> getMacAndPort(PDU pdu, String address, String port,String community) throws IOException {
        Address targetAddress = GenericAddress.parse(address); // 生成目标地址对象
        Target target = null;
        target = setTarget(community);
        // 目标对象相关设置
        target.setAddress(targetAddress);
        target.setRetries(5);
        target.setTimeout(1000);
        // 发送报文 并且接受响应
        ResponseEvent response = snmp.send(pdu, target);
        // 处理响应
        PDU pduresult = response.getResponse();// 获取响应报文数据
        Vector<? extends VariableBinding> vector = pduresult.getVariableBindings();
        VariableBinding strPort = vector.get(0);// 获取端口号
        VariableBinding strMac = vector.get(1);// 获取MAC

        // 判断如果返回值是物理地址MAC,则进行递归获取数据
        if (strMac.getVariable().toString().contains(":")) {
            //配置多个外网端口时使用
            String[] array = port.split(",");
            List<String> list = Arrays.asList(array);
            if(!list.contains(strPort.getVariable().toString())){
                macTable.put(strMac.getVariable().toString(),strPort.getVariable().toString());
            }

            if (pdu.getVariableBindings().get(0).getOid().toString().equals(strPort.getOid().toString()) && pdu.getVariableBindings().get(1).getOid().toString().equals( strMac.getOid().toString())){
                LOGGER.debug("OID Duplicated!");
                return macTable;
            }

            pdu = new PDU();
            OID oPort = new OID(strPort.getOid().toString()); // 获取端口
            OID oMac = new OID(strMac.getOid().toString()); // 获取端口对应的MAC
            // 设置要获取的对象ID，这个OID代表远程计算机的名称
            pdu.add(new VariableBinding(oPort));
            pdu.add(new VariableBinding(oMac));
            // 设置报文类型
            pdu.setType(PDU.GETNEXT);
            getMacAndPort(pdu, address, port,community);
        }
        return macTable;
    }

    /**
     * 获取ARP表
     * @param pdu snmp数据包
     * @param address 获取ARP表的地址,格式为protocol:ip/port,比如udp:10.100.100.251/161
     * @return HashMap类型的ARP表,{mac:ip}
     * @throws IOException
     */
    public HashMap<String,String> getMacAndIp(PDU pdu, String address,String community) throws IOException {
        Address targetAddress = GenericAddress.parse(address); // 生成目标地址对象
        Target target = null;
        target = setTarget(community);
        // 目标对象相关设置
        target.setAddress(targetAddress);
        target.setRetries(5);
        target.setTimeout(1000);

        // 发送报文 并且接受响应
        ResponseEvent response = snmp.send(pdu, target);
        PDU pduresult = response.getResponse();// 获取响应报文数据
        Vector<? extends VariableBinding> vector = pduresult.getVariableBindings();
        VariableBinding strMac = vector.get(0);// 获取IP
        VariableBinding strIp = vector.get(1);// 获取IP

        // 判断如果返回值是IP地址,则进行递归获取数据
        if (strMac.getVariable().toString().contains(":")) {
            arpTable.put(strMac.getVariable().toString(),strIp.getVariable().toString());
            pdu = new PDU();
            OID oMac = new OID(strMac.getOid().toString()); // 获取端口对应的MAC
            OID oIp = new OID(strIp.getOid().toString()); // 获取端口对应的MAC
            // 设置要获取的对象ID，这个OID代表远程计算机的名称
            pdu.add(new VariableBinding(oMac));
            pdu.add(new VariableBinding(oIp));
            // 设置报文类型
            pdu.setType(PDU.GETNEXT);
            getMacAndIp(pdu, address,community);
        }
        return arpTable;
    }

    /**
     * 根据snmp版本设置target
     */
    private Target setTarget(String community){
        Target target = null;
        if (version == SnmpConstants.version3) {
            // 添加用户
            snmp.getUSM().addUser(new OctetString("MD5DES"), new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                    new OctetString("MD5DESUserAuthPassword"), PrivDES.ID, new OctetString("MD5DESUserPrivPassword")));
            target = new UserTarget();
            // 设置安全级别
            ((UserTarget) target).setSecurityLevel(SecurityLevel.AUTH_PRIV);
            ((UserTarget) target).setSecurityName(new OctetString("MD5DES"));
            target.setVersion(SnmpConstants.version3);
        } else {
            target = new CommunityTarget();
            if (version == SnmpConstants.version1) {
                target.setVersion(SnmpConstants.version1);
                ((CommunityTarget) target).setCommunity(new OctetString(community));
            } else {
                target.setVersion(SnmpConstants.version2c);
                ((CommunityTarget) target).setCommunity(new OctetString(community));
            }
        }
        return target;
    }

    public static void main(String[] args) throws IOException {
//        //test getMacAndPort
//        PDU pdu = new PDU();
//        OID oid1 = new OID(SnmpConstant.MACOID1);
//        OID oid2 = new OID(SnmpConstant.MACOID2);
//        pdu.add(new VariableBinding(oid1));
//        pdu.add(new VariableBinding(oid2));
//        pdu.setType(PDU.GETNEXT);
//
//        String udpURL = "udp:10.100.100.251/161";
//        SnmpManager manager = new SnmpManager(SnmpConstants.version2c);
//
//        HashMap<String,String> macTable = null;
//        macTable = manager.getMacAndPort(pdu,udpURL,"45");
//        if (macTable == null){
//            System.out.println("macTable is empty!");
//        }
//        System.out.println("MacTable:" + macTable.size());
//        for (String mac:macTable.keySet()){
//            System.out.println("Mac[" + mac + "] <==> port[" + macTable.get(mac) + "]");
//        }

        //test getMacAndIp
//        PDU pdu = new PDU();
//        OID oid1 = new OID(SnmpConstant.IP_OID1);
//        OID oid2 = new OID(SnmpConstant.IP_OID2);
//        pdu.add(new VariableBinding(oid1));
//        pdu.add(new VariableBinding(oid2));
//        pdu.setType(PDU.GETNEXT);
//
//        String udpURL = "udp:10.100.100.251/161";
//        SnmpManager manager = new SnmpManager(SnmpConstants.version2c);
//
//        HashMap<String,String> arpTable = null;
//        arpTable = manager.getMacAndIp(pdu,udpURL);
//        if (arpTable == null){
//            System.out.println("arpTable is empty!");
//        }
//        System.out.println("ARPTable:" + arpTable.size());
//        for (String mac:arpTable.keySet()){
//            System.out.println("Mac[" + mac + "] <==> ip[" + arpTable.get(mac) + "]");
//        }
    }
}
