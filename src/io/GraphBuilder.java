package io;

import model.Graph;
import model.Node;

import java.util.List;

public class GraphBuilder {

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

}
