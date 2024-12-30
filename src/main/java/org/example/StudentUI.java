package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentUI {

    private JFrame jFrame;
    private JPanel mainPanel; // Panel to dynamically update content
    private JMenuBar menuBar;
    private JButton subjectsButton;
    private JButton studentsGroupsButton;
    private JButton gradesButton;
    private JButton availableGroupsButton;
    JButton availableCoursesButton;

    private Student student;
    private DBController dbController;

    public StudentUI(Student student, DBController dbController) {
        this.student = student;
        this.dbController = dbController;

        // Initialize JFrame
        jFrame = new JFrame("StudyPlatform");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        // Initialize Menu Bar
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        // Initialize Buttons
        subjectsButton = new JButton("My Subjects");
        studentsGroupsButton = new JButton("My Groups");
        gradesButton = new JButton("Grades");
        availableGroupsButton = new JButton("Available Groups");
        availableCoursesButton = new JButton("Available Courses");

        menuBar.add(subjectsButton);
        menuBar.add(studentsGroupsButton);
        menuBar.add(gradesButton);
        menuBar.add(availableGroupsButton);
        menuBar.add(availableCoursesButton);


        // Wrap the Menu Bar in a JPanel
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        menuPanel.add(menuBar);

        // Add the Menu Panel to the Frame at the Top
        jFrame.add(menuPanel, BorderLayout.NORTH);

        // Initialize Main Panel (Initially Empty)
        mainPanel = new JPanel(new BorderLayout());
        jFrame.add(mainPanel, BorderLayout.CENTER);

        // Add Action Listeners
        subjectsButton.addActionListener(e -> displaySubjectsTable());
        gradesButton.addActionListener(e -> displayGradesTable());
        studentsGroupsButton.addActionListener(e -> displayGroupsTable());
        availableGroupsButton.addActionListener(e -> displayAvailableGroups());
        availableCoursesButton.addActionListener(e -> displayAvailableCourses());

        // Show JFrame
        jFrame.setVisible(true);
    }


    private void displaySubjectsTable() {
        try {
            // Fetch subjects, final grades, and IDs
            List<Object[]> subjectsAndGrades = fetchSubjectsAndGradesFromDatabase();

            // Create Table Model with Three Columns: "Subjects", "Final Grade", and "Actions"
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // Only "Actions" column is editable
                }
            };
            tableModel.addColumn("Subjects");       // Column 1
            tableModel.addColumn("Final Grade");    // Column 2
            tableModel.addColumn("Actions");        // Column 3

            // Add rows to the table model
            for (Object[] row : subjectsAndGrades) {
                String subjectName = (String) row[0];
                int finalGrade = (int) row[1];
                int subjectId = (int) row[2];
                tableModel.addRow(new Object[]{subjectName, finalGrade, "Leave"}); // Add "Leave" button
            }

            // Create JTable with the model
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new LeaveButtonEditor(new JCheckBox(), subjectsAndGrades));

            // Clear previous content in the main panel and add the new table
            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching subjects and grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private List<Object[]> fetchSubjectsAndGradesFromDatabase() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String query = student.getSubjectsAndGrades();

        // Execute the query and fetch results
        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int subjectId = rs.getInt("subject_id");
                String subjectName = rs.getString("subject_name");
                int finalGrade = rs.getInt("final_grade");
                data.add(new Object[]{subjectName, finalGrade, subjectId});
            }
        }

        return data;
    }


    private void displayGradesTable() {
        try {
            // Fetch grades, subjects, and activities
            List<Object[]> gradesData = fetchGradesFromDatabase();

            // Create Table Model with Three Columns: "Subject", "Activity Type", "Grade"
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Subject");        // Column 1
            tableModel.addColumn("Activity Type"); // Column 2
            tableModel.addColumn("Grade");         // Column 3

            // Add rows to the table model
            for (Object[] row : gradesData) {
                tableModel.addRow(row);
            }

            // Create JTable with the model
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Clear previous content in the main panel and add the new table
            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Object[]> fetchGradesFromDatabase() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String query = student.getAllGrades(); // Use the method from Student class to get the query

        // Execute the query and fetch results
        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String subjectName = rs.getString("subject_name");  // Get subject name
                String activityType = rs.getString("activity_type"); // Get activity type
                Float grade = rs.getFloat("grade");                // Get grade (can be null)
                data.add(new Object[]{subjectName, activityType, grade != 0 ? grade : "N/A"}); // Handle null grades
            }
        }

        return data;
    }

    private void displayGroupsTable() {
        try {
            // Fetch group data
            List<Object[]> groups = fetchGroupsWithIdsFromDatabase();

            // Create Table Model with Four Columns: "Subject", "View Members", "Chat", and "Leave"
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column >= 1; // Allow editing only for the "Actions" columns
                }
            };
            tableModel.addColumn("Subject");       // Column for subject names
            tableModel.addColumn("View Members"); // Column for "View Members" button
            tableModel.addColumn("Chat");         // Column for "Chat" button
            tableModel.addColumn("Leave");        // Column for "Leave" button

            // Add rows to the table model
            for (Object[] group : groups) {
                String subjectName = (String) group[0];
                int groupId = (int) group[1];
                tableModel.addRow(new Object[]{subjectName, "View Members", "Chat", "Leave"}); // Add placeholder buttons
            }

            // Create JTable with the model
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "View Members", "Chat", and "Leave" columns
            table.getColumn("View Members").setCellRenderer(new ButtonRenderer());
            table.getColumn("View Members").setCellEditor(new GroupActionButtonEditor(new JCheckBox(), groups, "View Members"));
            table.getColumn("Chat").setCellRenderer(new ButtonRenderer());
            table.getColumn("Chat").setCellEditor(new GroupActionButtonEditor(new JCheckBox(), groups, "Chat"));
            table.getColumn("Leave").setCellRenderer(new ButtonRenderer());
            table.getColumn("Leave").setCellEditor(new GroupActionButtonEditor(new JCheckBox(), groups, "Leave"));

            // Add JTable to JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.removeAll();
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Refresh the main panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching groups: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private List<Object[]> fetchGroupsWithIdsFromDatabase() throws SQLException {
        List<Object[]> groups = new ArrayList<>();
        String query = student.getStudentGroups();
        // Execute the query and fetch results
        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String subjectName = rs.getString("subject_name"); // Get subject name
                int groupId = rs.getInt("group_id");              // Get group ID
                groups.add(new Object[]{subjectName, groupId});   // Add to the list
            }
        }

        return groups;
    }

    private void displayGroupMembers(int groupId) {
        try {
            // Fetch members of the group
            List<String[]> members = fetchGroupMembersFromDatabase(groupId);

            // Create Table Model with Three Columns: "First Name", "Last Name", "Email"
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");
            tableModel.addColumn("Email");

            // Add rows to the table model
            for (String[] member : members) {
                tableModel.addRow(member);
            }

            // Create JTable with the model
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Clear previous content in the main panel and add the new table
            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching members: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String[]> fetchGroupMembersFromDatabase(int groupId) throws SQLException {
        List<String[]> members = new ArrayList<>();
        String query = student.getAllStudentsForThisGroup(groupId);

        // Execute the query and fetch results
        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                members.add(new String[]{firstName, lastName, email});
            }
        }

        return members;
    }


    private void leaveCourse(int subjectId, String subjectName) throws SQLException {
        // Delete from materii_studenti
        String deleteFromMateriiStudenti = "DELETE FROM materii_studenti WHERE CNP_student = ? AND id_materie = ?";
        try (PreparedStatement stmt = dbController.db.getCon().prepareStatement(deleteFromMateriiStudenti)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }

        // Delete from note_activitati
        String deleteFromNoteActivitati = "DELETE na " +
                "FROM note_activitati na " +
                "JOIN activitati_profesori ap ON na.id_activitate = ap.id_activitate " +
                "WHERE na.CNP_student = ? AND ap.id_materie = ?";
        try (PreparedStatement stmt = dbController.db.getCon().prepareStatement(deleteFromNoteActivitati)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(jFrame, "Successfully left the course: " + subjectName, "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh the subjects table
        displaySubjectsTable();
    }


    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }


    private void enterGroupChat(int groupId) {
        // Create a new frame or panel for the group chat
        JFrame chatFrame = new JFrame("Group Chat - Group " + groupId);
        chatFrame.setSize(500, 400);
        chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Chat UI components
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        // Add components to the chat frame
        chatFrame.setLayout(new BorderLayout());
        chatFrame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatFrame.add(inputPanel, BorderLayout.SOUTH);

        // Load existing messages
        try {
            List<String[]> messages = dbController.getMessagesForGroup(groupId);
            for (String[] message : messages) {
                String sender = message[0];
                String content = message[1];
                String timestamp = message[2];
                chatArea.append("[" + timestamp + "] " + sender + ": " + content + "\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(chatFrame, "Error loading messages: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        sendButton.addActionListener(e -> {
            String message = inputField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    dbController.saveMessage(groupId, student.getFirstName() + " " + student.getSecondName(), message);
                    chatArea.append("[Now] You: " + message + "\n");
                    inputField.setText("");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(chatFrame, "Error saving message: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        chatFrame.setVisible(true);
    }

    class LeaveButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> subjectsAndGrades;

        public LeaveButtonEditor(JCheckBox checkBox, List<Object[]> subjectsAndGrades) {
            super(checkBox);
            this.subjectsAndGrades = subjectsAndGrades;

            // Initialize the button
            button = new JButton();
            button.setOpaque(true);

            // Add action listener to handle the button click
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && "Leave".equals(label)) {
                // Get the selected subject's ID and name
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                String subjectName = (String) subjectsAndGrades.get(rowIndex)[0];
                int subjectId = (int) subjectsAndGrades.get(rowIndex)[2];

                // Perform the leave action
                try {
                    leaveCourse(subjectId, subjectName);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(button, "Error leaving course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }


    class GroupActionButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> groups;
        private String actionType; // "View Members", "Chat", or "Leave"

        public GroupActionButtonEditor(JCheckBox checkBox, List<Object[]> groups, String actionType) {
            super(checkBox);
            this.groups = groups;
            this.actionType = actionType;
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // Get the group ID for the clicked row
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int groupId = (int) groups.get(rowIndex)[1];

                switch (actionType) {
                    case "View Members" -> displayGroupMembers(groupId); // Show members of the group
                    case "Chat" -> enterGroupChat(groupId);              // Enter group chat
                    case "Leave" -> leaveGroup(groupId);                // Leave the group
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void leaveGroup(int groupId) {
        try {
            // Delete the student from the group
            String query = "DELETE FROM studenti_grupuri_studenti WHERE CNP_student = ? AND id_grup = ?";
            try (PreparedStatement stmt = dbController.db.getCon().prepareStatement(query)) {
                stmt.setString(1, student.getCNP());
                stmt.setInt(2, groupId);
                stmt.executeUpdate();
            }

            // Show success message
            JOptionPane.showMessageDialog(jFrame, "Successfully left the group!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh the groups table
            displayGroupsTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error leaving group: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAvailableGroups() {
        try {
            // Fetch available groups
            List<Object[]> availableGroups = fetchAvailableGroupsFromDatabase();

            // Create Table Model with Two Columns: "Subject Name" and "Actions"
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1; // Make only "Actions" column editable
                }
            };
            tableModel.addColumn("Subject Name"); // Column for subject names
            tableModel.addColumn("Actions");      // Column for the "Join Group" button

            // Add rows to the table model
            for (Object[] group : availableGroups) {
                String subjectName = (String) group[0];
                int groupId = (int) group[1];
                tableModel.addRow(new Object[]{subjectName, "Join Group"}); // Add placeholder button
            }

            // Create JTable with the model
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new JoinGroupButtonEditor(new JCheckBox(), availableGroups));

            // Add JTable to JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.removeAll();
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Refresh the main panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching available groups: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private List<Object[]> fetchAvailableGroupsFromDatabase() throws SQLException {
        List<Object[]> groups = new ArrayList<>();
        String query = "SELECT DISTINCT m.nume AS subject_name, gs.id_grup AS group_id " +
                "FROM grupuri_studenti gs " +
                "JOIN materii m ON gs.id_materie = m.id " +
                "WHERE gs.id_grup NOT IN (" +
                "    SELECT id_grup " +
                "    FROM studenti_grupuri_studenti " +
                "    WHERE CNP_student = '" + student.getCNP() + "'" +
                ")";

        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String subjectName = rs.getString("subject_name");
                int groupId = rs.getInt("group_id");
                groups.add(new Object[]{subjectName, groupId});
            }
        }

        return groups;
    }


    class JoinGroupButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> availableGroups;

        public JoinGroupButtonEditor(JCheckBox checkBox, List<Object[]> availableGroups) {
            super(checkBox);
            this.availableGroups = availableGroups;
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && "Join Group".equals(label)) {
                // Get the selected group's ID
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int groupId = (int) availableGroups.get(rowIndex)[1];

                // Perform join action
                try {
                    joinGroup(groupId);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(button, "Error joining group: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void joinGroup(int groupId) throws SQLException {
        String query = "INSERT INTO studenti_grupuri_studenti (CNP_student, id_grup) VALUES (?, ?)";
        try (PreparedStatement stmt = dbController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, groupId);
            stmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(jFrame, "Successfully joined the group!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh available groups table
        displayAvailableGroups();
    }

    private List<Object[]> fetchAvailableCoursesFromDatabase() throws SQLException {
        List<Object[]> courses = new ArrayList<>();
        String query = "SELECT m.nume AS course_name, m.id AS course_id " +
                "FROM materii m " +
                "WHERE m.id NOT IN (" +
                "    SELECT ms.id_materie " +
                "    FROM materii_studenti ms " +
                "    WHERE ms.CNP_student = '" + student.getCNP() + "'" +
                ")";

        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String courseName = rs.getString("course_name");
                int courseId = rs.getInt("course_id");
                courses.add(new Object[]{courseName, courseId});
            }
        }

        return courses;
    }

    private void displayAvailableCourses() {
        try {
            // Fetch available courses
            List<Object[]> availableCourses = fetchAvailableCoursesFromDatabase();

            // Create Table Model with Two Columns: "Course Name" and "Actions"
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1; // Make only the "Actions" column editable
                }
            };
            tableModel.addColumn("Course Name"); // Column for subject names
            tableModel.addColumn("Actions");     // Column for the "Enroll" button

            // Add rows to the table model
            for (Object[] course : availableCourses) {
                String courseName = (String) course[0];
                int courseId = (int) course[1];
                tableModel.addRow(new Object[]{courseName, "Enroll"}); // Add placeholder button
            }

            // Create JTable with the model
            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new EnrollButtonEditor(new JCheckBox(), availableCourses));

            // Add JTable to JScrollPane
            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.removeAll();
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Refresh the main panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching available courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    class EnrollButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> availableCourses;

        public EnrollButtonEditor(JCheckBox checkBox, List<Object[]> availableCourses) {
            super(checkBox);
            this.availableCourses = availableCourses;
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && "Enroll".equals(label)) {
                // Get the selected course's ID
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int courseId = (int) availableCourses.get(rowIndex)[1];

                // Perform enroll action
                try {
                    enrollInCourse(courseId);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(button, "Error enrolling in course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void enrollInCourse(int courseId) throws SQLException {
        String query = "INSERT INTO materii_studenti (CNP_student, id_materie) VALUES (?, ?)";
        try (PreparedStatement stmt = dbController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(jFrame, "Successfully enrolled in the course!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Refresh available courses table
        displayAvailableCourses();
    }


}
