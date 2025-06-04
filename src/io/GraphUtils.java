package io;

import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphUtils {

    public Graph build(int numCols, List<Integer> vertices, List<Integer> verticesRow,
                       List<Integer> connectedNodes, List<Integer> firstElementInGroup) {

        Graph graph = new Graph(numCols);
        int indexNumber = 0;
        for (int i = 0; i < verticesRow.size() - 1; i++) {
            for (int j = verticesRow.get(i); j < verticesRow.get(i + 1); j++) {
                graph.addNode(new Node(indexNumber, i, vertices.get(j)));
                indexNumber++;

            }

        }

        for (int i = 0; i < firstElementInGroup.size() - 1; i++) {
            for (int j = firstElementInGroup.get(i); j < firstElementInGroup.get(i + 1); j++) {
                graph.addEdge(connectedNodes.get(firstElementInGroup.get(i)), connectedNodes.get(j));
            }
        }

        return graph;
    }

    public List<Graph> separateGraphs(Graph graph) {
        Set<Node> visited = new HashSet<>();
        List<Graph> components = new ArrayList<>();

        for (Node node : graph.getNodes()) {
            if (!visited.contains(node)) {
                List<Node> componentNodes = new ArrayList<>();
                dfs(node, graph, visited, componentNodes);

                Graph componentGraph = new Graph(graph.getNumCols());

                for (Node n : componentNodes) {
                    componentGraph.addNode(n);
                }

                for (Node n : componentNodes) {
                    for (Node neighbor : graph.getNeighbors(n)) {
                        if (componentGraph.getNodeByIndex(neighbor.getIndex()) != null) {
                            componentGraph.addEdge(n.getIndex(), neighbor.getIndex());
                        }
                    }
                }

                components.add(componentGraph);
            }
        }

        return components;
    }

    private static void dfs(Node current, Graph graph, Set<Node> visited, List<Node> component) {
        visited.add(current);
        component.add(current);

        for (Node neighbor : graph.getNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, graph, visited, component);
            }
        }

        for (Node node : graph.getNodes()) {
            if (graph.getNeighbors(node).contains(current) && !visited.contains(node)) {
                dfs(node, graph, visited, component);
            }
        }
    }


}
