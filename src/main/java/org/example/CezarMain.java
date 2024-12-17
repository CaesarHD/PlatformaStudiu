package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class CezarMain {

    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");

        Professor cezar = new Professor("5040420125807", "Cezar", "Stir", "str. Fazanilor, nr. 2", "0767332422", "stir.cezar@gmail.com", "CezarIban", 3495, "cezar", "profesor", db);
        Professor pr1 = new Professor();
        cezar.add();
//        UI ui = new UI();
//
//        JButton jButton = new JButton("new user");
//        jButton.addActionListener(new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                cezar.add();
//            }
//        });
//        ui.setLayout(new FlowLayout());
//        ui.addComponent(jButton);
//        ui.show();
    }
}
