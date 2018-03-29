package cn.harmonycloud.topodiscoving;

/**
 * Created by wangyun on 3/28/18.
 */
public interface TopoDiscovingStrategy {
    /**
     * 从交换机获取数据
     * @return true:成功获取数据,false:获取数据失败
     */
    boolean getSwitchData();

    /**
     * 对获取的交换机数据进行加工处理
     * @return
     */
    boolean processData();

    /**
     * 根据数据发现拓扑结构
     * @return
     */
    boolean discoveTopo();
}
