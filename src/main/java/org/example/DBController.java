package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class DBController {
    static DataBase db;

    public DBController(DataBase db) {
        DBController.db = db;
    }

    public static void setDbConnection(DataBase newConnection) {
        db = newConnection;
    }

    public static Professor initializeProfessor(String email, String password) throws SQLException {
        User user = getUser(User.findUser(email, password));
        Professor professor = (Professor) user;
        getSubjectsFromDB(professor);
        getProfessorActivityFromDB(professor);
        getMeetingsFromDB(professor);
        return professor;
    }

    public static User getUser(String query) throws SQLException {
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
                case "super-administrator" -> new SuperAdministrator();
                default -> throw new IllegalArgumentException("Unknown user type: " + userType);
            };

            populateUserFields(user, rs);

            if(user instanceof Professor professor) {
                getProffesorDetails(professor);
            }
        }

        return user;
    }


    public static List<Object[]> fetchActivitiesForProfessor(Professor professor) throws SQLException {
        List<Object[]> activities = new ArrayList<>();
        String query = """
        SELECT
            a.id_activitate,
            a.data,
            a.numar_ore,
            a.numar_minim_participanti,
            a.numar_participanti,
            a.id_grup,
            a.timp_expirare,
            a.nume
        FROM
            activitati_studenti a
        JOIN
            studenti_activitati_studenti pgs ON a.id_activitate = pgs.id_activitate
        WHERE
            pgs.CNP_student = ?;
    """;

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, professor.getCNP());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("nume");
                String date = rs.getString("data");
                int duration = rs.getInt("numar_ore");
                int minParticipants = rs.getInt("numar_minim_participanti");
                int currentParticipants = rs.getInt("numar_participanti");
                String expirationTime = rs.getString("timp_expirare");

                boolean isCanceled = currentParticipants < minParticipants
                        && LocalDateTime.parse(expirationTime.replace(" ", "T")).isBefore(LocalDateTime.now());

                activities.add(new Object[]{
                        name,
                        date,
                        duration,
                        minParticipants,
                        currentParticipants,
                        expirationTime,
                        isCanceled ? "This activity was canceled" : "active"
                });
            }
        }

        return activities;
    }

    static List<Object[]> getProfessorStudentActivities(Professor professor) throws SQLException {
        List<Object[]> activities = new ArrayList<>();
        String query = """
            SELECT
                     w.*
                 FROM
                     activitati_studenti w
                 JOIN
                     profesori_grupuri_studenti pg
                 ON
                     w.id_activitate = pg.id_activitate
                 WHERE
                     pg.CNP_profesor = ?;
   \s""";

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, professor.getCNP());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("nume");
                String date = rs.getString("data");
                int duration = rs.getInt("numar_ore");
                int minParticipants = rs.getInt("numar_minim_participanti");
                int currentParticipants = rs.getInt("numar_participanti");
                String expirationTime = rs.getString("timp_expirare");
                int activityId = rs.getInt("id_activitate");

                boolean isCanceled = currentParticipants < minParticipants
                        && LocalDateTime.parse(expirationTime.replace(" ", "T")).isBefore(LocalDateTime.now());

                activities.add(new Object[]{
                        name,
                        date,
                        duration,
                        minParticipants,
                        currentParticipants,
                        expirationTime,
                        isCanceled ? "This activity was canceled" : "active",
                        activityId
                });
            }
        }

        return activities;
    }

    public static void deleteInvitation(String CNP, int activityId){
        System.out.println(CNP + " " + activityId);
        try {
            db.execute("use proiect");
            db.execute("DELETE FROM profesori_grupuri_studenti where CNP_profesor = '" + CNP + "' and id_activitate = " + activityId +";");
        }
        catch (RuntimeException e) {
            System.out.println("nu mere");
        }
    }

    public static void selectStudentActivitiesEnrolled() {
        db.execute("SELECT * from ");
    }

    public static void addProfessorToStudentActivity(Professor professor, int activityId) {
        try {
            String enrollQuery = """
                INSERT INTO studenti_activitati_studenti (CNP_student, id_activitate)
                VALUES (?, ?);
            """;
            try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(enrollQuery)) {
                stmt.setString(1, professor.getCNP());
                stmt.setInt(2, activityId);
                stmt.executeUpdate();
            }

            String updateParticipantsQuery = """
                UPDATE activitati_studenti
                SET numar_participanti = numar_participanti + 1
                WHERE id_activitate = ?;
            """;
            try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(updateParticipantsQuery)) {
                stmt.setInt(1, activityId);
                int rowsUpdated = stmt.executeUpdate();
                System.out.println("Updated participants count for activity ID: " + activityId + ". Rows affected: " + rowsUpdated);
            }
        } catch (SQLException ex) {
            System.out.println("Nu se poate adauga profesorul.");
        }
    }

    private static void populateUserFields(User user, ResultSet rs) throws SQLException {
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


    public static void getProffesorDetails(Professor professor) throws SQLException {
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

    private static void populateProffesorFields(Professor professor, ResultSet rs) throws SQLException {
        professor.setCNP(rs.getString("CNP"));
        professor.setDepartment(rs.getString("departament"));
        professor.setMinHour(rs.getInt("numar_minim_ore_predate"));
        professor.setMaxHour(rs.getInt("numar_maxim_ore_predate"));
    }

    public static void getSubjectsFromDB(Professor professor) throws SQLException {
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

    public static void getProfessorActivityFromDB(Professor professor) throws SQLException {
        db.execute("use proiect");
        ResultSet rs;

        try (Statement stmt = db.getCon().createStatement()) {
            rs = stmt.executeQuery(professor.selectProfessorActivities());

            while (rs.next()) {
                ProfessorActivity professorActivity;
                professorActivity = new ProfessorActivity();

                professorActivity.setId(rs.getInt("id_activitate"));
                professorActivity.setType(rs.getString("tip_activitate"));

                professorActivity.setMaxNb(rs.getInt("nr_max_participanti"));
                professorActivity.setDescription(rs.getString("descriere"));
                getProfessorActivityClassId(professorActivity);
                getProfessorActivityClassName(professorActivity);
                getProfessorActivityStudents(professorActivity);
                professor.getProfessorActivities().add(professorActivity);
            }
        }
    }

    public static void getMeetingsFromDB(Professor professor) throws SQLException {
        db.execute("use proiect");
        ResultSet rs;

        try (Statement stmt = db.getCon().createStatement()) {
            rs = stmt.executeQuery(professor.selectMeetings());

            while (rs.next()) {
                Meeting meeting;
                meeting = new Meeting();

                meeting.setId(rs.getInt("id_programare"));
                meeting.setType(rs.getString("tip_activitate"));

                Instant startDate = Instant.ofEpochMilli(rs.getTimestamp("data_inceput").getTime());
                LocalDateTime startLocalDate = LocalDateTime.ofInstant(startDate, ZoneOffset.UTC);
                meeting.setStartDate(startLocalDate);

                Instant endDate = Instant.ofEpochMilli(rs.getTimestamp("data_final").getTime());
                LocalDateTime endLocalDate = LocalDateTime.ofInstant(endDate, ZoneOffset.UTC);
                meeting.setEndDate(endLocalDate);

                meeting.setMaxNb(rs.getInt("nr_max_participanti"));
                meeting.setCrtNb(rs.getInt("nr_participanti"));
                meeting.setDescription(rs.getString("descriere_programare"));
                meeting.setClassId(rs.getInt("id_materie"));
                getMeetingClassName(meeting);

                professor.getMeetings().add(meeting);
            }
        }
    }

    private static void populateSubjectDetails(Subject subject) {
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

    private static void populateSubjectFields(Subject subject, ResultSet rs) throws SQLException {
        subject.setName(rs.getString("nume"));
        subject.setClassWeight(rs.getInt("pondere_curs"));
        subject.setLabWeight(rs.getInt("pondere_lab"));
        subject.setSemWeight(rs.getInt("pondere_seminar"));
    }

    public static void changeLabWeight(Subject subject) {
        db.execute(subject.changeLabWeight());
    }

    public static void changeClassWeight(Subject subject) {
        db.execute(subject.changeClassWeight());
    }

    public static void changeSemWeight(Subject subject) {
        db.execute(subject.changeSemWeight());
    }

    public static void getProfessorActivityClassId(ProfessorActivity professorActivity) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professorActivity.selectClassId());
        if (rs.next()) {
            professorActivity.setClassId(rs.getInt("id"));
        } else {
            System.out.println("Nu există înregistrări pentru id-ul specificat.");
            professorActivity.setClassId(-1);
        }
    }

    public static void getProfessorActivityClassName(ProfessorActivity professorActivity) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(professorActivity.selectClassName());
        if (rs.next()) {
            professorActivity.setClassName(rs.getString("nume"));
        } else {
            System.out.println("Nu există înregistrări pentru id-ul specificat.");
            professorActivity.setClassId(-1);
        }
    }

    public static void saveMessage(int groupId, String sender, String message) throws SQLException {
        String query = "INSERT INTO group_messages (id_grup, sender, message) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = db.getCon().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setString(2, sender);
            stmt.setString(3, message);
            stmt.executeUpdate();
        }
    }
    public static void getMeetingClassName(Meeting meeting) throws SQLException {
        db.execute("use proiect");
        Statement stmt = db.getCon().createStatement();
        ResultSet rs = stmt.executeQuery(meeting.selectClassName());
        if (rs.next()) {
            meeting.setClassName(rs.getString("nume"));
        } else {
            System.out.println("Nu există înregistrări pentru id-ul specificat.");
            meeting.setClassId(-1);
        }
    }

    public static void getProfessorActivityStudents (ProfessorActivity professorActivity) throws SQLException {
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

    public static void deleteMeeting (Meeting meeting, Professor professor) throws SQLException {
        db.execute("use proiect");
        db.execute(meeting.deleteMeeting());
        professor.getMeetings().remove(meeting);
    }

    public static void createNewMeeting (Professor professor, Meeting newMeeting) throws SQLException {
        db.execute("use proiect");
        String insertQuery = professor.insertMeeting(newMeeting);

        try (PreparedStatement ps = db.getCon().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, newMeeting.getProfessorActivityId());
            ps.setTimestamp(2, Timestamp.valueOf(newMeeting.getStartDate()));
            ps.setTimestamp(3, Timestamp.valueOf(newMeeting.getEndDate()));
            ps.setString(4, newMeeting.getDescription());
            ps.setInt(5, newMeeting.getCrtNb());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        newMeeting.setId(generatedId);
                        System.out.println("Generated ID: " + generatedId);
                    } else {
                        System.out.println("No ID was generated.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        public static void changeGrades(ProfessorActivity professorActivity, Student student) {
        db.execute(professorActivity.changeGrade(student));
    }

    public static void updateMeeting(Meeting meeting) {
        db.execute(meeting.updateMeeting());
    }

    public static void addUser(Admin admin, User user) throws SQLException {

        if (!user.getUserType().equalsIgnoreCase("student") &&
                !user.getUserType().equalsIgnoreCase("profesor")) {
            throw new IllegalArgumentException("Only users of type 'student' or 'profesor' can be added!");
        }

        String query = admin.addUser();
        db.execute("use proiect");

        try (PreparedStatement pstmt = db.getCon().prepareStatement(query)) {

            pstmt.setString(1, user.getCNP());
            pstmt.setString(2, user.getSecondName());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getIban());
            pstmt.setInt(8, user.getContractNumber());
            pstmt.setString(9, user.getPassword());
            pstmt.setString(10, user.getUserType());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user: " + e.getMessage());
        }
    }

    public static void deleteUser(Admin admin, String CNP) throws SQLException {
        db.execute("use proiect");


        String checkQuery = "SELECT tip_utilizator FROM utilizatori WHERE CNP = ?";
        try (PreparedStatement checkPstmt = db.getCon().prepareStatement(checkQuery)) {
            checkPstmt.setString(1, CNP);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("tip_utilizator");
                if (userType.equalsIgnoreCase("administrator") || userType.equalsIgnoreCase("super-administrator")) {
                    throw new SecurityException("You cannot delete users of type 'administrator' or 'super-administrator'!");
                }
            } else {
                throw new IllegalArgumentException("No user found with the given CNP!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user type: " + e.getMessage());
        }


        String query = admin.deleteUser();
        try (PreparedStatement pstmt = db.getCon().prepareStatement(query)) {
            pstmt.setString(1, CNP);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    public static void updateUser(Admin admin, String CNP, String field, String newValue) throws SQLException {
        db.execute("use proiect");


        String checkQuery = "SELECT tip_utilizator FROM utilizatori WHERE CNP = ?";
        try (PreparedStatement checkPstmt = db.getCon().prepareStatement(checkQuery)) {
            checkPstmt.setString(1, CNP);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("tip_utilizator");
                if (userType.equalsIgnoreCase("administrator") || userType.equalsIgnoreCase("super-administrator")) {
                    throw new SecurityException("You cannot update users of type 'administrator' or 'super-administrator'!");
                }
            } else {
                throw new IllegalArgumentException("No user found with the given CNP!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user type: " + e.getMessage());
        }


        String query = admin.updateUser();
        try (PreparedStatement pstmt = db.getCon().prepareStatement(query)) {
            pstmt.setString(1, newValue);
            pstmt.setString(2, CNP);
            pstmt.setString(3, field);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    public static ResultSet searchUser(Admin admin, String name, String lastName) throws SQLException {
        String query = admin.searchUser();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, "%" + name + "%");
        pstmt.setString(2, "%" + lastName + "%");
        return pstmt.executeQuery();
    }

    public static ResultSet filterUser(Admin admin, String userType) throws SQLException {
        String query = admin.filterUser();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, userType);
        return pstmt.executeQuery();
    }

    public static void assignProfessor(Admin admin, String profCNP, int id) throws SQLException {
        String query = admin.assignProfessor();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, profCNP);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public static ResultSet searchCourseByName(Admin admin, String courseName) throws SQLException {
        String query = admin.searchCourseByName();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, "%" + courseName + "%");
        return pstmt.executeQuery();
    }

    public static ResultSet getStudentsForCourse(Admin admin, int courseId) throws SQLException {
        String query = admin.getStudentsForCourseQuery();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setInt(1, courseId);
        return pstmt.executeQuery();
    }

    public static List<String[]> getMessagesForGroup(int groupId) throws SQLException {
        List<String[]> messages = new ArrayList<>();
        String query = "SELECT sender, message, timestamp FROM group_messages WHERE id_grup = ? ORDER BY timestamp ASC";
        try (PreparedStatement stmt = db.getCon().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");
                messages.add(new String[]{sender, message, timestamp});
            }
        }
        return messages;
    }


    public static void addUser2(SuperAdministrator sadmin, User user) throws SQLException
    {
        String query = sadmin.addUser2();
        db.execute("use proiect");

        try (PreparedStatement pstmt = db.getCon().prepareStatement(query))
        {
            pstmt.setString(1, user.getCNP());
            pstmt.setString(2, user.getSecondName());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getEmail());
            pstmt.setString(7, user.getIban());
            pstmt.setInt(8, user.getContractNumber());
            pstmt.setString(9, user.getPassword());
            pstmt.setString(10, user.getUserType());

            pstmt.executeUpdate();
        } catch (SQLException e)
        {
            throw new RuntimeException("Error adding user: " + e.getMessage());
        }
    }

    public static void deleteUser2(SuperAdministrator sadmin, String CNP) throws SQLException
    {
        db.execute("use proiect");

        String checkQuery = "SELECT tip_utilizator FROM utilizatori WHERE CNP = ?";
        try (PreparedStatement checkPstmt = db.getCon().prepareStatement(checkQuery)) {
            checkPstmt.setString(1, CNP);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next())
            {
                String userType = rs.getString("tip_utilizator");
                if ( userType.equalsIgnoreCase("super-administrator"))
                {
                    throw new SecurityException("You cannot delete users of type 'super-administrator'!");
                }
            } else {
                throw new IllegalArgumentException("No user found with the given CNP!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user type: " + e.getMessage());
        }


        String query = sadmin.deleteUser2();
        try (PreparedStatement pstmt = db.getCon().prepareStatement(query))
        {
            pstmt.setString(1, CNP);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    public static void updateUser2(SuperAdministrator sadmin, String CNP, String field, String newValue) throws SQLException
    {
        db.execute("use proiect");

        String checkQuery = "SELECT tip_utilizator FROM utilizatori WHERE CNP = ?";
        try (PreparedStatement checkPstmt = db.getCon().prepareStatement(checkQuery))
        {
            checkPstmt.setString(1, CNP);
            ResultSet rs = checkPstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("tip_utilizator");
                if (userType.equalsIgnoreCase("super-administrator")) {
                    throw new SecurityException("You cannot update users of type 'super-administrator' !");
                }
            } else {
                throw new IllegalArgumentException("No user found with the given CNP!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user type: " + e.getMessage());
        }


        String query = sadmin.updateUser2();
        try (PreparedStatement pstmt = db.getCon().prepareStatement(query))
        {
            pstmt.setString(1, newValue);
            pstmt.setString(2, CNP);
            pstmt.setString(3, field);
            pstmt.executeUpdate();
        } catch (SQLException e)
        {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    public static ResultSet searchUser2(SuperAdministrator sadmin, String name, String lastName) throws SQLException
    {
        String query = sadmin.searchUser2();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, "%" + name + "%");
        pstmt.setString(2, "%" + lastName + "%");
        return pstmt.executeQuery();
    }

    public static ResultSet filterUser2(SuperAdministrator sadmin, String userType) throws SQLException
    {
        String query = sadmin.filterUser2();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, userType);
        return pstmt.executeQuery();
    }

    public static void assignProfessor2(SuperAdministrator sadmin, String profCNP, int id) throws SQLException
    {
        String query = sadmin.assignProfessor2();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, profCNP);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public static ResultSet searchCourseByName2(SuperAdministrator sadmin, String courseName) throws SQLException
    {
        String query = sadmin.searchCourseByName2();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setString(1, "%" + courseName + "%");
        return pstmt.executeQuery();
    }

    public static ResultSet getStudentsForCourse2(SuperAdministrator sadmin, int courseId) throws SQLException
    {
        String query = sadmin.getStudentsForCourseQuery2();
        db.execute("use proiect");
        PreparedStatement pstmt = db.getCon().prepareStatement(query);
        pstmt.setInt(1, courseId);
        return pstmt.executeQuery();
    }

    public static String retrieveUserType(String email, String password) {
        String userType = null;
        String query = "SELECT tip_utilizator FROM proiect.utilizatori WHERE utilizatori.email = ? AND utilizatori.parola = ?";

        try (PreparedStatement stmt = db.getCon().prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userType = rs.getString("tip_utilizator");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while retrieving user type: " + e.getMessage(), e);
        }
        System.out.println(userType);
        return userType;
    }

    public static boolean doesCNPExist(SuperAdministrator sadmin, String cnp) throws SQLException {
        String query = "SELECT COUNT(*) FROM utilizatori WHERE cnp = ? AND tip_utilizator = 'profesor'";
        db.execute("use proiect");
        try (PreparedStatement stmt = db.getCon().prepareStatement(query)) {
            stmt.setString(1, cnp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public static boolean doesCNPExist2(Admin admin, String cnp) throws SQLException {
        String query = "SELECT COUNT(*) FROM utilizatori WHERE cnp = ? AND tip_utilizator = 'profesor'";
        db.execute("use proiect");
        try (PreparedStatement stmt = db.getCon().prepareStatement(query)) {
            stmt.setString(1, cnp);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

}