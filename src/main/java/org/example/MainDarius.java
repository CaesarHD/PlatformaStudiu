package org.example;

import javax.swing.*;

public class MainDarius {
    public static void main(String[] args) {
        try {

            DataBase db = new DataBase();
            db.connect("root", "root");


           // Admin admin = new Admin("5050118245033", "Darius", "SefuVost", "str. Dinamo, nr. 1", "0756992299", "popdarius1801@gmail.com", "DariusIban", 6969, "darius", "administrator");
            SuperAdministrator sadmin = new SuperAdministrator("5050118245031", "Darius", "Sefu", "str. Dinamo, nr. 1", "0756992298", "p0pdarius1801@gmail.com", "DariusIbann", 6960, "dariuss", "super-administrator");
            DBController dbController = new DBController(db);


          // SwingUtilities.invokeLater(() -> new AdminUI(admin));
            SwingUtilities.invokeLater(() -> new SuperAdministratorUI(sadmin));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
