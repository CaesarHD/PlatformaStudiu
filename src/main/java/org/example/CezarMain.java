package org.example;

import java.sql.SQLException;

public class CezarMain {

    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");
        DBController dbController = new DBController(db);


        Professor professor;
        professor = dbController.initializeProfessor("2174897302000", "WZuxthqdob");

        ProffesorUI pUi = new ProffesorUI(professor, dbController);
        pUi.show();

    }
}