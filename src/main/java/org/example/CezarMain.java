package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class CezarMain {

    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");
        ProffesorUI pUi = new ProffesorUI();

        pUi.show();
    }
}