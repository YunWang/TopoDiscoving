package cn.harmonycloud.constant;

/**
 * Created by wangyun on 3/27/18.
 */
public class SnmpConstant {

    /**
     * 获取ARP表的OID1,OID对应的值为MAC地址
     */
    public final static String IP_OID1 = "1.3.6.1.2.1.4.22.1.2";

    /**
     * 获取ARP表的OID2,OID对应的值为IP
     */
    public final static String IP_OID2 = "1.3.6.1.2.1.4.22.1.3";

    /**
     * 获取MAC地址表的OID1,OID对应的值为端口
     */
    public final static String MAC_OID1 = "1.3.6.1.2.1.17.4.3.1.2";

    /**
     * 获取MAC地址表的OID2,OID对应的值为MAC地址
     */
    public final static String MAC_OID2 = "1.3.6.1.2.1.17.4.3.1.1";

    /**
     * 获取ARP表时的端口值
     */
    public final static String INTERNET_PORT = "45";

    /**
     * snmp数据传输协议：udp
     */
    public final static String PROTOCOL = "udp";

    /**
     * snmp udp协议端口
     */
    public final static String UDP_PORT = "161";
}
