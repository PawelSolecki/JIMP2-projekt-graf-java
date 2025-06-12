import gui.ControlPanel;
import gui.GraphPanelWithControls;
import gui.MenuBar;
import io.*;
import model.Graph;
import partition.GraphPartitioner;
import partition.SimpleGraphPartitioner;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainWindow extends JFrame {
    private final GraphPanelWithControls graphPanel;
    private final ControlPanel controlPanel;
    private final GraphUtils graphUtils = new GraphUtils();

    private final GraphPartitioner partitioner = new SimpleGraphPartitioner();

    public MainWindow() {
        super("Graph Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        graphPanel = new GraphPanelWithControls(List.of());
        graphPanel.setGraph(List.of(MockGraphProvider.createSampleGraph1(), MockGraphProvider.createSampleGraph2()));
        controlPanel = new ControlPanel(this::handleGraphPartitioning);
        controlPanel.setGraphViews(graphPanel.getGraphViews());

        setJMenuBar(new MenuBar(this::handleFileSelection, this::handleGraphSaving).getMenuBar());

        add(graphPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.WEST);
        setVisible(true);
    }

    private void handleGraphPartitioning() {
        if (graphPanel.getGraphViews().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No graph available to partition.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int numPartitions;
        while (true) {
            String input = JOptionPane.showInputDialog(
                this,
                "Enter number of partitions:",
                "Graph Partitioning",
                JOptionPane.QUESTION_MESSAGE
            );
            if (input == null || input.trim().isEmpty()) return; // Cancel

            try {
                numPartitions = Integer.parseInt(input.trim());
                if (numPartitions <= 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a positive integer for the number of partitions.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        double marginPercent;
        while (true) {
            String input = JOptionPane.showInputDialog(
                this,
                "Enter margin percent (0-100):",
                "Graph Partitioning",
                JOptionPane.QUESTION_MESSAGE
            );
            if (input == null || input.trim().isEmpty()) return; // Cancel

            try {
                marginPercent = Double.parseDouble(input.trim());
                if (marginPercent < 0 || marginPercent > 100) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid number between 0 and 100 for the margin percent.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        try {
            Graph originalGraph = graphPanel.getGraphViews().get(0).getGraph();
            Graph newGraph = partitioner.partition(originalGraph, numPartitions, marginPercent / 100.0);
            if (newGraph == null) {
                JOptionPane.showMessageDialog(this,
                    "Partitioning failed: partitioner returned null.",
                    "Partition Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            graphPanel.setGraph(graphUtils.separateGraphs(newGraph));
            controlPanel.setGraphViews(graphPanel.getGraphViews());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "An error occurred during graph partitioning: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleGraphSaving() {
        if (graphPanel.getGraphViews().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Brak grafu do zapisania.",
                "Błąd",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz lokalizację do zapisu grafu");
        fileChooser.setSelectedFile(new File("graph.csrrg"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // Użytkownik anulował
        }

        File fileToSave = fileChooser.getSelectedFile();
        // Upewnij się, że plik ma rozszerzenie .csrrg
        if (!fileToSave.getName().toLowerCase().endsWith(".csrrg")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".csrrg");
        }

        Graph graph = graphPanel.getGraphViews().get(0).getGraph(); // zapisujemy pierwszy graf

        try {
            GraphWriter writer = new GraphWriterCSRRG();
            writer.writeGraph(fileToSave.getAbsolutePath(), graph);
            JOptionPane.showMessageDialog(this,
                "Graf został zapisany pomyślnie.",
                "Zapis zakończony",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Wystąpił błąd podczas zapisu grafu: " + e.getMessage(),
                "Błąd zapisu",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Nieoczekiwany błąd: " + e.getMessage(),
                "Błąd",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFileSelection(File selectedFile) {
        try {
            String fileName = selectedFile.getName().toLowerCase();
            GraphReader reader;

            if (fileName.endsWith(".bin")) {
                reader = new GraphReaderBin();
            } else if (fileName.endsWith(".csrrg")) {
                reader = new GraphReaderCSRRG();
            } else {
                throw new IllegalArgumentException("Nieobsługiwane rozszerzenie pliku.");
            }

            graphPanel.setGraph(reader.readGraph(selectedFile.getAbsolutePath()));


        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Błąd podczas wczytywania pliku",
                "Błąd",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(this,
                "Niepoprawny format pliku. Upewnij się, że plik jest w formacie CSRRG.",
                "Błąd",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE);
        }
        controlPanel.setGraphViews(graphPanel.getGraphViews());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
        GraphWriter writer = new GraphWriterCSRRG();
        try {
            writer.writeGraph("sample_graph.csrrg",MockGraphProvider.createSampleGraph1() );
            writer.writeGraph("sample_graph2.csrrg",MockGraphProvider.createSampleGraph2());
        } catch (IOException e) {
            System.err.println("Error writing graph: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
