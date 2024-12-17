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
    String password;
    String userType;
    DataBase db;

    public User() {
    }

    public User(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType, DataBase db) {
        this.CNP = CNP;
        this.firstName = firstName;
        this.secondName = secondName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.iban = iban;
        this.contractNumber = contractNumber;
        this.password = password;
        this.userType = userType;
        this.db = db;
    }

    public void add() {
        this.db.execute("insert into utilizatori " +
                "(CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator)" +
                " values (" + "'" + this.CNP + "'" + "," + "'" + this.secondName + "'" + "," +
                "'" + this.firstName + "'" + "," + "'" + this.address + "'" + "," + "'" + this.phoneNumber + "'"
                + "," + "'" + this.email + "'" + "," + "'" + this.password + "'" + "," + "'" + this.iban + "'" + "," +
                "'" + this.contractNumber + "'" + "," + "'" + this.userType + "'" + ");");
    }

    public void delete() {

    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public int getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(int contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public DataBase getDb() {
        return db;
    }

    public void setDb(DataBase db) {
        this.db = db;
    }
}
