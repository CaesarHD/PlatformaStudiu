package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Proffesor extends User{

    int maxHour;
    int minHour;
    String department;

    public Proffesor() {
    }


    public Proffesor(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType);
    }


    @Override
    public String toString() {
        return "Proffesor{" +
                "CNP='" + CNP + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", iban='" + iban + '\'' +
                ", contractNumber=" + contractNumber +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                ", maxHour=" + maxHour +
                ", minHour=" + minHour +
                ", department='" + department + '\'' +
                '}';
    }

    public int getMaxHour() {
        return maxHour;
    }

    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }

    public int getMinHour() {
        return minHour;
    }

    public void setMinHour(int minHour) {
        this.minHour = minHour;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

