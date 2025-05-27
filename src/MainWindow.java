import gui.GraphPanelWithControls;
import gui.MenuBar;
import io.GraphReader;
import io.GraphReaderCSRRG;
import gui.ControlPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainWindow extends JFrame {
    private final GraphReader reader = new GraphReaderCSRRG();
    private final GraphPanelWithControls graphPanel;
    private final ControlPanel controlPanel;

    public MainWindow() {
        super("Graph Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        graphPanel = new GraphPanelWithControls(List.of());
        controlPanel = new ControlPanel();

        setJMenuBar(new MenuBar(this::handleFileSelection).getMenuBar());

        add(graphPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.WEST);
        setVisible(true);
    }

    private void handleFileSelection(File selectedFile) {
        try {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
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
