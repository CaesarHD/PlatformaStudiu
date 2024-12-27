package org.example;

import javax.swing.*;
import java.awt.*;

public class DayLabel extends JLabel {
    private boolean busyMarker = false;

    public DayLabel(String text, Color background, Color foreground, boolean btn) {
        setText(text);
        setHorizontalAlignment(JLabel.CENTER);
        setFont(new Font("Helvetica", Font.BOLD, 20));
        setOpaque(true);
        setBackground(background);
        setForeground(foreground);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (busyMarker) {
            g.setColor(Color.CYAN);
            g.fillOval(10, 10, 10, 10);
        }
    }

    public void setBusyMarker(boolean busyMarker) {
        this.busyMarker = busyMarker;
    }
}
