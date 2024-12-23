package org.example;

import javax.swing.*;
import java.awt.*;

public class DayLabel extends JLabel {
    public DayLabel(String text, Color background, Color foreground, boolean btn) {
        setText(text);
        setHorizontalAlignment(JLabel.CENTER);
        setFont(new Font("Helvetica", Font.BOLD, 20));
        setOpaque(true);
        setBackground(background);
        setForeground(foreground);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
