package gui;

import model.Graph;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
}