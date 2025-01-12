package org.example.out.production.example.org.example;

import org.example.DBController;
import org.example.DataBase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class GetUserTest {

    DataBase db = new DataBase();

    public GetUserTest() throws SQLException {
        db.connect("root", "root");
        DBController.setDbConnection(db);
    }

    @Test
    public void testCNP() throws SQLException {

        assert DBController.getUser("SELECT * FROM utilizatori where CNP = 2174897302000").getCNP().equals("2174897302000");
    }

    @Test
    public void testFirstName() throws SQLException {
        assert DBController.getUser("SELECT * FROM utilizatori where CNP = 2174897302000").getFirstName().equals("Cristiana");
    }

}
