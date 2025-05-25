package partition;

import model.Graph;

public interface GraphPartitioner {
    Graph partition(Graph graph, int numPartitions, double marginPercent);
}
