import gui.ControlPanel;
import gui.GraphPanelWithControls;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        super("Graph Viewer");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        GraphPanelWithControls graphPanel = new GraphPanelWithControls();
        ControlPanel controlPanel = new ControlPanel();

        add(graphPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        setVisible(true);
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
