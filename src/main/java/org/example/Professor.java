package org.example;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Professor extends User{

    int maxHour;
    int minHour;
    String department;

    public Professor() {
    }

    public Professor(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String userType, DataBase db) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, userType, db);
    }

    public void insertDetails() {
        try {
            Statement st = db.getCon().createStatement();
            st.execute("use proiect");
            st.execute("insert into detalii_profesori (numar_maxim_ore_predate, numar_minim_ore_predate, departament)" +
                    "values( " + "'" + this.maxHour + "'" + "'" + this.minHour + "'" + "'" + this.department + "');");
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet getDetails() {
        try {
            Statement st = db.getCon().createStatement();
            st.execute("use proiect");
            ResultSet rs = st.executeQuery("SELECT * from detalii_profesori where CNP = " + "'" + this.CNP + "';");
            st.close();
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getStudents() {
        this.db.execute("SELECT * from utilizatori where");
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

