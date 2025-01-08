package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
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
    private JButton profileButton;
    private JButton logoutButton;
    private JButton meetingsButton;
    private JButton availableMeetingsButton;
    private JButton availableCoursesButton;

    private Student student;


    public StudentUI(Student student) {
        this.student = student;

        // Initialize JFrame
        jFrame = new JFrame("StudyPlatform");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        // Initialize Menu Bar
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        Font buttonFont = new Font("Arial", Font.BOLD, 18); // Larger text
        Color buttonBackground = Color.LIGHT_GRAY;

        // Initialize Buttons
        subjectsButton = new JButton("My Subjects");
        subjectsButton.setFont(buttonFont);
        subjectsButton.setBackground(buttonBackground);

        studentsGroupsButton = new JButton("My Groups");
        studentsGroupsButton.setFont(buttonFont);
        studentsGroupsButton.setBackground(buttonBackground);

        gradesButton = new JButton("Grades");
        gradesButton.setFont(buttonFont);
        gradesButton.setBackground(buttonBackground);

        availableGroupsButton = new JButton("Available Groups");
        availableGroupsButton.setFont(buttonFont);
        availableGroupsButton.setBackground(buttonBackground);

        availableCoursesButton = new JButton("Available Courses");
        availableCoursesButton.setFont(buttonFont);
        availableCoursesButton.setBackground(buttonBackground);

        profileButton = new JButton("Profile");
        profileButton.setFont(buttonFont);
        profileButton.setBackground(buttonBackground);

        logoutButton = new JButton("Log Out");
        logoutButton.setFont(buttonFont);
        logoutButton.setBackground(buttonBackground);

        meetingsButton = new JButton("Meetings");
        meetingsButton.setFont(buttonFont);
        meetingsButton.setBackground(buttonBackground);

        availableMeetingsButton = new JButton("Available Meetings");
        availableMeetingsButton.setFont(buttonFont);
        availableMeetingsButton.setBackground(buttonBackground);

        menuBar.setFont(new Font("Arial", Font.BOLD, 16));

        menuBar.add(subjectsButton);
        menuBar.add(gradesButton);
        menuBar.add(meetingsButton);
        menuBar.add(availableMeetingsButton);
        menuBar.add(studentsGroupsButton);
        menuBar.add(availableGroupsButton);
        menuBar.add(availableCoursesButton);
        menuBar.add(profileButton);
        menuBar.add(logoutButton);

        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        menuPanel.add(menuBar);

        jFrame.add(menuPanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new BorderLayout());
        jFrame.add(mainPanel, BorderLayout.CENTER);

        subjectsButton.addActionListener(e -> displaySubjectsTable());
        gradesButton.addActionListener(e -> displayGradesTable());
        studentsGroupsButton.addActionListener(e -> displayGroupsTable());
        availableGroupsButton.addActionListener(e -> displayAvailableGroups());
        availableCoursesButton.addActionListener(e -> displayAvailableCourses());
        profileButton.addActionListener(e -> displayProfileDetails());
        logoutButton.addActionListener(e -> handleLogout());
        meetingsButton.addActionListener(e -> displayMeetingsTable());
        availableMeetingsButton.addActionListener(e -> displayAvailableMeetingsTable());

        jFrame.setVisible(true);
    }


    private void displaySubjectsTable() {
        try {
            // Clear the main panel
            mainPanel.removeAll();

            // Panel for search functionality
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField searchField = new JTextField(20); // Width is still 20 columns
            searchField.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase font size
            searchField.setPreferredSize(new Dimension(300, 30));

            JButton searchButton = new JButton("Search");
            Font buttonFont = new Font("Arial", Font.PLAIN, 18); // Larger text
            Color buttonBackground = Color.LIGHT_GRAY;
            searchButton.setFont(buttonFont);
            searchButton.setBackground(buttonBackground);

            JLabel searchLabel = new JLabel("Search by Subject:");
            searchLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font and size
            searchLabel.setForeground(Color.BLACK); // Set text color (optional)
            searchPanel.add(searchLabel);

//            searchPanel.add(new JLabel("Search by Subject: "));
//            searchPanel.setFont(new Font("Arial", Font.PLAIN, 18));
//            searchPanel.setOpaque(true);
//            searchPanel.setBackground(Color.LIGHT_GRAY);
//            searchPanel.setForeground(Color.BLACK);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            // Fetch subjects and grades
            List<Object[]> subjectsAndGrades = fetchSubjectsAndGradesFromDatabase();

            // Create Table Model
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2; // Only "Actions" column is editable
                }
            };
            tableModel.addColumn("Subjects");
            tableModel.addColumn("Final Grade");
            tableModel.addColumn("Actions");

            // Populate the table model
            for (Object[] row : subjectsAndGrades) {
                tableModel.addRow(new Object[]{row[0], row[1], "Leave"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new LeaveButtonEditor(new JCheckBox(), subjectsAndGrades));

            // Search functionality
            searchButton.addActionListener(e -> filterTable(searchField.getText(), tableModel, subjectsAndGrades));
            searchField.addActionListener(e -> filterTable(searchField.getText(), tableModel, subjectsAndGrades));

            // Add components to the main panel
            mainPanel.add(searchPanel, BorderLayout.NORTH);
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching subjects and grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAvailableCourses() {
        try {
            // Clear the main panel
            mainPanel.removeAll();

            // Panel for search functionality
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField searchField = new JTextField(20); // Width is still 20 columns
            searchField.setFont(new Font("Arial", Font.PLAIN, 18)); // Increase font size
            searchField.setPreferredSize(new Dimension(300, 30));

            JButton searchButton = new JButton("Search");
            Font buttonFont = new Font("Arial", Font.PLAIN, 18); // Larger text
            Color buttonBackground = Color.LIGHT_GRAY;
            searchButton.setFont(buttonFont);
            searchButton.setBackground(buttonBackground);

            JLabel searchLabel = new JLabel("Search by Subject:");
            searchLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Set font and size
            searchLabel.setForeground(Color.BLACK); // Set text color (optional)
            searchPanel.add(searchLabel);

            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            // Fetch available courses
            List<Object[]> availableCourses = fetchAvailableCoursesFromDatabase();

            // Create Table Model
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1; // Only the "Actions" column is editable
                }
            };
            tableModel.addColumn("Course Name");
            tableModel.addColumn("Actions");

            // Populate the table model
            for (Object[] course : availableCourses) {
                tableModel.addRow(new Object[]{course[0], "Enroll"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new EnrollButtonEditor(new JCheckBox(), availableCourses));

            // Search functionality
            searchButton.addActionListener(e -> filterAvailableCourses(searchField.getText(), tableModel, availableCourses));
            searchField.addActionListener(e -> filterAvailableCourses(searchField.getText(), tableModel, availableCourses));

            // Add components to the main panel
            mainPanel.add(searchPanel, BorderLayout.NORTH);
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the main panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching available courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable(String query, DefaultTableModel tableModel, List<Object[]> data) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Object[] row : data) {
            String subjectName = (String) row[0];
            if (subjectName.toLowerCase().contains(query.toLowerCase())) {
                tableModel.addRow(new Object[]{row[0], row[1], "Leave"});
            }
        }
    }



    private List<Object[]> fetchSubjectsAndGradesFromDatabase() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String query = student.getSubjectsAndGrades();

        // Execute the query and fetch results
        try (Statement stmt = DBController.db.getCon().createStatement();
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
        try (Statement stmt = DBController.db.getCon().createStatement();
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

            // Create Table Model with Five Columns: "Subject", "View Members", "Chat", "Activities", and "Leave"
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column >= 1; // Allow editing only for the "Actions" columns
                }
            };
            tableModel.addColumn("Subject");       // Column for group names
            tableModel.addColumn("View Members"); // Column for "View Members" button
            tableModel.addColumn("Chat");         // Column for "Chat" button
            tableModel.addColumn("Activities");
            tableModel.addColumn("View Suggestions for Members");
            tableModel.addColumn("Leave");        // Column for "Leave" button

            // Add rows to the table model
            for (Object[] group : groups) {
                String subjectName = (String) group[0];
                int groupId = (int) group[1];
                tableModel.addRow(new Object[]{subjectName, "View Members", "Chat", "Activities",  "View Suggestions for Members", "Leave"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "View Members," "Chat," "Activities," and "Leave" columns
            table.getColumn("View Members").setCellRenderer(new ButtonRenderer());
            table.getColumn("View Members").setCellEditor(new GroupActionButtonEditor(new JCheckBox(), groups, "View Members"));

            table.getColumn("Chat").setCellRenderer(new ButtonRenderer());
            table.getColumn("Chat").setCellEditor(new GroupActionButtonEditor(new JCheckBox(), groups, "Chat"));

            table.getColumn("Activities").setCellRenderer(new ButtonRenderer());
            table.getColumn("Activities").setCellEditor(new GroupActionButtonEditor(new JCheckBox(), groups, "Activities"));

            table.getColumn("View Suggestions for Members").setCellRenderer(new ButtonRenderer());
            table.getColumn("View Suggestions for Members").setCellEditor(new SuggestionsButtonEditor(new JCheckBox(), groups));

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
        try (Statement stmt = DBController.db.getCon().createStatement();
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
        try (Statement stmt = DBController.db.getCon().createStatement();
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
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(deleteFromMateriiStudenti)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }

        // Delete from note_activitati
        String deleteFromNoteActivitati = "DELETE na " +
                "FROM note_activitati na " +
                "JOIN activitati_profesori ap ON na.id_activitate = ap.id_activitate " +
                "WHERE na.CNP_student = ? AND ap.id_materie = ?";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(deleteFromNoteActivitati)) {
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
            setBackground(Color.LIGHT_GRAY); // Set the background color to match menu buttons
            setForeground(Color.BLACK); // Set the text color
            setFont(new Font("Arial", Font.BOLD, 14)); // Set the
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
        Font buttonFont = new Font("Arial", Font.BOLD, 14); // Larger text
        Color buttonBackground = Color.LIGHT_GRAY;
        sendButton.setFont(buttonFont);
        sendButton.setBackground(buttonBackground);

        // Add components to the chat frame
        chatFrame.setLayout(new BorderLayout());
        chatFrame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatFrame.add(inputPanel, BorderLayout.SOUTH);

        // Load existing messages
        try {
            List<String[]> messages = DBController.getMessagesForGroup(groupId);
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
                    DBController.saveMessage(groupId, student.getFirstName() + " " + student.getSecondName(), message);
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
        Font buttonFont = new Font("Arial", Font.PLAIN, 18); // Larger text
        Color buttonBackground = Color.LIGHT_GRAY;
//        button.setFont(buttonFont);
//        button.setBackground(buttonBackground);
        private String label;
        private boolean clicked;
        private List<Object[]> subjectsAndGrades;

        public LeaveButtonEditor(JCheckBox checkBox, List<Object[]> subjectsAndGrades) {
            super(checkBox);
            this.subjectsAndGrades = subjectsAndGrades;

            // Initialize the button
            button = new JButton();
            button.setOpaque(true);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(Color.GRAY); // Set the background color to match menu buttons
            button.setForeground(Color.BLACK); // Set the text color
            button.setFont(new Font("Arial", Font.BOLD, 14)); // Set the same font
            button.setPreferredSize(new Dimension(100, 30));


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
        private String actionType; // "View Members," "Chat," "Activities," or "Leave"

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
                    case "Activities" -> displayActivitiesTable(groupId); // Show activities of the group
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

    private void displayActivitiesTable(int groupId) {
        try {
            // Fetch activities for the selected group
            List<Object[]> activities = fetchActivitiesForGroupWithEnrollment(groupId);

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6 || column == 7; // Only "Download" and "Enroll" columns are editable
                }
            };

            // Add column headers
            tableModel.addColumn("Activity Name");
            tableModel.addColumn("Date");
            tableModel.addColumn("Hours");
            tableModel.addColumn("Minimum Participants");
            tableModel.addColumn("Current Participants");
            tableModel.addColumn("Expiration Time");
            tableModel.addColumn("Download");
            tableModel.addColumn("Enroll");
            tableModel.addColumn("Status"); // New column for the cancellation message

            // Populate the table with activity data
            for (Object[] activity : activities) {
                tableModel.addRow(new Object[]{
                        activity[0],
                        activity[1],
                        activity[2],
                        activity[3],
                        activity[4],
                        activity[5],
                        "Download",
                        (boolean) activity[7] ? "✓" : "Enroll",
                        activity[9] // Status (e.g., "This activity was canceled")
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Download" column
            table.getColumn("Download").setCellRenderer(new ButtonRenderer());
            table.getColumn("Download").setCellEditor(new ActivityDownloadButtonEditor(new JCheckBox(), activities));

            // Set custom renderer and editor for the "Enroll" column
            table.getColumn("Enroll").setCellRenderer(new ButtonRenderer());
            table.getColumn("Enroll").setCellEditor(new EnrollActivityButtonEditor(new JCheckBox(), activities));

            // Add "Add Activity" button
            JButton addActivityButton = new JButton("Add Activity");
            Font buttonFont = new Font("Arial", Font.BOLD, 14); // Larger text
            Color buttonBackground = Color.LIGHT_GRAY;
            addActivityButton.setFont(buttonFont);
            addActivityButton.setBackground(buttonBackground);
            addActivityButton.addActionListener(e -> openAddActivityDialog(groupId));

            // Create a top panel for the button
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(addActivityButton);

            // Clear and add components to the main panel
            mainPanel.removeAll();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(topPanel, BorderLayout.NORTH); // Add the button panel above the table
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching activities: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }







    class ActivityDownloadButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> activities;

        public ActivityDownloadButtonEditor(JCheckBox checkBox, List<Object[]> activities) {
            super(checkBox);
            this.activities = activities;
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
            if (clicked && "Download".equals(label)) {
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                try {
                    downloadActivityDetails(activities.get(rowIndex));
                    JOptionPane.showMessageDialog(button, "Activity details downloaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(button, "Error downloading activity details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private void downloadActivityDetails(Object[] activityDetails) throws Exception {
        // Prepare activity details for the file
        String content = "Activity Details:\n";
        content += "Activity Name: " + activityDetails[0] + "\n"; // Activity Name
        content += "Date: " + activityDetails[1] + "\n"; // Date
        content += "Hours: " + activityDetails[2] + "\n"; // Hours
        content += "Minimum Participants: " + activityDetails[3] + "\n"; // Minimum Participants
        content += "Current Participants: " + activityDetails[4] + "\n"; // Current Participants
        content += "Expiration Time: " + activityDetails[5] + "\n"; // Expiration Time

        // Show file save dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Activity Details");
        fileChooser.setSelectedFile(new File(activityDetails[0] + "_details.txt")); // Default file name
        int userSelection = fileChooser.showSaveDialog(jFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Write details to the file
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(content);
            }
        }
    }



    private void openAddActivityDialog(int groupId) {
        // Create input fields
        JTextField dateField = new JTextField();
        JTextField hoursField = new JTextField();
        JTextField minParticipantsField = new JTextField();
        JTextField expirationTimeField = new JTextField();
        JTextField nameField = new JTextField();

        Object[] message = {
                "Date (yyyy-MM-dd HH:mm:ss):", dateField,
                "Hours:", hoursField,
                "Minimum Participants:", minParticipantsField,
                "Expiration Time (yyyy-MM-dd HH:mm:ss):", expirationTimeField,
                "Activity Name:", nameField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add Activity", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                // Collect inputs
                String date = dateField.getText();
                int hours = Integer.parseInt(hoursField.getText());
                int minParticipants = Integer.parseInt(minParticipantsField.getText());
                String expirationTime = expirationTimeField.getText();
                String name = nameField.getText();

                // Insert into database
                addActivity(groupId, date, hours, minParticipants, expirationTime, name);

                JOptionPane.showMessageDialog(jFrame, "Activity added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh the activities table
                displayActivitiesTable(groupId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(jFrame, "Error adding activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addActivity(int groupId, String date, int hours, int minParticipants, String expirationTime, String name) throws SQLException {
        String query = "INSERT INTO activitati_studenti (data, numar_ore, numar_minim_participanti, id_grup, timp_expirare, nume) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, date);
            stmt.setInt(2, hours);
            stmt.setInt(3, minParticipants);
            stmt.setInt(4, groupId);
            stmt.setString(5, expirationTime);
            stmt.setString(6, name);
            stmt.executeUpdate();
        }
    }




    private List<Object[]> fetchActivitiesForGroupWithEnrollment(int groupId) throws SQLException {
        List<Object[]> activities = new ArrayList<>();
        String query = """
        SELECT 
            a.nume AS activity_name, 
            a.data AS activity_date, 
            a.numar_ore AS duration, 
            a.numar_minim_participanti AS min_participants, 
            a.numar_participanti AS current_participants, 
            a.timp_expirare AS expiration_time,
            a.id_activitate AS activity_id,
            EXISTS(
                SELECT 1
                FROM studenti_activitati_studenti sas
                WHERE sas.CNP_student = ? AND sas.id_activitate = a.id_activitate
            ) AS enrolled,
            a.id_grup AS group_id
        FROM 
            activitati_studenti a
        WHERE 
            a.id_grup = ?;
    """;

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("activity_name");
                String date = rs.getString("activity_date");
                int duration = rs.getInt("duration");
                int minParticipants = rs.getInt("min_participants");
                int currentParticipants = rs.getInt("current_participants");
                String expirationTime = rs.getString("expiration_time");
                int activityId = rs.getInt("activity_id");
                boolean enrolled = rs.getBoolean("enrolled");
                int groupIdValue = rs.getInt("group_id");

                // Determine if the activity is canceled
                boolean isCanceled = currentParticipants < minParticipants
                        && LocalDateTime.parse(expirationTime.replace(" ", "T")).isBefore(LocalDateTime.now());

                // Add all details, including a cancellation flag
                activities.add(new Object[]{
                        name,
                        date,
                        duration,
                        minParticipants,
                        currentParticipants,
                        expirationTime,
                        activityId,
                        enrolled,
                        groupIdValue,
                        isCanceled ? "This activity was canceled" : ""
                });
            }
        }

        return activities;
    }







    private void leaveGroup(int groupId) {
        try {
            // Step 1: Delete all dependent rows in `studenti_activitati_studenti`
            String deleteActivitiesQuery = """
            DELETE sas
            FROM studenti_activitati_studenti sas
            JOIN activitati_studenti act ON sas.id_activitate = act.id_activitate
            WHERE sas.CNP_student = ? AND act.id_grup = ?;
        """;
            try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(deleteActivitiesQuery)) {
                stmt.setString(1, student.getCNP());
                stmt.setInt(2, groupId);
                int rowsDeleted = stmt.executeUpdate();
                System.out.println("Deleted " + rowsDeleted + " dependent activities for group ID: " + groupId);
            }

            // Step 2: Delete the student from the group
            String deleteGroupQuery = """
            DELETE FROM studenti_grupuri_studenti 
            WHERE CNP_student = ? AND id_grup = ?;
        """;
            try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(deleteGroupQuery)) {
                stmt.setString(1, student.getCNP());
                stmt.setInt(2, groupId);
                int rowsDeleted = stmt.executeUpdate();
                System.out.println("Deleted group membership for group ID: " + groupId);
            }

            // Step 3: Notify user and refresh the groups table
            JOptionPane.showMessageDialog(jFrame, "Successfully left the group!", "Success", JOptionPane.INFORMATION_MESSAGE);
            displayGroupsTable(); // Refresh the groups table
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
                "JOIN materii_studenti ms ON ms.id_materie = m.id " +
                "WHERE ms.CNP_student = '" + student.getCNP() + "' " +
                "AND gs.id_grup NOT IN (" +
                "    SELECT id_grup " +
                "    FROM studenti_grupuri_studenti " +
                "    WHERE CNP_student = '" + student.getCNP() + "'" +
                ")";


        try (Statement stmt = DBController.db.getCon().createStatement();
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
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
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

        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String courseName = rs.getString("course_name");
                int courseId = rs.getInt("course_id");
                courses.add(new Object[]{courseName, courseId});
            }
        }

        return courses;
    }




    private void filterAvailableCourses(String query, DefaultTableModel tableModel, List<Object[]> data) {
        tableModel.setRowCount(0); // Clear existing rows
        for (Object[] row : data) {
            String courseName = (String) row[0];
            if (courseName.toLowerCase().contains(query.toLowerCase())) {
                tableModel.addRow(new Object[]{row[0], "Enroll"});
            }
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
        String query = "SELECT pm.CNP_profesor FROM profesori_materii pm LEFT JOIN materii_studenti ms " +
                "ON pm.CNP_profesor = ms.CNP_profesor AND pm.id_materie = ms.id_materie " +
                "WHERE pm.id_materie = " + courseId +
                " GROUP BY pm.CNP_profesor ORDER BY COUNT(ms.CNP_student) ASC LIMIT 1;";

        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                String professorCNP = rs.getString("CNP_profesor");

                // Insert the student enrollment with a default value for nota_finala
                String insertQuery = """
                INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala)
                VALUES (?, ?, ?, ?);
            """;

                try (PreparedStatement insertStmt = DBController.db.getCon().prepareStatement(insertQuery)) {
                    insertStmt.setString(1, student.getCNP());
                    insertStmt.setString(2, professorCNP);
                    insertStmt.setInt(3, courseId);
                    insertStmt.setInt(4, 0); // Default value for nota_finala
                    insertStmt.executeUpdate();

                    JOptionPane.showMessageDialog(jFrame, "Successfully enrolled in the course!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(jFrame, "No available professor found for this course.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(jFrame, "Error enrolling in the course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw e;
        }

        // Refresh available courses table
        displayAvailableCourses();
    }




    public int fetchYearOfStudies() {
        int yearOfStudies = -1; // Default value in case of no data found or errors
        String query = student.getYearOfStudies(); // Get the SQL query

        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                yearOfStudies = rs.getInt("an_de_studiu");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching year of studies: " + e.getMessage());
        }

        return yearOfStudies;
    }

    public int fetchNrHoursSustained() {
        int hoursSustained = -1; // Default value in case of no data found or errors
        String query = student.getNrHoursSustained(); // Get the SQL query

        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                hoursSustained = rs.getInt("numar_ore_sustinute");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching number of hours sustained: " + e.getMessage());
        }

        return hoursSustained;
    }


    private void displayProfileDetails() {
        // Clear the main panel
        mainPanel.removeAll();

        // Fetch student details
        String[] columnNames = {"Field", "Value"};
        Object[][] data = {
                {"CNP", student.getCNP()},
                {"First Name", student.getFirstName()},
                {"Last Name", student.getSecondName()},
                {"Address", student.getAddress()},
                {"Phone Number", student.getPhoneNumber()},
                {"Email", student.getEmail()},
                {"IBAN", student.getIban()},
                {"Contract Number", student.getContractNumber()},
                {"Year of Studies", fetchYearOfStudies()},
                {"Hours Sustained", fetchNrHoursSustained()},
        };

        // Create a JTable to display the details
        JTable table = new JTable(data, columnNames);
        table.setRowHeight(30);

        // Add the table to the main panel
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Create a panel for the GIF and static image
        JPanel gifPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center-align with padding
        gifPanel.setOpaque(false); // Make it transparent to avoid background conflicts

        // Load the static and animated images
        ImageIcon staticGif = new ImageIcon("grinch.png"); // Update path
        ImageIcon animatedGif = new ImageIcon("grinch.gif"); // Update path

        // Create a JLabel for the static image
        JLabel gifLabel = new JLabel(staticGif);

        // Add MouseListener to switch between static and animated GIFs
        gifLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                gifLabel.setIcon(animatedGif); // Switch to animated GIF on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                gifLabel.setIcon(staticGif); // Revert to static image on mouse exit
            }
        });

        // Add the label to the panel
        gifPanel.add(gifLabel);

        // Add the GIF panel below the table
        mainPanel.add(gifPanel, BorderLayout.SOUTH);

        // Refresh the main panel
        mainPanel.revalidate();
        mainPanel.repaint();

    }

    private void handleLogout() {
        // Create a custom panel for the message
        JPanel panel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel("Are you sure you want to log out?");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Bigger font for the message
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, BorderLayout.CENTER);

        // Custom buttons
        JButton yesButton = new JButton("YES");
        yesButton.setFont(new Font("Arial", Font.BOLD, 14));
        yesButton.setBackground(Color.LIGHT_GRAY);
        yesButton.setFocusPainted(false);

        JButton noButton = new JButton("NO");
        noButton.setFont(new Font("Arial", Font.BOLD, 14));
        noButton.setBackground(Color.LIGHT_GRAY);
        noButton.setFocusPainted(false);

        // Create a custom dialog
        JDialog dialog = new JDialog(jFrame, "Log Out", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add button actions
        yesButton.addActionListener(e -> {
            dialog.dispose();
            jFrame.dispose();
            new LogInUI();
        });

        noButton.addActionListener(e -> dialog.dispose());

        // Display the dialog
        dialog.setLocationRelativeTo(jFrame);
        dialog.setVisible(true);
    }



    private void displayMeetingsTable() {
        try {
            // Query to fetch meetings data
            String query = """
        SELECT 
            ap.tip_activitate AS activity_type,
            ap.nr_max_participanti AS max_participants,
            p.descriere_programare AS description,
            CONCAT(u.nume, ' ', u.prenume) AS professor_name,
            m.nume AS subject_name,
            p.data_inceput AS start_date,
            p.data_final AS end_date,
            p.nr_participanti AS current_participants,
            p.id_programare AS schedule_id
        FROM 
            programari_studenti ps
        JOIN 
            programari p ON ps.id_programare = p.id_programare
        JOIN 
            activitati_profesori ap ON p.id_activitate = ap.id_activitate
        JOIN 
            materii m ON ap.id_materie = m.id
        JOIN 
            detalii_profesori dp ON ap.CNP_profesor = dp.CNP
        JOIN 
            utilizatori u ON dp.CNP = u.CNP
        WHERE 
            ps.CNP_student = '""" + student.getCNP() + "' " + """
        ORDER BY p.data_inceput;
        """;

            // Execute query and fetch results
            List<Object[]> meetings = fetchMeetingsFromDatabase(query);

            // Create Table Model
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 7; // Only the "Download" column is editable
                }
            };
            tableModel.addColumn("Activity Type");
            tableModel.addColumn("Subject");
            tableModel.addColumn("Description");
            tableModel.addColumn("Professor Name");
            tableModel.addColumn("Start Date");
            tableModel.addColumn("End Date");
            tableModel.addColumn("Participants");
            tableModel.addColumn("Actions"); // Download Button Column

            // Populate the table
            for (Object[] meeting : meetings) {
                String participants = meeting[7] + "/" + meeting[1]; // current_participants/max_participants
                tableModel.addRow(new Object[]{
                        meeting[0], // Activity Type
                        meeting[4], // Subject
                        meeting[2], // Description
                        meeting[3], // Professor Name
                        meeting[5], // Start Date
                        meeting[6], // End Date
                        participants, // Participants
                        "Download" // Download Button Placeholder
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new MeetingDownloadButtonEditor(new JCheckBox(), meetings));

            // Add table to the main panel
            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching meetings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private List<Object[]> fetchMeetingsFromDatabase(String query) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String activityType = rs.getString("activity_type");
                int maxParticipants = rs.getInt("max_participants");
                String description = rs.getString("description");
                String professorName = rs.getString("professor_name");
                String subjectName = rs.getString("subject_name");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                int currentParticipants = rs.getInt("current_participants");
                int scheduleId = rs.getInt("schedule_id"); // Add schedule_id here

                data.add(new Object[]{
                        activityType,
                        maxParticipants,
                        description,
                        professorName,
                        subjectName,
                        startDate,
                        endDate,
                        currentParticipants,
                        scheduleId // Include schedule_id
                });
            }
        }
        return data;
    }



    private void displayAvailableMeetingsTable() {
        try {
            // Query to fetch available meetings
            String query = """
                SELECT 
                    ap.tip_activitate AS activity_type,
                    ap.nr_max_participanti AS max_participants,
                    p.descriere_programare AS description,
                    CONCAT(u.nume, ' ', u.prenume) AS professor_name,
                    m.nume AS subject_name, -- Fetch subject name
                    p.id_programare AS schedule_id,
                    p.data_inceput AS start_date,
                    p.data_final AS end_date,
                    p.nr_participanti AS current_participants
                FROM 
                    programari p
                JOIN 
                    activitati_profesori ap ON p.id_activitate = ap.id_activitate
                JOIN 
                    materii m ON ap.id_materie = m.id -- Join with materii to get subject name
                JOIN 
                    materii_studenti ms ON ap.id_materie = ms.id_materie
                JOIN 
                    detalii_profesori dp ON ap.CNP_profesor = dp.CNP
                JOIN 
                    utilizatori u ON dp.CNP = u.CNP
                WHERE 
                    ms.CNP_student = '""" + student.getCNP() + "' " + """
                    AND p.id_programare NOT IN (
                        SELECT ps.id_programare
                        FROM programari_studenti ps
                        WHERE ps.CNP_student = '""" + student.getCNP() + "' " + """
                    )
                ORDER BY 
                    DATE(p.data_inceput) = CURDATE() DESC, -- Current day meetings first
                    p.data_inceput ASC; -- Then sort by start date
                """;
            List<Object[]> availableMeetings = fetchAvailableMeetingsFromDatabase(query);

            // Create Table Model
            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 7; // Make only the "Enroll" column editable
                }
            };
            tableModel.addColumn("Subject Name");    // New column for subject name
            tableModel.addColumn("Activity Type");
            tableModel.addColumn("Description");
            tableModel.addColumn("Professor Name");
            tableModel.addColumn("Start Date");
            tableModel.addColumn("End Date");
            tableModel.addColumn("Participants");
            tableModel.addColumn("Actions"); // Enroll Button Column

            // Populate the table
            for (Object[] meeting : availableMeetings) {
                String participants = meeting[8] + "/" + meeting[1]; // current/max participants
                tableModel.addRow(new Object[]{
                        meeting[4], // Subject Name
                        meeting[0], // Activity Type
                        meeting[2], // Description
                        meeting[3], // Professor Name
                        meeting[6], // Start Date
                        meeting[7], // End Date
                        participants, // Participants
                        "Enroll" // Enroll Button Placeholder
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Set custom renderer and editor for the "Actions" column
            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new AvailableMeetingEnrollButtonEditor(new JCheckBox(), availableMeetings));

            // Add table to the main panel
            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            // Refresh the panel
            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching available meetings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private List<Object[]> fetchAvailableMeetingsFromDatabase(String query) throws SQLException {
        List<Object[]> data = new ArrayList<>();
        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String activityType = rs.getString("activity_type");
                int maxParticipants = rs.getInt("max_participants");
                String description = rs.getString("description");
                String professorName = rs.getString("professor_name");
                String subjectName = rs.getString("subject_name"); // Fetch subject name
                int scheduleId = rs.getInt("schedule_id");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                int currentParticipants = rs.getInt("current_participants");

                data.add(new Object[]{
                        activityType,
                        maxParticipants,
                        description,
                        professorName,
                        subjectName,      // Subject name
                        scheduleId,       // Schedule ID
                        startDate,
                        endDate,
                        currentParticipants
                });
            }
        }
        return data;
    }





    class AvailableMeetingEnrollButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> availableMeetings;

        public AvailableMeetingEnrollButtonEditor(JCheckBox checkBox, List<Object[]> availableMeetings) {
            super(checkBox);
            this.availableMeetings = availableMeetings;
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
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                try {
                    int scheduleId = Integer.parseInt(availableMeetings.get(rowIndex)[5].toString()); // Corrected index for schedule_id

                    // Check both conditions: no time conflict and max participants not reached
                    if (!canEnroll(scheduleId)) {
                        return label; // Exit without enrolling if validation fails
                    }

                    // Perform the enrollment
                    enrollInMeeting(scheduleId, student.getCNP());
                    JOptionPane.showMessageDialog(button, "Successfully enrolled in the meeting!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Update the participants count
                    updateParticipants(scheduleId);

                    // Refresh the table
                    displayAvailableMeetingsTable();
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(button, "Error parsing schedule ID: " + nfe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(button, "Error enrolling in the meeting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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


    private void enrollInMeeting(int scheduleId, String cnpStudent) throws SQLException {
        System.out.println("Inserting enrollment for student CNP: " + cnpStudent + " and schedule ID: " + scheduleId); // Debugging log
        String query = "INSERT INTO programari_studenti (id_programare, CNP_student) VALUES (?, ?)";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            stmt.setString(2, cnpStudent);
            stmt.executeUpdate();
            System.out.println("Enrollment inserted."); // Debugging log
        }
    }

    private void updateParticipants(int scheduleId) throws SQLException {
        System.out.println("Updating participants for schedule ID: " + scheduleId); // Debugging log
        String query = "UPDATE programari SET nr_participanti = nr_participanti + 1 WHERE id_programare = ?";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            stmt.executeUpdate();
            System.out.println("Participants updated."); // Debugging log
        }
    }

    private boolean canEnroll(int scheduleId) throws SQLException {
        // Query to get the start and end time, and current/max participants of the selected meeting
        String query = """
        SELECT p.data_inceput AS start_date, 
               p.data_final AS end_date, 
               p.nr_participanti AS current_participants, 
               ap.nr_max_participanti AS max_participants
        FROM programari p
        JOIN activitati_profesori ap ON p.id_activitate = ap.id_activitate
        WHERE p.id_programare = ?;
    """;

        LocalDateTime selectedStartDate;
        LocalDateTime selectedEndDate;
        int currentParticipants;
        int maxParticipants;

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                selectedStartDate = rs.getTimestamp("start_date").toLocalDateTime();
                selectedEndDate = rs.getTimestamp("end_date").toLocalDateTime();
                currentParticipants = rs.getInt("current_participants");
                maxParticipants = rs.getInt("max_participants");
            } else {
                throw new SQLException("Meeting not found for ID: " + scheduleId);
            }
        }

        // Check if the meeting has reached the maximum number of participants
        if (currentParticipants >= maxParticipants) {
            JOptionPane.showMessageDialog(null, "The maximum number of participants for this meeting has been reached.", "Max Participants Reached", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Query to get all existing meetings for the student
        String existingMeetingsQuery = """
        SELECT p.data_inceput AS start_date, p.data_final AS end_date
        FROM programari_studenti ps
        JOIN programari p ON ps.id_programare = p.id_programare
        WHERE ps.CNP_student = ?;
    """;

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(existingMeetingsQuery)) {
            stmt.setString(1, student.getCNP());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LocalDateTime existingStartDate = rs.getTimestamp("start_date").toLocalDateTime();
                LocalDateTime existingEndDate = rs.getTimestamp("end_date").toLocalDateTime();


                if (!(selectedEndDate.isBefore(existingStartDate) || selectedStartDate.isAfter(existingEndDate))) {
                    JOptionPane.showMessageDialog(null, "You cannot enroll in this meeting because it conflicts with another meeting.", "Time Conflict", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }

        return true;
    }

    class MeetingDownloadButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> meetings;

        public MeetingDownloadButtonEditor(JCheckBox checkBox, List<Object[]> meetings) {
            super(checkBox);
            this.meetings = meetings;
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
            if (clicked && "Download".equals(label)) {
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                Object[] meeting = meetings.get(rowIndex);

                try {
                    saveMeetingDetailsToFile(meeting);
                    JOptionPane.showMessageDialog(button, "Meeting details downloaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(button, "Error downloading meeting details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void saveMeetingDetailsToFile(Object[] meeting) throws Exception {
            // Use a file chooser to allow the user to select a save location
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Meeting Details");
            fileChooser.setSelectedFile(new java.io.File("MeetingDetails_" + meeting[8] + ".txt")); // Default file name

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                // Write meeting details to the selected file
                try (PrintWriter writer = new PrintWriter(fileToSave)) {
                    writer.println("Meeting Details:");
                    writer.println("Activity Type: " + meeting[0]);
                    writer.println("Subject: " + meeting[4]);
                    writer.println("Description: " + meeting[2]);
                    writer.println("Professor Name: " + meeting[3]);
                    writer.println("Start Date: " + meeting[5]);
                    writer.println("End Date: " + meeting[6]);
                    writer.println("Participants: " + meeting[7] + "/" + meeting[1]);
                }

                JOptionPane.showMessageDialog(null, "Meeting details saved to " + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Save operation canceled.", "Canceled", JOptionPane.WARNING_MESSAGE);
            }
        }

    }


    class EnrollActivityButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> activities;

        public EnrollActivityButtonEditor(JCheckBox checkBox, List<Object[]> activities) {
            super(checkBox);
            this.activities = activities;
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
                try {
                    int rowIndex = ((JTable) button.getParent()).getSelectedRow();

                    // Retrieve activity ID and group ID
                    int activityId = (int) activities.get(rowIndex)[6]; // Index for activity ID
                    int groupId = (int) activities.get(rowIndex)[8];    // Index for group ID
                    String studentCNP = student.getCNP();

                    // Enroll in the activity
                    enrollInActivity(activityId, studentCNP, groupId);

                    // Notify the user
                    JOptionPane.showMessageDialog(button, "Successfully enrolled in the activity!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Refresh the activities table
                    System.out.println("Refreshing activities for Group ID: " + groupId);
                    displayActivitiesTable(groupId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(button, "Error enrolling in the activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void enrollInActivity(int activityId, String studentCNP, int groupId) {
            try {
                // Step 1: Insert the enrollment into `studenti_activitati_studenti`
                String enrollQuery = """
                INSERT INTO studenti_activitati_studenti (CNP_student, id_activitate)
                VALUES (?, ?);
            """;
                try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(enrollQuery)) {
                    stmt.setString(1, studentCNP);
                    stmt.setInt(2, activityId);
                    stmt.executeUpdate();
                    System.out.println("Enrolled student: " + studentCNP + " in activity ID: " + activityId);
                }

                // Step 2: Update the number of current participants
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
                JOptionPane.showMessageDialog(jFrame, "Error enrolling in the activity: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displaySuggestionsForGroup(int groupId) {
        try {
            // Fetch suggestions for the group
            List<String[]> suggestions = fetchSuggestionsForGroupFromDatabase(groupId);

            // Create Table Model with Student Information
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");
            tableModel.addColumn("Email");

            // Add rows to the table model
            for (String[] suggestion : suggestions) {
                tableModel.addRow(suggestion);
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            // Display the suggestions in a dialog or new panel
            JFrame suggestionsFrame = new JFrame("Suggestions for Members - Group " + groupId);
            suggestionsFrame.setSize(600, 400);
            suggestionsFrame.add(new JScrollPane(table), BorderLayout.CENTER);
            suggestionsFrame.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching suggestions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String[]> fetchSuggestionsForGroupFromDatabase(int groupId) throws SQLException {
        List<String[]> suggestions = new ArrayList<>();
        String query =
         " SELECT u.nume AS first_name, u.prenume AS last_name, u.email  " +
                  " FROM utilizatori u  " +
                  " JOIN materii_studenti ms ON u.CNP = ms.CNP_student " +
                  " JOIN grupuri_studenti gs ON ms.id_materie = gs.id_materie " +
                  " WHERE gs.id_grup =  ?" +
                  " AND u.CNP NOT IN ( " +
                      " SELECT CNP_student " +
                      " FROM studenti_grupuri_studenti " +
                      " WHERE id_grup = ?"  + "  ); " ;

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, groupId);
            stmt.setInt(2, groupId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                suggestions.add(new String[]{firstName, lastName, email});
            }
        }

        return suggestions;
    }


    class SuggestionsButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> groups;

        public SuggestionsButtonEditor(JCheckBox checkBox, List<Object[]> groups) {
            super(checkBox);
            this.groups = groups;
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
                // Get the selected group's ID
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int groupId = (int) groups.get(rowIndex)[1];

                // Display suggestions for members
                displaySuggestionsForGroup(groupId);
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }




}

