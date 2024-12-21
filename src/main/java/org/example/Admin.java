package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Admin extends User {
    public Admin() {
    }

    public Admin(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType, DataBase db) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType, db);
    }

    public void add() {
        this.db.execute("insert into utilizatori " +
                "(CNP, nume, prenume, adresa, numar_telefon, email, parola, IBAN, numar_contract, tip_utilizator)" +
                " values (" + "'" + this.CNP + "'" + "," + "'" + this.secondName + "'" + "," +
                "'" + this.firstName + "'" + "," + "'" + this.address + "'" + "," + "'" + this.phoneNumber + "'"
                + "," + "'" + this.email + "'" + "," + "'" + this.password + "'" + "," + "'" + this.iban + "'" + "," +
                "'" + this.contractNumber + "'" + "," + "'" + this.userType + "'" + ");");
    }

    public void deleteUser(String CNP)
    {
        String query = "DELETE FROM utilizatori WHERE CNP = '" + CNP + "'";
        this.db.execute(query);
    }

    public void updateUser(String CNP,String field, String newValue)
    {
        String query ="UPDATE utilizatori SET " + field + " = '" + newValue + "' WHERE CNP = '" + CNP + "';";
        this.db.execute(query);
    }

    public void searchUser(String name) throws SQLException
    {
       String query = "SELECT * FROM utilizatori WHERE nume LIKE '%" + name + "%';";
       this.db.execute(query);
    }

    public void filterUsers(String userType) throws SQLException
    {
        String query ="SELECT * FROM utilizatori WHERE tip_utilizator = '" + userType + "';";
        this.db.execute(query);
    }

    public void assignProfessor(String profCNP,int id)
    {
        String query = "INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('" + profCNP + "', " + id + ");";
        this.db.execute(query);
    }

    public void searchCourseByName(String materie) throws SQLException
    {
        String query = "SELECT m.id, m.nume, u.nume AS nume_profesor, u.prenume AS prenume_profesor " +
                        "FROM materii m " + "JOIN profesori_materii pm ON m.id = pm.id_materie " +
                        "JOIN utilizatori u ON pm.CNP_profesor = u.CNP " +
                        "WHERE m.nume LIKE '%" + materie + "%';";
        this.db.execute(query);
    }

    public void listStudents(int id) throws  SQLException
    {
        String query = "SELECT u.nume, u.prenume " + "FROM studenti_grupuri_studenti sgs " +
                        "JOIN utilizatori u ON sgs.CNP_student = u.CNP " +
                        "WHERE sgs.id_grup IN (SELECT id_grup FROM grupuri_studenti WHERE id_activitate = " + id + ");";
        this.db.execute(query);
    }


}
