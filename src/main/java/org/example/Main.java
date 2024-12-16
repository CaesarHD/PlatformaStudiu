package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");

        JFrame jFrame = new JFrame("asd");
        jFrame.setSize(1000, 1000);
        JButton jButton = new JButton("new user");
        jButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                db.execute("insert into materii (nume, id) values ('Cezar', 11);");
            }
        });
        jFrame.setLayout(new FlowLayout());
        jFrame.add(jButton);
        jFrame.setVisible(true);

    }
}