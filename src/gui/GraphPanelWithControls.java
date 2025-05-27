package gui;

import io.GraphReaderCSRRG;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphPanelWithControls extends JPanel {

    public GraphPanelWithControls() {
        setLayout(new BorderLayout());

        List<ColoredGraphView> graphViews = new ArrayList<>();
//        graphViews.add(new ColoredGraphView(MockGraphProvider.createSampleGraph1(), generateRandomColor()));
//        graphViews.add(new ColoredGraphView(MockGraphProvider.createSampleGraph2(), generateRandomColor()));
        try {
            graphViews.add(
                new ColoredGraphView(
                    new GraphReaderCSRRG().readGraph("src/resources/graf.csrrg"),
                    generateRandomColor()
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        GraphPanel graphPanel = new GraphPanel(graphViews);
        add(graphPanel, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout());

        for (ColoredGraphView view : graphViews) {
            ColorToggleButton toggleButton = new ColorToggleButton(view.getColor());
            toggleButton.addActionListener(e -> {
                view.toggleVisibility();
                toggleButton.setToggled(view.isVisible());
                graphPanel.refresh();
            });
            controls.add(toggleButton);
        }

        add(controls, BorderLayout.SOUTH);
    }

    private Color generateRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));
    }
}