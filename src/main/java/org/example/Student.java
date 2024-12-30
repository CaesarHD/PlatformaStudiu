package org.example;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Student extends User{
    private int year_of_studies;
    private int nr_hours_sustained;

    public Student() {

    }

    public Student(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType);
        this.year_of_studies = contractNumber;
        this.nr_hours_sustained = contractNumber;
    }
    public int getYear_of_studies() {
        return year_of_studies;
    }
    public void setYear_of_studies(int year_of_studies) {
        this.year_of_studies = year_of_studies;
    }
    public int getNr_hours_sustained() {
        return nr_hours_sustained;
    }
    public void setNr_hours_sustained(int nr_hours_sustained) {
        this.nr_hours_sustained = nr_hours_sustained;
    }

    public String getSubjectsFinalGrade() {
        return  "SELECT m.nume, ms.nota_finala FROM materii_studenti ms " +
                "JOIN materii m ON ms.id_materie = m.id " +
                "WHERE ms.CNP_student = '" + this.CNP + "';";

    }

    public String getAllGrades() {
        return "SELECT m.nume AS subject_name, ap.tip_activitate AS activity_type, na.nota AS grade FROM materii m " +
                "JOIN activitati_profesori ap ON m.id = ap.id_materie " +
                "LEFT JOIN note_activitati na ON ap.id_activitate = na.id_activitate " +
                " WHERE na.CNP_student = '" + this.CNP + "';";

    }

    public String getStudentGroups() {
        return "SELECT m.nume AS subject_name, gs.id_grup AS group_id " +
                "FROM studenti_grupuri_studenti sgs " +
                "JOIN grupuri_studenti gs ON sgs.id_grup = gs.id_grup " +
                "JOIN materii m ON gs.id_materie = m.id " +
                "WHERE sgs.CNP_student = '" + this.CNP + "';";
    }

    public String getAllStudentsForThisGroup(int id) {
        return "SELECT u.nume AS last_name, u.prenume AS first_name, u.email AS email FROM " +
        " studenti_grupuri_studenti sgs JOIN detalii_studenti ds ON sgs.CNP_student = ds.CNP JOIN " +
                " utilizatori u ON ds.CNP = u.CNP WHERE sgs.id_grup = " + id + ";";
    }

    public String getSubjectsAndGrades() {
        return "SELECT m.id AS subject_id, m.nume AS subject_name, ms.nota_finala AS final_grade " +
                "FROM materii_studenti ms " +
                "JOIN materii m ON ms.id_materie = m.id " +
                "WHERE ms.CNP_student = '" + this.CNP + "'";
    }


}
