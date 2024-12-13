package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {
    private Connection con;

    public void connect(String username, String password) throws SQLException {
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306",
                    username,
                    password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
