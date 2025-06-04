package gui;

import model.Graph;
import model.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColoredGraphView {
    private final Graph graph;
    private final Color color;
    private boolean visible;
    private boolean showPartitions;
    private final Map<Integer, Color> partitionColors = new HashMap<>();
    
    public ColoredGraphView(Graph graph, Color color) {
        this.graph = graph;
        this.color = color;
        this.visible = true;
        this.showPartitions = false;
    }

    public Graph getGraph() {
        return graph;
    }

    public Color getColor() {
        return color;
    }

    public boolean isVisible() {
        return visible;
    }
    
    public boolean isShowingPartitions() {
        return showPartitions;
    }
    
    public void setShowPartitions(boolean showPartitions) {
        this.showPartitions = showPartitions;
        generatePartitionColors();
    }
    
    private void generatePartitionColors() {
        partitionColors.clear();
        // Find all unique partition values in the graph
        graph.getAdjacencyList().keySet().stream()
            .map(Node::getPartition)
            .distinct()
            .forEach(partition -> {
                float h = (float) partition / 10.0f % 1.0f; // Cycle through hues
                partitionColors.put(partition, Color.getHSBColor(h, 0.8f, 0.9f));
            });
    }
    
    public Color getNodeColor(Node node) {
        if (showPartitions) {
            return partitionColors.getOrDefault(node.getPartition(), color);
        }
        return color;
    }


    private final List<VisibilityChangeListener> listeners = new ArrayList<>();

    public interface VisibilityChangeListener {
        void onVisibilityChanged();
    }

    public void addVisibilityChangeListener(VisibilityChangeListener listener) {
        listeners.add(listener);
    }

    public void toggleVisibility() {
        visible = !visible;
        listeners.forEach(VisibilityChangeListener::onVisibilityChanged);
    }
    
    public void togglePartitions() {
        showPartitions = !showPartitions;
        generatePartitionColors();
        listeners.forEach(VisibilityChangeListener::onVisibilityChanged);
    }
}