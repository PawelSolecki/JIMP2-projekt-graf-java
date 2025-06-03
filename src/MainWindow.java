import gui.GraphPanelWithControls;
import gui.MenuBar;
import io.GraphReader;
import io.GraphReaderBin;
import io.GraphReaderCSRRG;
import gui.ControlPanel;
import io.MockGraphProvider;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainWindow extends JFrame {
    private final GraphPanelWithControls graphPanel;
    private final ControlPanel controlPanel;

    public MainWindow() {
        super("Graph Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        graphPanel = new GraphPanelWithControls(List.of());
        graphPanel.setGraph(List.of(MockGraphProvider.createSampleGraph1(), MockGraphProvider.createSampleGraph2()));
        controlPanel = new ControlPanel();
        controlPanel.setGraphViews(graphPanel.getGraphViews());

        setJMenuBar(new MenuBar(this::handleFileSelection).getMenuBar());

        add(graphPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.WEST);
        setVisible(true);
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

            graphPanel.setGraph(List.of(reader.readGraph(selectedFile.getAbsolutePath())));


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
    }
}
