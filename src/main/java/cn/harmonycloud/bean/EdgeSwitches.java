package cn.harmonycloud.bean;

/**
 * Created by wangyun on 3/27/18.
 * 交换机之间的边
 */
public class EdgeSwitches implements Edge{
    private String switchIp1;
    private String switchIp2;
    private String switchPort1;
    private String switchPort2;

    /**
     * Constructor
     */
    public EdgeSwitches(){}

    /**
     * Constructor
     * @param switchIp1
     * @param switchIp2
     * @param switchPort1
     * @param switchPort2
     */
    public EdgeSwitches(String switchIp1,String switchPort1,String switchIp2,String switchPort2){
        this.switchIp1 = switchIp1;
        this.switchIp2 = switchIp2;
        this.switchPort1 = switchPort1;
        this.switchPort2 = switchPort2;
    }

    public String getSwitchIp1() {
        return switchIp1;
    }

    public void setSwitchIp1(String switchIp1) {
        this.switchIp1 = switchIp1;
    }

    public String getSwitchIp2() {
        return switchIp2;
    }

    public void setSwitchIp2(String switchIp2) {
        this.switchIp2 = switchIp2;
    }

    public String getSwitchPort1() {
        return switchPort1;
    }

    public void setSwitchPort1(String switchPort1) {
        this.switchPort1 = switchPort1;
    }

    public String getSwitchPort2() {
        return switchPort2;
    }

    public void setSwitchPort2(String switchPort2) {
        this.switchPort2 = switchPort2;
    }


}
