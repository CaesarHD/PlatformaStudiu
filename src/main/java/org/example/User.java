package org.example;

public abstract class User {
    String CNP;
    String firstName;
    String secondName;
    String address;
    String phoneNumber;
    String email;
    String iban;
    int contractNumber;
    String userType;
    DataBase db;

    public User() {
    }

    public User(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String userType, DataBase db) {
        this.CNP = CNP;
        this.firstName = firstName;
        this.secondName = secondName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.iban = iban;
        this.contractNumber = contractNumber;
        this.userType = userType;
        this.db = db;
    }

    public void add() {
        this.db.execute("insert into utilizatori " +
                "(CNP, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, tip_utilizator)" +
                " values ('" + "'" + this.CNP + "'" + "," + "'" + this.secondName + "'" + "," +
                "'" + this.firstName + "'" + "," + "'" + this.address + "'" + "," + "'" +this.phoneNumber + "'"
                + "," + "'" + this.email + "'" + "," + "'" + this.userType + "'" + "');");
    }

    public void delete() {

    }


}
