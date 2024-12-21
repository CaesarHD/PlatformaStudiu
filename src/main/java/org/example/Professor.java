package org.example;

import java.util.List;
public class Professor extends User{

    int maxHour;
    int minHour;
    String department;


    public Professor(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType);
    }

    public String getSubject() {
        return ("select * from profesori_materii where CNP_profesor = '" + this.CNP + "';");
    }

    public void printSubjects() {
        for(Subject s : subjects){
            System.out.println(s);
        }
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    List<Subject> subjects;

    public Professor() {
    }

    @Override
    public String toString() {
        return "Professor{" +
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
                ", department='" + department +

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

