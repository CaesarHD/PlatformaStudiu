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
                case "profesor" -> new Professor();
                case "administrator" -> new Admin();
                default -> throw new IllegalArgumentException("Unknown user type: " + userType);
            };

            populateUserFields(user, rs);

            if(user instanceof Professor professor) {
                getProffesorDetails(professor);
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


    public void getProffesorDetails(Professor professor) throws SQLException {
        db.execute("use proiect");
        String query = "SELECT * FROM detalii_profesori WHERE CNP = '" + professor.CNP + "';";

        try (Statement stmt = db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (!rs.next()) {
                throw new SQLException("No professor details found for CNP: " + professor.CNP);
            }
            populateProffesorFields(professor, rs);
        }
    }

    private void populateProffesorFields(Professor professor, ResultSet rs) throws SQLException {
        professor.setCNP(rs.getString("CNP"));
        professor.setDepartment(rs.getString("departament"));
        professor.setMinHour(rs.getInt("numar_minim_ore_predate"));
        professor.setMaxHour(rs.getInt("numar_maxim_ore_predate")); // Assuming there's a setMaxHour method
    }

    public List<Subject> getSubjects(String query) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(query);
        List<Subject> subjects = new ArrayList<>();
        while(rs.next()) {
            Subject subject = new Subject();
            subject.setId(rs.getInt("id_materie"));
            populateSubjectDetails(subject);
            subjects.add(subject);
        }

        return subjects;
    }

    private void populateSubjectDetails(Subject subject) {
        db.execute("use proiect");
        String query = "SELECT * FROM materii WHERE id = '" + subject.getId() + "';";

        try (Statement stmt = db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (!rs.next()) {
                throw new SQLException("No subject details found for id: " + subject.getId());
            }
            populateSubjectFields(subject, rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void populateSubjectFields(Subject subject, ResultSet rs) throws SQLException {
        subject.setName(rs.getString("nume"));
        subject.setClassWeight(rs.getInt("pondere_curs"));
        subject.setLabWeight(rs.getInt("pondere_lab"));
        subject.setSemWeight(rs.getInt("pondere_seminar"));
    }

    public void changeLabWeight(Subject subject) {
        db.execute(subject.changeLabWeight());
    }

    public void changeClassWeight(Subject subject) {
        db.execute(subject.changeClassWeight());
    }

    public void changeSemWeight(Subject subject) {
        db.execute(subject.changeSemWeight());
    }

//    public void getStudents() {
//        this.db.execute("SELECT * from utilizatori where");
//    }

}