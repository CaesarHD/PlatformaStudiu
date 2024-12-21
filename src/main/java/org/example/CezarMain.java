package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class CezarMain {

    public static void main(String[] args) throws SQLException {
        DataBase db = new DataBase();
        db.connect("root", "root");
        DBController dbController = new DBController(db);


        User user =  dbController.getUser(User.findUser("3951032160081", "EdHIlvuG8R"));

        Proffesor proffesor = (Proffesor) user;

        System.out.println(proffesor);

    }
}