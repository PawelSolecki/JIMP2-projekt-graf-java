package gui;

import javax.swing.*;
import java.awt.*;

public class ColorToggleButton extends JButton {
    private boolean isOn = true;

    public ColorToggleButton(Color color) {
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

    private record ColorIcon(Color color, int width, int height) implements Icon {

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