package gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.function.Consumer;

public class MenuBar extends JMenuBar {
    private final JMenuBar menuBar = new JMenuBar();
    private final Consumer<File> fileSelectionCallback;
    private File selectedFile;

    public MenuBar(Consumer<File> fileSelectionCallback, Runnable handleGraphSaving) {
        super();
        this.fileSelectionCallback = fileSelectionCallback;

        JMenu fileMenu = new JMenu("Plik");
        JMenuItem openItem = new JMenuItem("Wczytaj");
        JMenuItem saveItem = new JMenuItem("Zapisz");
        JMenu helpMenu = new JMenu("Pomoc");
        JMenuItem aboutItem = new JMenuItem("O programie");

        openItem.addActionListener(this::handleOpenFile);

        saveItem.addActionListener(e->handleGraphSaving.run());

        menuBar.add(fileMenu);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        menuBar.add(helpMenu);
        helpMenu.add(aboutItem);
    }

    private void handleOpenFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz plik do wczytania");

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Pliki grafu (*.bin, *.csrrg)", "bin", "csrrg");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            if (fileSelectionCallback != null) {
                fileSelectionCallback.accept(selectedFile);
            }
        }
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

}
