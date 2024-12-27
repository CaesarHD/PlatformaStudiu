package org.example;

import javax.swing.*;
import java.sql.SQLException;
import java.util.TimeZone;

public class CezarMain {

    public static void main(String[] args) throws SQLException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        DataBase db = new DataBase();
        db.connect("root", "root");
        DBController.setDbConnection(db);

        Professor professor;
        professor = DBController.initializeProfessor("2174897302000", "WZuxthqdob");


//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception e) {
//            System.err.println("Failed to initialize LaF");
//        }
        ProffesorUI pUi = new ProffesorUI(professor);
        pUi.show();

    }
}