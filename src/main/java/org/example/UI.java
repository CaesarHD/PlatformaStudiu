package org.example;

import javax.swing.*;
import java.awt.*;

public class UI {

    private JFrame jFrame;

    public UI() {
        this.jFrame = new JFrame("StudyPlatform");
        jFrame.setSize(1500, 1000);
        jFrame.setVisible(true);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void addComponent(Component component){
        jFrame.add(component);
    }

    public void show() {
        jFrame.setVisible(true);
    }

    public void setLayout(LayoutManager layout){
        jFrame.setLayout(layout);
    }
}
