package org.example;

import java.sql.SQLException;

public class AdelinMain {

    public static void main(String[] args) throws SQLException {
        // Initialize the database connection
        DataBase db = new DataBase();
        db.connect("root", "root");

        // Initialize the database controller
        DBController dbController = new DBController(db);

        // Retrieve the user (assume valid credentials are provided)
        User user = dbController.getUser(User.findUser("2404023608380", "IfVh3yRlXc"));

        // Ensure the retrieved user is a Student
        if (user instanceof Student student) {
            // Initialize the UI with the student and the database controller
            new StudentUI(student, dbController);
        } else {
            System.out.println("Error: Retrieved user is not a Student.");
        }
    }
}
