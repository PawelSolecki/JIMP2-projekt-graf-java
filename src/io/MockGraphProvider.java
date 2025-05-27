package io;

import model.Graph;
import model.Node;

public class MockGraphProvider {

    public static Graph createSampleGraph1() {
        Graph graph = new Graph(4); // np. 4 kolumny w siatce

        Node n0 = new Node(0, 0, 0);
        Node n1 = new Node(1, 0, 1);
        Node n2 = new Node(2, 1, 0);
        Node n3 = new Node(3, 1, 1);

        graph.addNode(n0);
        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);

        graph.addEdge(n0, n1);
        graph.addEdge(n0, n2);
        graph.addEdge(n1, n3);
        graph.addEdge(n2, n3);

        return graph;
    }
    public static Graph createSampleGraph2() {
        Graph graph = new Graph(4); // np. 4 kolumny w siatce

        Node n0 = new Node(4, 0, 2);
        Node n1 = new Node(5, 1, 2);
        Node n2 = new Node(6, 2, 0);
        Node n3 = new Node(7, 2, 1);

        graph.addNode(n0);
        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);

        graph.addEdge(n0, n1);
        graph.addEdge(n1, n3);
        graph.addEdge(n2, n3);

        return graph;
    }
}
