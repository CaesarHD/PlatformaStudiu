package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MainDarius {
    public static void dariusMain() throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");

        Professor darius = new Professor("5050118245033", "Darius", "SefuVost", "str. Dinamo, nr. 1", "0767332424", "popdarius1801@gmail.com", "DariusIban", 6969, "profesor", db);

        UI ui = new UI();
        JButton jButton = new JButton("new user");
        jButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                darius.add();
            }
        });
        ui.setLayout(new FlowLayout());
        ui.addComponent(jButton);
        ui.show();

    }
}

