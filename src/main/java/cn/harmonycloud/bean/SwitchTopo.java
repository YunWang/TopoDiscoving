package cn.harmonycloud.bean;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyun on 3/27/18.
 */
public class SwitchTopo {
    private final static Logger LOGGER = LoggerFactory.getLogger(SwitchTopo.class);
    /**
     * 存储topo结构中的节点，包括switch和computer
     */
    private List<Device> nodes = new ArrayList<Device>();

    /**
     * 存储topo结构中的边，包括交换机之间的边和交换机与主机之间的边
     */
    private List<Edge> edges = new ArrayList<Edge>();

    /**
     * topo结构中的switch节点
     */
    private List<Switch> nodeSwitches = new ArrayList<Switch>();

    /**
     * topo结构中的主机节点

     */
    private List<Computer> nodeComputers = new ArrayList<Computer>();
    /**
     * topo结构中交换机之间的边
     */
    private List<EdgeSwitches> edgeSwitches = new ArrayList<EdgeSwitches>();
    /**
     * topo结构中交换机与主机之间的边
     */
    private List<EdgeComputerAndSwitch> edgeComputerAndSwitches = new ArrayList<EdgeComputerAndSwitch>();

    public SwitchTopo(){}

    public SwitchTopo(List<Switch> nodeSwitches,List<Computer> nodeComputers,List<EdgeSwitches> edgeSwitches,List<EdgeComputerAndSwitch> edgeComputerAndSwitches){
        this.nodeSwitches = nodeSwitches;
        this.nodeComputers = nodeComputers;
        this.edgeSwitches = edgeSwitches;
        this.edgeComputerAndSwitches = edgeComputerAndSwitches;
    }
    public List<EdgeSwitches> getEdgeSwitches() {
        return edgeSwitches;
    }

    public void setEdgeSwitches(List<EdgeSwitches> edgeSwitches) {
        this.edgeSwitches = edgeSwitches;
    }

    public List<EdgeComputerAndSwitch> getEdgeComputerAndSwitches() {
        return edgeComputerAndSwitches;
    }

    public void setEdgeComputerAndSwitches(List<EdgeComputerAndSwitch> edgeComputerAndSwitches) {
        this.edgeComputerAndSwitches = edgeComputerAndSwitches;
    }
    public List<Switch> getNodeSwitches() {
        return nodeSwitches;
    }

    public void setNodeSwitches(List<Switch> nodeSwitches) {
        this.nodeSwitches = nodeSwitches;
    }

    public List<Computer> getNodeComputers() {
        return nodeComputers;
    }

    public void setNodeComputers(List<Computer> nodeComputers) {
        this.nodeComputers = nodeComputers;
    }
    public List<Device> getNodes() {
        return nodes;
    }

    public void setNodes(List<Device> nodes) {
        this.nodes = nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }
}
