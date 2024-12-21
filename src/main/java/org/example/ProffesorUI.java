package org.example;

import javax.swing.*;
import java.awt.*;

public class ProffesorUI extends UI{
    JButton courses = new JButton("Courese");
    JButton currentDay = new JButton("Current Day");
    JButton meetings = new JButton("Meetings");
    JButton allActivities = new JButton("All Activities");
    JButton classBook = new JButton("Class Book");
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Menu");

    public ProffesorUI() {
        this.addComponent(menuBar);
        menuBar.add(menu);
        this.addComponent(courses);
        this.addComponent(currentDay);
        this.addComponent(meetings);
        this.addComponent(allActivities);
//        this.addComponent(classBook);
    }

}
