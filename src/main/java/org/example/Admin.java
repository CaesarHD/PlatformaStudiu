package org.example;

public class Admin extends User {
    public Admin() {
    }

    public Admin(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType, DataBase db) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType, db);
    }

    public void deleteUser(String CNP)
    {
        String query = "DELETE FROM utilizatori WHERE CNP = '" + CNP + "'";
        this.db.execute(query);
    }

    public void updateUser(String CNP,String field, String newValue)
    {

    }

}
