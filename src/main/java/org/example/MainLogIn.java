package org.example;

public class MainLogIn {
    public static void main(String[] args) {

        DataBase db = new DataBase();

        try {

            System.out.println("Connecting to the database...");
            db.connect("root", "root");

            new LogInUI();

        } catch (Exception e) {

            System.err.println("An error occurred while connecting to the database:");
            e.printStackTrace();
        }
    }
}