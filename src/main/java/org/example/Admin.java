package org.example;

import java.sql.SQLException;

public class Admin extends User
{
    public Admin() {
    }

    public Admin(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType)
    {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType);
    }


    public String addUser() {
        return "INSERT INTO utilizatori (CNP, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, parola, tip_utilizator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    public String deleteUser() {
        return "DELETE FROM utilizatori WHERE CNP = ? AND (tip_utilizator = 'student' OR tip_utilizator = 'profesor')";
    }

    public String updateUser() {
        return "UPDATE utilizatori SET ? = ? WHERE CNP = ? AND (tip_utilizator = 'student' OR tip_utilizator = 'profesor')";
    }

    public String searchUser()
    {
        return "SELECT cnp, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, tip_utilizator " +
                "FROM utilizatori WHERE nume LIKE ? AND prenume LIKE ?;";
    }

    public String filterUser() {
        return "SELECT cnp, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, tip_utilizator " +
                "FROM utilizatori WHERE tip_utilizator = ?;";
    }

    public String assignProfessor() {
        return "INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES (?, ?);";
    }

    public String searchCourseByName() {
        return "SELECT m.id, m.nume, u.nume AS nume_profesor, u.prenume AS prenume_profesor " +
                "FROM materii m " +
                "JOIN profesori_materii pm ON m.id = pm.id_materie " +
                "JOIN utilizatori u ON pm.CNP_profesor = u.CNP " +
                "WHERE m.nume LIKE ?;";
    }

    public String getStudentsForCourseQuery() {
        return "SELECT u.nume, u.prenume " +
                "FROM materii_studenti sm " +
                "JOIN utilizatori u ON sm.CNP_student = u.CNP " +
                "WHERE sm.id_materie = ?;";
    }


}
