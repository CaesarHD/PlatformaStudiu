package org.example;

import java.sql.SQLException;

public class AdelinMain {

    public static void main(String[] args) throws SQLException {

        DataBase db = new DataBase();
        db.connect("root", "root");

        DBController dbController = new DBController(db);

        User user = dbController.getUser(User.findUser("2404023608380", "IfVh3yRlXc"));

        if (user instanceof Student student) {
            new StudentUI(student);
        } else {
            System.out.println("Error: Retrieved user is not a Student.");
        }
    }
}
