package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBController {
    DataBase db;

    public DBController(DataBase db) {
        this.db = db;
    }

    public Professor initializeProfessor(String CNP, String password) throws SQLException {
        User user = getUser(User.findUser(CNP, password));
        Professor professor = (Professor) user;
        getSubjectsFromDB(professor);
        getProfessorActivityFromDB(professor);
        return professor;
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
                case "student" -> new Student();
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

    public void getSubjectsFromDB(Professor professor) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professor.selectSubjects());
        List<Subject> subjects = new ArrayList<>();
        while(rs.next()) {
            Subject subject = new Subject();
            subject.setId(rs.getInt("id_materie"));
            populateSubjectDetails(subject);
            subjects.add(subject);
        }
        professor.setSubjects(subjects);
    }

    public void getProfessorActivityFromDB(Professor professor) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professor.selectProfessorActivities());
        while(rs.next()) {
            ProfessorActivity professorActivity;
            professorActivity = new ProfessorActivity();

            professorActivity.setId(rs.getInt("id_activitate"));
            professorActivity.setType(rs.getString("tip_activitate"));
            professorActivity.setStartDate(rs.getDate("data_inceput"));
            professorActivity.setEndDate(rs.getDate("data_final"));
            professorActivity.setMaxNb(rs.getInt("nr_max_participanti"));
            professorActivity.setDescription(rs.getString("descriere"));
            getProfessorActivityClassId(professorActivity);
            getProfessorActivityClassName(professorActivity);
            getProfessorActivityStudents(professorActivity);
            professor.getProfessorActivities().add(professorActivity);
        }
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

    public void getProfessorActivityClassId(ProfessorActivity professorActivity) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professorActivity.selectClassId());
        if (rs.next()) { // Verifică dacă există o înregistrare
            professorActivity.setClassId(rs.getInt("id"));
        } else {
            System.out.println("Nu există înregistrări pentru id-ul specificat.");
            professorActivity.setClassId(-1); // Sau o altă valoare implicită
        }
    }

    public void getProfessorActivityClassName(ProfessorActivity professorActivity) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professorActivity.selectClassName());
        if (rs.next()) { // Verifică dacă există o înregistrare
            professorActivity.setClassName(rs.getString("nume"));
        } else {
            System.out.println("Nu există înregistrări pentru id-ul specificat.");
            professorActivity.setClassId(-1); // Sau o altă valoare implicită
        }
    }

    public void getProfessorActivityStudents (ProfessorActivity professorActivity) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professorActivity.selectStudentsandGrades());
        while(rs.next()) {
            Student student = new Student();
            student.setCNP(rs.getString("CNP"));
            student.setFirstName(rs.getString("prenume"));
            student.setSecondName(rs.getString("nume"));
            int grade = rs.getInt("nota");
            professorActivity.getGrades().put(student, grade);
            professorActivity.getStudents().add(student);
        }
    }

    public void changeGrades(ProfessorActivity professorActivity, Student student) {
        db.execute(professorActivity.changeGrade(student));
    }

//    public void getStudents() {
//        this.db.execute("SELECT * from utilizatori where");
//    }


    public void searchUser(String query) throws SQLException
    {
        db.execute("use proiect");
        db.execute(query);

    }


}