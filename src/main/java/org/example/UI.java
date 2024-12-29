package org.example;

import javax.swing.*;
import java.awt.*;

public class UI {

    private JFrame jFrame;
    private JPanel jPanel;
    public UI() {
        this.jPanel = new JPanel();
        this.jFrame = new JFrame("StudyPlatform");
        jFrame.setSize(1500, 1000);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.add(jPanel);
    }



    public void addComponent(Component component){
        jPanel.add(component);
    }

    public void show() {
        jFrame.setVisible(true);
    }

    public void setLayout(LayoutManager layout){
        jFrame.setLayout(layout);
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    public void setjFrame(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    public JPanel getjPanel() {
        return jPanel;
    }

    public void setjPanel(JPanel jPanel) {
        this.jPanel = jPanel;
    }
}
