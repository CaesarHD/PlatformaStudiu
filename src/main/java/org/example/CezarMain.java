package org.example;

import java.util.List;
import java.sql.SQLException;

public class CezarMain {

    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");
        DBController dbController = new DBController(db);


        User user =  dbController.getUser(User.findUser("8400244911342", "Aci6MrX8LW"));
        Professor professor = (Professor) user;
        professor.setSubjects(dbController.getSubjects(professor.getSubject()));
//        System.out.println(professor);
//        professor.printSubjects();

        ProffesorUI pUi = new ProffesorUI(professor);
        pUi.show();

    }
}