package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.sql.*;

public class Professor extends User {

    private int maxHour;
    private int minHour;
    private String department;
    private List<Subject> subjects;
    private List<ProfessorActivity> professorActivities;
    private List<Meeting> meetings;

    public Professor() {
        subjects = new ArrayList<>();
        professorActivities = new ArrayList<>();
        meetings = new ArrayList<>();
    }

    public Professor(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType);
        subjects = new ArrayList<>();
        professorActivities = new ArrayList<>();
        meetings = new ArrayList<>();
    }

    public String insertMeeting(Meeting meeting) {
        return "INSERT INTO programari (id_activitate, data_inceput, data_final, descriere_programare, nr_participanti) " +
                "VALUES (?, ?, ?, ?, ?)";
    }

    public String selectSubjects() {
        return ("select * from profesori_materii where CNP_profesor = '" + this.CNP + "';");
    }

    public String selectProfessorActivities() {
        return ("select * from activitati_profesori where CNP_profesor = '" + this.CNP + "';");
    }

    public String selectMeetings() {
        return ("select * from programari join activitati_profesori\n" +
                "    on programari.id_activitate = activitati_profesori.id_activitate\n" +
                "         where CNP_profesor = '" + this.CNP + "';");
    }

    public void printSubjects() {
        for (Subject s : subjects) {
            System.out.println(s);
        }
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
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

    public void setMaxHour(int maxHour) {
        this.maxHour = maxHour;
    }

    public void setMinHour(int minHour) {
        this.minHour = minHour;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<ProfessorActivity> getProfessorActivities() {
        return professorActivities;
    }

    public ProfessorActivity getProfessorActivityById(int id) {
        for(ProfessorActivity professorActivity : professorActivities){
            if(professorActivity.getId() == id) {
                return professorActivity;
            }
        }
        return null;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }
}
