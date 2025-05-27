package gui;

import javax.swing.*;
import java.awt.*;

public class ColorToggleButton extends JButton {
    private final Color color;
    private boolean isOn = true;

    public ColorToggleButton(Color color) {
        this.color = color;
        setIcon(new ColorIcon(color, 16, 16));
        setToggled(true);

        setFocusPainted(false);
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    public void setToggled(boolean on) {
        this.isOn = on;
        setText(on ? "ON" : "OFF");
        setEnabled(true);
        setForeground(on ? Color.BLACK : Color.GRAY);
    }

    public boolean isToggled() {
        return isOn;
    }

    // Prosta klasa do rysowania kolorowego kwadracika
    private static class ColorIcon implements Icon {
        private final Color color;
        private final int width;
        private final int height;

        public ColorIcon(Color color, int width, int height) {
            this.color = color;
            this.width = width;
            this.height = height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width - 1, height - 1);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }
}