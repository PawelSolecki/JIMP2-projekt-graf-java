package partition;

import model.Graph;
import model.Node;

import java.util.*;

/**
 * A simple graph partitioning implementation that divides the graph into specified number of partitions.
 * This implementation uses a basic greedy algorithm to create partitions.
 */
public class SimpleGraphPartitioner implements GraphPartitioner {
    
    @Override
    public Graph partition(Graph graph, int numPartitions, double marginPercent) {
        if (numPartitions <= 0) {
            throw new IllegalArgumentException("Number of partitions must be positive");
        }
        
        // Create a copy of the original graph to mark partition assignments
        Graph partitionedGraph = new Graph(graph.getNumCols());
        
        // Copy all nodes from original graph
        Map<Node, List<Node>> adjacencyList = graph.getAdjacencyList();
        for (Node node : adjacencyList.keySet()) {
            partitionedGraph.addNode(node);
        }
        
        // Copy all edges from original graph
        for (Map.Entry<Node, List<Node>> entry : adjacencyList.entrySet()) {
            Node source = entry.getKey();
            for (Node target : entry.getValue()) {
                partitionedGraph.addEdge(source.getIndex(), target.getIndex());
            }
        }
        
        // Assign each node to a partition (for simplicity, just assign nodes sequentially)
        // In a real implementation, you would use a more sophisticated algorithm 
        // like Metis, SCOTCH, or a spectral partitioning algorithm
        
        int nodesPerPartition = (int) Math.ceil((double) graph.getNumVertices() / numPartitions);
        int currentPartition = 0;
        int currentCount = 0;
        
        // Assign each node to a partition
        for (Node node : adjacencyList.keySet()) {
            // Get the corresponding node in the partitioned graph
            Node partitionedNode = partitionedGraph.getAdjacencyList().keySet()
                    .stream()
                    .filter(n -> n.getIndex() == node.getIndex())
                    .findFirst()
                    .orElseThrow();
            
            // Set the partition
            partitionedNode.setPartition(currentPartition);
            currentCount++;
            
            if (currentCount >= nodesPerPartition && currentPartition < numPartitions - 1) {
                currentPartition++;
                currentCount = 0;
            }
        }
        
        // In a real implementation, we would balance the partitions and minimize edge cuts
        // For now, we just return the partitioned graph with partition information
        
        return partitionedGraph;
    }
}
