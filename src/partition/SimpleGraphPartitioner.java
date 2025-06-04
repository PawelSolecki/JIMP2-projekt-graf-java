package partition;

import model.Graph;
import model.Node;

import java.util.*;

/**
 * A simple graph partitioning implementation that divides the graph into specified number of partitions.
 * This implementation uses a breadth-first traversal strategy to create more coherent partitions.
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
        
        // Calculate target size for each partition
        int totalNodes = graph.getNumVertices();
        int targetNodesPerPartition = (int) Math.ceil((double) totalNodes / numPartitions);
        int maxNodesPerPartition = (int) Math.ceil(targetNodesPerPartition * (1 + marginPercent / 100.0));
        
        // Track how many nodes are assigned to each partition
        int[] partitionSizes = new int[numPartitions];
        
        // Track which nodes have been assigned to a partition
        Set<Node> unassignedNodes = new HashSet<>(adjacencyList.keySet());
        Set<Node> assignedNodes = new HashSet<>();
        
        // Map original nodes to partitioned graph nodes for easy lookup
        Map<Integer, Node> partitionedNodes = new HashMap<>();
        for (Node node : partitionedGraph.getAdjacencyList().keySet()) {
            partitionedNodes.put(node.getIndex(), node);
        }
        
        // Use a breadth-first approach to assign nodes to partitions
        int currentPartition = 0;
        
        while (!unassignedNodes.isEmpty() && currentPartition < numPartitions) {
            // Start with a "seed" node for this partition
            Node seed = findSeedNode(unassignedNodes, adjacencyList);
            Queue<Node> queue = new LinkedList<>();
            queue.add(seed);
            unassignedNodes.remove(seed);
            
            // Assign the seed node to the current partition
            Node partitionedSeed = partitionedNodes.get(seed.getIndex());
            partitionedSeed.setPartition(currentPartition);
            assignedNodes.add(seed);
            partitionSizes[currentPartition]++;
            
            // Use BFS to find connected nodes and add them to the same partition
            while (!queue.isEmpty() && partitionSizes[currentPartition] < maxNodesPerPartition) {
                Node current = queue.poll();
                List<Node> neighbors = adjacencyList.get(current);
                
                if (neighbors != null) {
                    // First collect and sort neighbors by their connectivity
                    List<Node> unassignedNeighbors = new ArrayList<>();
                    for (Node neighbor : neighbors) {
                        if (unassignedNodes.contains(neighbor)) {
                            unassignedNeighbors.add(neighbor);
                        }
                    }
                    
                    // Process neighbors
                    for (Node neighbor : unassignedNeighbors) {
                        // Check again if this neighbor is still unassigned (it might have been assigned by another node)
                        if (unassignedNodes.contains(neighbor)) {
                            // If we can add more nodes to this partition, do it
                            if (partitionSizes[currentPartition] < maxNodesPerPartition) {
                                Node partitionedNeighbor = partitionedNodes.get(neighbor.getIndex());
                                partitionedNeighbor.setPartition(currentPartition);
                                queue.add(neighbor);
                                unassignedNodes.remove(neighbor);
                                assignedNodes.add(neighbor);
                                partitionSizes[currentPartition]++;
                            } else {
                                // This partition is full, break out of the entire BFS for this partition
                                queue.clear(); // Clear the queue to exit the while loop
                                break;
                            }
                        }
                    }
                }
            }
            
            // If this partition is at capacity or we've processed all connected components 
            // from this seed, move to the next partition
            if (partitionSizes[currentPartition] >= targetNodesPerPartition || queue.isEmpty()) {
                currentPartition++;
            }
        }
        
        // If we have leftover unassigned nodes, assign them in a way that minimizes edge cuts
        if (!unassignedNodes.isEmpty()) {
            List<Node> remainingNodes = new ArrayList<>(unassignedNodes);
            
            // First try to assign nodes to partitions where they have the most neighbors
            for (Node node : remainingNodes) {
                if (!unassignedNodes.contains(node)) continue; // Skip if already assigned in a previous iteration
                
                // Count neighbors in each partition
                int[] neighborCountsByPartition = new int[numPartitions];
                List<Node> neighbors = adjacencyList.get(node);
                
                if (neighbors != null) {
                    for (Node neighbor : neighbors) {
                        if (assignedNodes.contains(neighbor)) {
                            // Find this neighbor in the partitioned graph
                            Node partitionedNeighbor = partitionedNodes.get(neighbor.getIndex());
                            int partition = partitionedNeighbor.getPartition();
                            neighborCountsByPartition[partition]++;
                        }
                    }
                }
                
                // Find the partition with most neighbors but still under capacity
                int bestPartition = -1;
                int maxNeighbors = -1;
                
                for (int i = 0; i < numPartitions; i++) {
                    if (partitionSizes[i] < maxNodesPerPartition && 
                        neighborCountsByPartition[i] > maxNeighbors) {
                        maxNeighbors = neighborCountsByPartition[i];
                        bestPartition = i;
                    }
                }
                
                // If we found a partition with neighbors, use it
                // Otherwise use the partition with fewest nodes
                if (bestPartition == -1) {
                    bestPartition = 0;
                    for (int i = 1; i < numPartitions; i++) {
                        if (partitionSizes[i] < partitionSizes[bestPartition]) {
                            bestPartition = i;
                        }
                    }
                }
                
                // Assign node to the best partition
                Node partitionedNode = partitionedNodes.get(node.getIndex());
                partitionedNode.setPartition(bestPartition);
                partitionSizes[bestPartition]++;
                unassignedNodes.remove(node);
                assignedNodes.add(node);
            }
            
            // If any nodes are still unassigned (which shouldn't happen), assign to smallest partition
            if (!unassignedNodes.isEmpty()) {
                for (Node node : unassignedNodes) {
                    int minPartition = 0;
                    for (int i = 1; i < numPartitions; i++) {
                        if (partitionSizes[i] < partitionSizes[minPartition]) {
                            minPartition = i;
                        }
                    }
                    
                    Node partitionedNode = partitionedNodes.get(node.getIndex());
                    partitionedNode.setPartition(minPartition);
                    partitionSizes[minPartition]++;
                }
            }
        }
        
        // Print partition statistics for debugging
        System.out.println("Partition sizes:");
        int totalEdgeCuts = calculateEdgeCuts(partitionedGraph);
        for (int i = 0; i < numPartitions; i++) {
            System.out.println("Partition " + i + ": " + partitionSizes[i] + " nodes");
        }
        System.out.println("Total edge cuts: " + totalEdgeCuts);
        
        return partitionedGraph;
    }
    
    /**
     * Find a good seed node to start a new partition.
     * Ideally, we want to pick nodes that are more isolated or at the "edge" of the graph.
     */
    private Node findSeedNode(Set<Node> unassignedNodes, Map<Node, List<Node>> adjacencyList) {
        if (unassignedNodes.isEmpty()) {
            throw new IllegalStateException("No unassigned nodes available");
        }
        
        // Use a better heuristic: 
        // 1. First try to find a node at the "boundary" - having some assigned neighbors
        // 2. If no boundary nodes exist, pick the node with highest degree (most connections)
        // 3. If multiple nodes tie, prefer the one with the fewest unassigned neighbors
        
        // First look for boundary nodes (nodes connected to already assigned partitions)
        List<Node> boundaryNodes = new ArrayList<>();
        for (Node node : unassignedNodes) {
            List<Node> neighbors = adjacencyList.get(node);
            boolean hasBoundary = false;
            
            if (neighbors != null) {
                for (Node neighbor : neighbors) {
                    if (!unassignedNodes.contains(neighbor)) {
                        hasBoundary = true;
                        break;
                    }
                }
            }
            
            if (hasBoundary) {
                boundaryNodes.add(node);
            }
        }
        
        if (!boundaryNodes.isEmpty()) {
            // Find the boundary node with the most connections to make a good starting point
            Node bestBoundaryNode = null;
            int maxConnections = Integer.MIN_VALUE;
            
            for (Node node : boundaryNodes) {
                List<Node> neighbors = adjacencyList.get(node);
                if (neighbors != null && neighbors.size() > maxConnections) {
                    maxConnections = neighbors.size();
                    bestBoundaryNode = node;
                }
            }
            
            return bestBoundaryNode;
        }
        
        // If no boundary nodes, find node with fewest unassigned neighbors (most isolated)
        Node bestSeed = null;
        int minNeighbors = Integer.MAX_VALUE;
        
        for (Node node : unassignedNodes) {
            List<Node> neighbors = adjacencyList.get(node);
            int unassignedNeighborCount = 0;
            
            if (neighbors != null) {
                for (Node neighbor : neighbors) {
                    if (unassignedNodes.contains(neighbor)) {
                        unassignedNeighborCount++;
                    }
                }
                
                if (unassignedNeighborCount < minNeighbors || 
                   (unassignedNeighborCount == minNeighbors && bestSeed != null && 
                    neighbors.size() > adjacencyList.get(bestSeed).size())) {
                    minNeighbors = unassignedNeighborCount;
                    bestSeed = node;
                }
            }
        }
        
        // If we couldn't find a good seed, just pick the first unassigned node
        return bestSeed != null ? bestSeed : unassignedNodes.iterator().next();
    }
    
    /**
     * Calculate the number of edge cuts in the graph based on partition assignments.
     * An edge cut is an edge that connects nodes in different partitions.
     */
    private int calculateEdgeCuts(Graph graph) {
        int edgeCuts = 0;
        Map<Node, List<Node>> adjacencyList = graph.getAdjacencyList();
        
        // Use a set to track edges we've already counted (to avoid double counting)
        Set<String> countedEdges = new HashSet<>();
        
        for (Map.Entry<Node, List<Node>> entry : adjacencyList.entrySet()) {
            Node source = entry.getKey();
            int sourcePartition = source.getPartition();
            int sourceIndex = source.getIndex();
            
            for (Node target : entry.getValue()) {
                int targetIndex = target.getIndex();
                int targetPartition = target.getPartition();
                
                if (sourcePartition != targetPartition) {
                    // Create a unique edge ID that's the same regardless of direction
                    String edgeId = sourceIndex < targetIndex ? 
                                    sourceIndex + "-" + targetIndex :
                                    targetIndex + "-" + sourceIndex;
                                    
                    if (!countedEdges.contains(edgeId)) {
                        edgeCuts++;
                        countedEdges.add(edgeId);
                    }
                }
            }
        }
        
        return edgeCuts;
    }
}
