package gui;

import model.Graph;

import java.awt.*;

public class ColoredGraphView {
    private final Graph graph;
    private final Color color;
    private boolean visible;

    public ColoredGraphView(Graph graph, Color color) {
        this.graph = graph;
        this.color = color;
        this.visible = true;
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

    public void toggleVisibility() {
        visible = !visible;
    }
}