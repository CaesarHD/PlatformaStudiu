package org.example;

import javax.xml.crypto.Data;

public class Professor extends User{

    public Professor() {
    }

    public Professor(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String userType, DataBase db) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, userType, db);
    }
    

    @Override
    public void delete() {

    }
}
