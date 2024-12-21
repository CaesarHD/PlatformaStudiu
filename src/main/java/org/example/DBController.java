package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBController {
    DataBase db;

    public DBController(DataBase db) {
        this.db = db;
    }

    public User getUser(String query) throws SQLException {
        User user = null;
        db.execute("use proiect");

        try (Statement stmt = db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (!rs.next()) {
                throw new SQLException("No user found for the given query.");
            }

            String userType = rs.getString("tip_utilizator");
            user = switch (userType) {
                case "profesor" -> new Proffesor();
                case "administrator" -> new Admin();
                case "student" -> new Student();
                default -> throw new IllegalArgumentException("Unknown user type: " + userType);
            };

            populateUserFields(user, rs);

            if(user instanceof Proffesor proffesor) {
                getProffesorDetails(proffesor);
            }
        }

        return user;
    }

    private void populateUserFields(User user, ResultSet rs) throws SQLException {
        user.setCNP(rs.getString("CNP"));
        user.setFirstName(rs.getString("prenume"));
        user.setSecondName(rs.getString("nume"));
        user.setAddress(rs.getString("adresa"));
        user.setPhoneNumber(rs.getString("numar_telefon"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("parola"));
        user.setIban(rs.getString("IBAN"));
        user.setContractNumber(rs.getInt("numar_contract"));
        user.setUserType(rs.getString("tip_utilizator"));

    }


    public void getProffesorDetails(Proffesor proffesor) throws SQLException {
        db.execute("use proiect");
        String query = "SELECT * FROM detalii_profesori WHERE CNP = '" + proffesor.CNP + "';";

        try (Statement stmt = db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (!rs.next()) {
                throw new SQLException("No professor details found for CNP: " + proffesor.CNP);
            }
            populateProffesorFields(proffesor, rs);
        }
    }

    private void populateProffesorFields(Proffesor proffesor, ResultSet rs) throws SQLException {
        proffesor.setCNP(rs.getString("CNP"));
        proffesor.setDepartment(rs.getString("departament"));
        proffesor.setMinHour(rs.getInt("numar_minim_ore_predate"));
        proffesor.setMaxHour(rs.getInt("numar_maxim_ore_predate")); // Assuming there's a setMaxHour method
    }




//    public void getStudents() {
//        this.db.execute("SELECT * from utilizatori where");
//    }

}