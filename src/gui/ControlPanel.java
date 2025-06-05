package gui;

import model.Graph;
import partition.GraphPartitioner;
import partition.SimpleGraphPartitioner;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private final List<ColoredGraphView> graphViews = new ArrayList<>();
    private final JPanel controls;

    public ControlPanel(Runnable handleGraphPartitioning2) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(200, getHeight()));

        controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JButton splitButton = new JButton("Podziel graf");
        splitButton.addActionListener(e -> handleGraphPartitioning2.run());
//        splitButton.addActionListener(e -> handleGraphPartitioning());
        controls.add(splitButton);
        controls.add(Box.createVerticalStrut(10));
        
        JButton togglePartitionsButton = new JButton("Pokaż/ukryj partycje");
        togglePartitionsButton.addActionListener(e -> {
            if (!graphViews.isEmpty()) {
                graphViews.get(0).togglePartitions();
                SwingUtilities.getAncestorOfClass(JFrame.class, this).repaint();
            }
        });
        controls.add(togglePartitionsButton);
        controls.add(Box.createVerticalStrut(10));

        add(controls);
    }

    public void setGraphViews(List<ColoredGraphView> graphViews) {
        this.graphViews.clear();
        this.graphViews.addAll(graphViews);
        refreshButtons();
    }

    private void refreshButtons() {
        // Zachowaj przyciski "Podziel graf" i "Pokaż/ukryj partycje"
        Component[] oldComponents = controls.getComponents();
        controls.removeAll();

        // Dodaj z powrotem przyciski
        if (oldComponents.length > 0) {
            controls.add(oldComponents[0]); // "Podziel graf" button
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
    
    private void handleGraphPartitioning() {
        if (graphViews.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No graph available to partition.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get the selected graph (just take the first one for simplicity)
        ColoredGraphView selectedView = graphViews.get(0);
        Graph originalGraph = selectedView.getGraph();
        
        // Ask user for number of partitions
        String input = JOptionPane.showInputDialog(
            this,
            "Enter number of partitions:",
            "Graph Partitioning",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input == null || input.trim().isEmpty()) {
            return; // User canceled
        }
        
        try {
            int numPartitions = Integer.parseInt(input.trim());
            if (numPartitions <= 0) {
                throw new NumberFormatException("Number of partitions must be positive");
            }
            
            // Ask for margin percent
            input = JOptionPane.showInputDialog(
                this,
                "Enter margin percent (0-100):",
                "Graph Partitioning",
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (input == null || input.trim().isEmpty()) {
                return; // User canceled
            }
            
            double marginPercent = Double.parseDouble(input.trim());
            if (marginPercent < 0 || marginPercent > 100) {
                throw new NumberFormatException("Margin percent must be between 0 and 100");
            }
            
            // Perform partitioning
            GraphPartitioner partitioner = new SimpleGraphPartitioner();
            Graph newGraph = partitioner.partition(originalGraph, numPartitions, marginPercent / 100.0);

            setGraphViews(List.of(new ColoredGraphView(originalGraph, selectedView.getColor())));
            
            // Update the view to show partitions
//            selectedView.setShowPartitions(true);
            
            // Refresh the display
//            SwingUtilities.getAncestorOfClass(JFrame.class, this).repaint();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid input: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}