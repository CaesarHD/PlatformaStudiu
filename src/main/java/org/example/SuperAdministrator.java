package org.example;

import java.sql.SQLException;

public class SuperAdministrator extends Admin
{
    public SuperAdministrator() {
    }

    public SuperAdministrator(String CNP, String firstName, String secondName, String address, String phoneNumber, String email, String iban, int contractNumber, String password, String userType) {
        super(CNP, firstName, secondName, address, phoneNumber, email, iban, contractNumber, password, userType);
    }

    public String deleteUser(String CNP)
    {
        return "DELETE FROM utilizatori WHERE CNP = '" + CNP + "'";

    }

    public String updateUser(String CNP,String field, String newValue)
    {
        return "UPDATE utilizatori SET " + field + " = '" + newValue + "' WHERE CNP = '" + CNP + "';";

    }

    public String searchUser(String name)
    {

        return "SELECT * FROM utilizatori WHERE nume LIKE '%" + name + "%';";

    }

    public String filterUsers(String userType)
    {
        return "SELECT * FROM utilizatori WHERE tip_utilizator = '" + userType + "';";
    }

    public String assignProfessor(String profCNP,int id)
    {
        return "INSERT INTO profesori_materii (CNP_profesor, id_materie) VALUES ('" + profCNP + "', " + id + ");";
    }

    public String searchCourseByName(String materie)
    {
        return "SELECT m.id, m.nume, u.nume AS nume_profesor, u.prenume AS prenume_profesor " +
                "FROM materii m " + "JOIN profesori_materii pm ON m.id = pm.id_materie " +
                "JOIN utilizatori u ON pm.CNP_profesor = u.CNP " +
                "WHERE m.nume LIKE '%" + materie + "%';";
    }

    public String listStudents(int id)
    {
        return "SELECT u.nume, u.prenume " + "FROM studenti_grupuri_studenti sgs " +
                "JOIN utilizatori u ON sgs.CNP_student = u.CNP " +
                "WHERE sgs.id_grup IN (SELECT id_grup FROM grupuri_studenti WHERE id_activitate = " + id + ");";
    }

}




