package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MainDarius {
    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");

        Admin darius = new Admin("5050118245033", "Darius", "SefuVost", "str. Dinamo, nr. 1", "0756992299", "popdarius1801@gmail.com", "DariusIban", 6969, "darius", "administrator",db);
        darius.add();



    }
}

