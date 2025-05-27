package gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private final List<ColoredGraphView> graphViews = new ArrayList<>();
    private final JPanel controls;

    public ControlPanel() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, getHeight()));

        controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JButton splitButton = new JButton("Podziel graf");
        controls.add(splitButton);
        controls.add(Box.createVerticalStrut(10));

        add(controls);
    }

    public void setGraphViews(List<ColoredGraphView> graphViews) {
        this.graphViews.clear();
        this.graphViews.addAll(graphViews);
        refreshButtons();
    }

    private void refreshButtons() {
        // Zachowaj przycisk "Podziel graf"
        Component[] oldComponents = controls.getComponents();
        controls.removeAll();

        // Dodaj z powrotem przycisk "Podziel graf"
        if (oldComponents.length > 0) {
            controls.add(oldComponents[0]);
            controls.add(Box.createVerticalStrut(10));
        }

        // Dodaj nowe przyciski
        for (ColoredGraphView view : graphViews) {
            ColorToggleButton toggleButton = new ColorToggleButton(view.getColor());
            toggleButton.addActionListener(e -> {
                view.toggleVisibility();
                toggleButton.setToggled(view.isVisible());
            });

            toggleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            controls.add(toggleButton);
            controls.add(Box.createVerticalStrut(5));
        }

        revalidate();
        repaint();
    }
}