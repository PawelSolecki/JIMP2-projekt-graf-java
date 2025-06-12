package gui;

import model.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Map;

public class GraphPanel extends JPanel {

    private final List<ColoredGraphView> graphViews;

    private double zoom = 1.0;
    private int translateX = 0;
    private int translateY = 0;
    private Point lastDragPoint = null;

    public GraphPanel(List<ColoredGraphView> graphViews) {
        this.graphViews = graphViews;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastDragPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastDragPoint != null) {
                    int dx = e.getX() - lastDragPoint.x;
                    int dy = e.getY() - lastDragPoint.y;
                    translateX += dx;
                    translateY += dy;
                    lastDragPoint = e.getPoint();
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            double delta = 0.1f * e.getPreciseWheelRotation();
            zoom -= delta;
            zoom = Math.max(0.1, Math.min(zoom, 5.0));
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGraphs((Graphics2D) g);
    }

    private void drawGraphs(Graphics2D g2) {
        AffineTransform originalTransform = g2.getTransform();

        g2.translate(translateX, translateY);
        g2.scale(zoom, zoom);

        for (ColoredGraphView view : graphViews) {
            if (!view.isVisible()) continue;

            g2.setColor(Color.GRAY);
            for (Map.Entry<Node, java.util.List<Node>> entry : view.getGraph().getAdjacencyList().entrySet()) {
                Node node = entry.getKey();
                int x = node.getColumn() * GraphStyle.CELL_SIZE + GraphStyle.OFFSET;
                int y = node.getRow() * GraphStyle.CELL_SIZE + GraphStyle.OFFSET;

                for (Node neighbor : entry.getValue()) {
                    int nx = neighbor.getColumn() * GraphStyle.CELL_SIZE + GraphStyle.OFFSET;
                    int ny = neighbor.getRow() * GraphStyle.CELL_SIZE + GraphStyle.OFFSET;
                    g2.drawLine(x, y, nx, ny);
                }
            }

            for (Node node : view.getGraph().getAdjacencyList().keySet()) {
                int x = node.getColumn() * GraphStyle.CELL_SIZE + GraphStyle.OFFSET;
                int y = node.getRow() * GraphStyle.CELL_SIZE + GraphStyle.OFFSET;

                g2.setColor(view.getNodeColor(node));
                g2.fillOval(x - GraphStyle.NODE_SIZE / 2, y - GraphStyle.NODE_SIZE / 2,
                    GraphStyle.NODE_SIZE, GraphStyle.NODE_SIZE);

                g2.setColor(Color.BLACK);
                g2.drawOval(x - GraphStyle.NODE_SIZE / 2, y - GraphStyle.NODE_SIZE / 2,
                    GraphStyle.NODE_SIZE, GraphStyle.NODE_SIZE);

                g2.drawString(String.valueOf(node.getIndex()), x - 5, y + 5);
            }
        }

        g2.setTransform(originalTransform);
    }

    public void refresh() {
        repaint();
    }
}