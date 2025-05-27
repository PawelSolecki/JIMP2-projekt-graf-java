package model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Graph {
    private final Map<Node, List<Node>> adjacencyList = new HashMap<>();
    private final int numCols;

    public Graph(int numCols) {
        this.numCols = numCols;
    }

    public void addNode(Node n) {
        adjacencyList.putIfAbsent(n, new ArrayList<>());
    }

    public void addEdge(Node from, Node to) {
        adjacencyList.get(from).add(to);
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

    public int getNumVertices() {
        return adjacencyList.size();
    }
}
