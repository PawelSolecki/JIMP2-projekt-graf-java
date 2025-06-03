package gui;

import model.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphPanelWithControls extends JPanel {
    private final List<ColoredGraphView> graphViews = new ArrayList<>();

    public GraphPanelWithControls(List<Graph> graphs) {
        setLayout(new BorderLayout());


//        graphViews.add(new ColoredGraphView(MockGraphProvider.createSampleGraph1(), generateRandomColor()));
//        graphViews.add(new ColoredGraphView(MockGraphProvider.createSampleGraph2(), generateRandomColor()));
////        try {
////            graphViews.add(
////                new ColoredGraphView(
////                    new GraphReaderCSRRG().readGraph("src/resources/graf.csrrg"),
////                    generateRandomColor()
////                )
////            );
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
        for (Graph graph : graphs) {
            graphViews.add(new ColoredGraphView(graph, generateRandomColor()));
        }
        GraphPanel graphPanel = new GraphPanel(graphViews);
        add(graphPanel, BorderLayout.CENTER);


    }

    private Color generateRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));
    }

    public List<ColoredGraphView> getGraphViews() {
        return graphViews;
    }

    private void addGraphView(ColoredGraphView view) {
        view.addVisibilityChangeListener(this::repaint);
        graphViews.add(view);
    }

    public void setGraph(List<Graph> graphs) {
        this.graphViews.clear();
        graphs.forEach(g -> addGraphView(new ColoredGraphView(g, generateRandomColor())));
        repaint();
    }

}