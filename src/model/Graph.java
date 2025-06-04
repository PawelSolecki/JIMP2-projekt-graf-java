package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final Map<Node, List<Node>> adjacencyList = new HashMap<>();
    private final Map<Integer, Node> nodesByIndex = new HashMap<>(); // Maps node indexes to Node objects
    private final int numCols;

    public Graph(int numCols) {
        this.numCols = numCols;
    }

    public void addNode(Node n) {
        adjacencyList.putIfAbsent(n, new ArrayList<>());
        nodesByIndex.put(n.getIndex(), n); // Add the node to the index map
    }

    public void addEdge(int fromIndex, int toIndex) {
        Node fromNode = nodesByIndex.get(fromIndex);
        Node toNode = nodesByIndex.get(toIndex);

        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Node with given index does not exist.");
        }

        adjacencyList.get(fromNode).add(toNode);
    }

    public List<Node> getNeighbors(Node n) {
        return adjacencyList.getOrDefault(n, new ArrayList<>());
    }

    public Map<Node, List<Node>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getNumCols() {
        return numCols;
    }

    public Node getNodeByIndex(int index) {
        return nodesByIndex.get(index);
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodesByIndex.values());
    }

    public int getNumVertices() {
        return adjacencyList.size();
    }


}