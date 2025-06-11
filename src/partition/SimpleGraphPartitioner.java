package partition;

import model.Graph;
import model.Node;

import java.util.*;

/**
 * A simple graph partitioning implementation that divides the graph into specified number of partitions.
 * This implementation uses a breadth-first traversal strategy to create more coherent partitions.
 */
public class SimpleGraphPartitioner implements GraphPartitioner {
    
    /**
     * Partitions a graph into a specified number of partitions.
     *
     * This method assigns each node in the graph to a partition. It attempts to create
     * balanced, connected partitions. After partitioning, it "cuts" edges that 
     * connect nodes belonging to different partitions by removing them from the 
     * graph's adjacency list.
     *
     * @param graph The graph to be partitioned. The graph will be modified in place.
     * @param numPartitions The desired number of partitions.
     * @param marginPercent A percentage (e.g., 0.1 for 10%) that allows a partition's size
     * to exceed the ideal size, providing flexibility for balancing.
     * @return The same graph object, now partitioned and with inter-partition edges removed.
     */
    @Override
    public Graph partition(Graph graph, int numPartitions, double marginPercent) {
        // --- 1. Initialization and Base Cases ---
        List<Node> allNodes = graph.getNodes();
        int totalNodes = allNodes.size();

        if (numPartitions <= 1 || totalNodes == 0) {
            return graph;
        }

        Set<Node> unassignedNodes = new HashSet<>(allNodes);
        List<List<Node>> partitions = new ArrayList<>();
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(new ArrayList<>());
        }

        double idealPartitionSize = (double) totalNodes / numPartitions;
        int maxPartitionSize = (int) Math.ceil(idealPartitionSize * (1.0 + marginPercent));

        // --- 2. Sequentially Grow Partitions using BFS ---
        for (int p = 0; p < numPartitions; p++) {
            if (unassignedNodes.isEmpty()) {
                break;
            }

            Queue<Node> queue = new LinkedList<>();
            Node startNode = unassignedNodes.iterator().next();

            startNode.setPartition(p);
            unassignedNodes.remove(startNode);
            partitions.get(p).add(startNode);
            queue.add(startNode);

            while (!queue.isEmpty()) {
                Node currentNode = queue.poll();

                for (Node neighbor : graph.getNeighbors(currentNode)) {
                    if (unassignedNodes.contains(neighbor) && partitions.get(p).size() < maxPartitionSize) {
                        neighbor.setPartition(p);
                        unassignedNodes.remove(neighbor);
                        partitions.get(p).add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }

        // --- 3. Assign Remaining Nodes by Connectivity ---
        if (!unassignedNodes.isEmpty()) {
            int lastPassAssignedCount;
            do {
                lastPassAssignedCount = 0;
                Iterator<Node> iterator = unassignedNodes.iterator();
                while (iterator.hasNext()) {
                    Node leftover = iterator.next();
                    // Attempt to assign the leftover node to an adjacent, already-formed partition
                    for (Node neighbor : graph.getNeighbors(leftover)) {
                        if (!unassignedNodes.contains(neighbor)) { // Check if neighbor is already in a partition
                            int neighborPartition = neighbor.getPartition();
                            leftover.setPartition(neighborPartition);
                            partitions.get(neighborPartition).add(leftover);
                            iterator.remove(); // Node is now assigned
                            lastPassAssignedCount++;
                            break; // Move to the next leftover node
                        }
                    }
                }
                // Repeat this process until a full pass over the leftovers results in no new assignments.
            } while (lastPassAssignedCount > 0 && !unassignedNodes.isEmpty());

            // If any nodes remain, they are in completely isolated components.
            // Assign these to the smallest partitions as a fallback.
            if (!unassignedNodes.isEmpty()) {
                 for(Node leftover : new ArrayList<>(unassignedNodes)) {
                     int smallestPartitionIndex = 0;
                     for (int i = 1; i < numPartitions; i++) {
                         if (partitions.get(i).size() < partitions.get(smallestPartitionIndex).size()) {
                             smallestPartitionIndex = i;
                         }
                     }
                     leftover.setPartition(smallestPartitionIndex);
                     partitions.get(smallestPartitionIndex).add(leftover);
                     unassignedNodes.remove(leftover);
                 }
            }
        }

        // --- 4. Cut Inter-Partition Edges ---
        for (Node node : allNodes) {
            List<Node> neighbors = graph.getNeighbors(node);
            neighbors.removeIf(neighbor -> node.getPartition() != neighbor.getPartition());
        }

        return graph;
    }
}
