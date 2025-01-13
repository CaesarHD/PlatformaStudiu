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
    private JPanel mainPanel;
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

        jFrame = new JFrame("StudyPlatform");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Color buttonBackground = Color.LIGHT_GRAY;

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
            mainPanel.removeAll();

            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField searchField = new JTextField(20);
            searchField.setFont(new Font("Arial", Font.PLAIN, 18));
            searchField.setPreferredSize(new Dimension(300, 30));

            JButton searchButton = new JButton("Search");
            Font buttonFont = new Font("Arial", Font.PLAIN, 18);
            Color buttonBackground = Color.LIGHT_GRAY;
            searchButton.setFont(buttonFont);
            searchButton.setBackground(buttonBackground);

            JLabel searchLabel = new JLabel("Search by Subject:");
            searchLabel.setFont(new Font("Arial", Font.BOLD, 18));
            searchLabel.setForeground(Color.BLACK);
            searchPanel.add(searchLabel);

//            searchPanel.add(new JLabel("Search by Subject: "));
//            searchPanel.setFont(new Font("Arial", Font.PLAIN, 18));
//            searchPanel.setOpaque(true);
//            searchPanel.setBackground(Color.LIGHT_GRAY);
//            searchPanel.setForeground(Color.BLACK);
            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            List<Object[]> subjectsAndGrades = fetchSubjectsAndGradesFromDatabase();

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 2;
                }
            };
            tableModel.addColumn("Subjects");
            tableModel.addColumn("Final Grade");
            tableModel.addColumn("Actions");

            for (Object[] row : subjectsAndGrades) {
                tableModel.addRow(new Object[]{row[0], row[1], "Leave"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new LeaveButtonEditor(new JCheckBox(), subjectsAndGrades));

            searchButton.addActionListener(e -> filterTable(searchField.getText(), tableModel, subjectsAndGrades));
            searchField.addActionListener(e -> filterTable(searchField.getText(), tableModel, subjectsAndGrades));

            mainPanel.add(searchPanel, BorderLayout.NORTH);
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            mainPanel.revalidate();
            mainPanel.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching subjects and grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayAvailableCourses() {
        try {
            mainPanel.removeAll();

            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JTextField searchField = new JTextField(20);
            searchField.setFont(new Font("Arial", Font.PLAIN, 18));
            searchField.setPreferredSize(new Dimension(300, 30));

            JButton searchButton = new JButton("Search");
            Font buttonFont = new Font("Arial", Font.PLAIN, 18);
            Color buttonBackground = Color.LIGHT_GRAY;
            searchButton.setFont(buttonFont);
            searchButton.setBackground(buttonBackground);

            JLabel searchLabel = new JLabel("Search by Subject:");
            searchLabel.setFont(new Font("Arial", Font.BOLD, 18));
            searchLabel.setForeground(Color.BLACK);
            searchPanel.add(searchLabel);

            searchPanel.add(searchField);
            searchPanel.add(searchButton);

            List<Object[]> availableCourses = fetchAvailableCoursesFromDatabase();

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1;
                }
            };
            tableModel.addColumn("Course Name");
            tableModel.addColumn("Actions");

            for (Object[] course : availableCourses) {
                tableModel.addRow(new Object[]{course[0], "Enroll"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new EnrollButtonEditor(new JCheckBox(), availableCourses));

            searchButton.addActionListener(e -> filterAvailableCourses(searchField.getText(), tableModel, availableCourses));
            searchField.addActionListener(e -> filterAvailableCourses(searchField.getText(), tableModel, availableCourses));

            mainPanel.add(searchPanel, BorderLayout.NORTH);
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching available courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable(String query, DefaultTableModel tableModel, List<Object[]> data) {
        tableModel.setRowCount(0);
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
            List<Object[]> gradesData = fetchGradesFromDatabase();

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Subject");
            tableModel.addColumn("Activity Type");
            tableModel.addColumn("Grade");

            for (Object[] row : gradesData) {
                tableModel.addRow(row);
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Object[]> fetchGradesFromDatabase() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String query = student.getAllGrades();

        try (Statement stmt = DBController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String subjectName = rs.getString("subject_name");
                String activityType = rs.getString("activity_type");
                Float grade = rs.getFloat("grade");
                data.add(new Object[]{subjectName, activityType, grade != 0 ? grade : "N/A"});
            }
        }

        return data;
    }

    private void displayGroupsTable() {
        try {
            List<Object[]> groups = fetchGroupsWithIdsFromDatabase();

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column >= 1;
                }
            };
            tableModel.addColumn("Subject");
            tableModel.addColumn("View Members");
            tableModel.addColumn("Chat");
            tableModel.addColumn("Activities");
            tableModel.addColumn("View Suggestions for Members");
            tableModel.addColumn("Leave");

            for (Object[] group : groups) {
                String subjectName = (String) group[0];
                int groupId = (int) group[1];
                tableModel.addRow(new Object[]{subjectName, "View Members", "Chat", "Activities",  "View Suggestions for Members", "Leave"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

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

            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.removeAll();
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching groups: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private List<Object[]> fetchGroupsWithIdsFromDatabase() throws SQLException {
        List<Object[]> groups = new ArrayList<>();
        String query = student.getStudentGroups();

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

    private void displayGroupMembers(int groupId) {
        try {
            List<String[]> members = fetchGroupMembersFromDatabase(groupId);

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");
            tableModel.addColumn("Email");

            for (String[] member : members) {
                tableModel.addRow(member);
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

            mainPanel.revalidate();
            mainPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching members: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String[]> fetchGroupMembersFromDatabase(int groupId) throws SQLException {
        List<String[]> members = new ArrayList<>();
        String query = student.getAllStudentsForThisGroup(groupId);

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
        String deleteFromMateriiStudenti = "DELETE FROM materii_studenti WHERE CNP_student = ? AND id_materie = ?";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(deleteFromMateriiStudenti)) {
            stmt.setString(1, student.getCNP());
            stmt.setInt(2, subjectId);
            stmt.executeUpdate();
        }

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

        displaySubjectsTable();
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
            setFont(new Font("Arial", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }


    private void enterGroupChat(int groupId) {
        JFrame chatFrame = new JFrame("Group Chat - Group " + groupId);
        chatFrame.setSize(500, 400);
        chatFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Send");
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Color buttonBackground = Color.LIGHT_GRAY;
        sendButton.setFont(buttonFont);
        sendButton.setBackground(buttonBackground);

        chatFrame.setLayout(new BorderLayout());
        chatFrame.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatFrame.add(inputPanel, BorderLayout.SOUTH);

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
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);
        Color buttonBackground = Color.LIGHT_GRAY;
//        button.setFont(buttonFont);
//        button.setBackground(buttonBackground);
        private String label;
        private boolean clicked;
        private List<Object[]> subjectsAndGrades;

        public LeaveButtonEditor(JCheckBox checkBox, List<Object[]> subjectsAndGrades) {
            super(checkBox);
            this.subjectsAndGrades = subjectsAndGrades;

            button = new JButton();
            button.setOpaque(true);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(Color.GRAY);
            button.setForeground(Color.BLACK);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setPreferredSize(new Dimension(100, 30));

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked && "Leave".equals(label)) {
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                String subjectName = (String) subjectsAndGrades.get(rowIndex)[0];
                int subjectId = (int) subjectsAndGrades.get(rowIndex)[2];

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
        private String actionType;

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
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int groupId = (int) groups.get(rowIndex)[1];

                switch (actionType) {
                    case "View Members" -> displayGroupMembers(groupId);
                    case "Chat" -> enterGroupChat(groupId);
                    case "Activities" -> displayActivitiesTable(groupId);
                    case "Leave" -> leaveGroup(groupId);
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
            List<Object[]> activities = fetchActivitiesForGroupWithEnrollment(groupId);

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 6 || column == 7 ||column == 8;
                }
            };

            tableModel.addColumn("Activity Name");
            tableModel.addColumn("Date");
            tableModel.addColumn("Hours");
            tableModel.addColumn("Minimum Participants");
            tableModel.addColumn("Current Participants");
            tableModel.addColumn("Expiration Time");
            tableModel.addColumn("Download");
            tableModel.addColumn("Enroll");
            tableModel.addColumn("Add professor");
            tableModel.addColumn("Status");

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
                        "Add professor",
                        activity[9]
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            table.getColumn("Download").setCellRenderer(new ButtonRenderer());
            table.getColumn("Download").setCellEditor(new ActivityDownloadButtonEditor(new JCheckBox(), activities));

            table.getColumn("Enroll").setCellRenderer(new ButtonRenderer());
            table.getColumn("Enroll").setCellEditor(new EnrollActivityButtonEditor(new JCheckBox(), activities));

            table.getColumn("Add professor").setCellRenderer(new ButtonRenderer());
            table.getColumn("Add professor").setCellEditor(new AddProfessorButtonEditor(new JCheckBox(), activities));

            JButton addActivityButton = new JButton("Add Activity");
            Font buttonFont = new Font("Arial", Font.BOLD, 14);
            Color buttonBackground = Color.LIGHT_GRAY;
            addActivityButton.setFont(buttonFont);
            addActivityButton.setBackground(buttonBackground);
            addActivityButton.addActionListener(e -> openAddActivityDialog(groupId));

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topPanel.add(addActivityButton);

            mainPanel.removeAll();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

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
        String content = "Activity Details:\n";
        content += "Activity Name: " + activityDetails[0] + "\n";
        content += "Date: " + activityDetails[1] + "\n";
        content += "Hours: " + activityDetails[2] + "\n";
        content += "Minimum Participants: " + activityDetails[3] + "\n";
        content += "Current Participants: " + activityDetails[4] + "\n";
        content += "Expiration Time: " + activityDetails[5] + "\n";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Activity Details");
        fileChooser.setSelectedFile(new File(activityDetails[0] + "_details.txt"));
        int userSelection = fileChooser.showSaveDialog(jFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(content);
            }
        }
    }

    private void openAddActivityDialog(int groupId) {
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
                String date = dateField.getText();
                int hours = Integer.parseInt(hoursField.getText());
                int minParticipants = Integer.parseInt(minParticipantsField.getText());
                String expirationTime = expirationTimeField.getText();
                String name = nameField.getText();

                addActivity(groupId, date, hours, minParticipants, expirationTime, name);

                JOptionPane.showMessageDialog(jFrame, "Activity added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

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

                boolean isCanceled = currentParticipants < minParticipants
                        && LocalDateTime.parse(expirationTime.replace(" ", "T")).isBefore(LocalDateTime.now());

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

            JOptionPane.showMessageDialog(jFrame, "Successfully left the group!", "Success", JOptionPane.INFORMATION_MESSAGE);
            displayGroupsTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error leaving group: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void displayAvailableGroups() {
        try {
            List<Object[]> availableGroups = fetchAvailableGroupsFromDatabase();

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1;
                }
            };
            tableModel.addColumn("Subject Name");
            tableModel.addColumn("Actions");

            for (Object[] group : availableGroups) {
                String subjectName = (String) group[0];
                int groupId = (int) group[1];
                tableModel.addRow(new Object[]{subjectName, "Join Group"});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new JoinGroupButtonEditor(new JCheckBox(), availableGroups));

            JScrollPane scrollPane = new JScrollPane(table);
            mainPanel.removeAll();
            mainPanel.add(scrollPane, BorderLayout.CENTER);

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
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int groupId = (int) availableGroups.get(rowIndex)[1];

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
        tableModel.setRowCount(0);
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
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int courseId = (int) availableCourses.get(rowIndex)[1];

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

                String insertQuery = """
                INSERT INTO materii_studenti (CNP_student, CNP_profesor, id_materie, nota_finala)
                VALUES (?, ?, ?, ?);
            """;

                try (PreparedStatement insertStmt = DBController.db.getCon().prepareStatement(insertQuery)) {
                    insertStmt.setString(1, student.getCNP());
                    insertStmt.setString(2, professorCNP);
                    insertStmt.setInt(3, courseId);
                    insertStmt.setInt(4, 0);
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

        displayAvailableCourses();
    }




    public int fetchYearOfStudies() {
        int yearOfStudies = -1;
        String query = student.getYearOfStudies();

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
        int hoursSustained = -1;
        String query = student.getNrHoursSustained();

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
        mainPanel.removeAll();
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

        JTable table = new JTable(data, columnNames);
        table.setRowHeight(30);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel gifPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        gifPanel.setOpaque(false);

        ImageIcon staticGif = new ImageIcon("grinch.png");
        ImageIcon animatedGif = new ImageIcon("grinch.gif");

        JLabel gifLabel = new JLabel(staticGif);

        gifLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                gifLabel.setIcon(animatedGif);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                gifLabel.setIcon(staticGif);
            }
        });

        gifPanel.add(gifLabel);

        mainPanel.add(gifPanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();

    }

    private void handleLogout() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel messageLabel = new JLabel("Are you sure you want to log out?");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, BorderLayout.CENTER);

        JButton yesButton = new JButton("YES");
        yesButton.setFont(new Font("Arial", Font.BOLD, 14));
        yesButton.setBackground(Color.LIGHT_GRAY);
        yesButton.setFocusPainted(false);

        JButton noButton = new JButton("NO");
        noButton.setFont(new Font("Arial", Font.BOLD, 14));
        noButton.setBackground(Color.LIGHT_GRAY);
        noButton.setFocusPainted(false);

        JDialog dialog = new JDialog(jFrame, "Log Out", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        yesButton.addActionListener(e -> {
            dialog.dispose();
            jFrame.dispose();
            new LogInUI();
        });

        noButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(jFrame);
        dialog.setVisible(true);
    }



    private void displayMeetingsTable() {
        try {
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

            List<Object[]> meetings = fetchMeetingsFromDatabase(query);

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 7;
                }
            };
            tableModel.addColumn("Activity Type");
            tableModel.addColumn("Subject");
            tableModel.addColumn("Description");
            tableModel.addColumn("Professor Name");
            tableModel.addColumn("Start Date");
            tableModel.addColumn("End Date");
            tableModel.addColumn("Participants");
            tableModel.addColumn("Actions");

            for (Object[] meeting : meetings) {
                String participants = meeting[7] + "/" + meeting[1];
                tableModel.addRow(new Object[]{
                        meeting[0],
                        meeting[4],
                        meeting[2],
                        meeting[3],
                        meeting[5],
                        meeting[6],
                        participants,
                        "Download"
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new MeetingDownloadButtonEditor(new JCheckBox(), meetings));

            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

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
                int scheduleId = rs.getInt("schedule_id");

                data.add(new Object[]{
                        activityType,
                        maxParticipants,
                        description,
                        professorName,
                        subjectName,
                        startDate,
                        endDate,
                        currentParticipants,
                        scheduleId
                });
            }
        }
        return data;
    }

    private void displayAvailableMeetingsTable() {
        try {
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

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 7;
                }
            };
            tableModel.addColumn("Subject Name");
            tableModel.addColumn("Activity Type");
            tableModel.addColumn("Description");
            tableModel.addColumn("Professor Name");
            tableModel.addColumn("Start Date");
            tableModel.addColumn("End Date");
            tableModel.addColumn("Participants");
            tableModel.addColumn("Actions");

            for (Object[] meeting : availableMeetings) {
                String participants = meeting[8] + "/" + meeting[1];
                tableModel.addRow(new Object[]{
                        meeting[4],
                        meeting[0],
                        meeting[2],
                        meeting[3],
                        meeting[6],
                        meeting[7],
                        participants,
                        "Enroll"
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);

            table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
            table.getColumn("Actions").setCellEditor(new AvailableMeetingEnrollButtonEditor(new JCheckBox(), availableMeetings));

            mainPanel.removeAll();
            mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

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
                String subjectName = rs.getString("subject_name");
                int scheduleId = rs.getInt("schedule_id");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                int currentParticipants = rs.getInt("current_participants");

                data.add(new Object[]{
                        activityType,
                        maxParticipants,
                        description,
                        professorName,
                        subjectName,
                        scheduleId,
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
                    int scheduleId = Integer.parseInt(availableMeetings.get(rowIndex)[5].toString());

                    if (!canEnroll(scheduleId)) {
                        return label;
                    }

                    enrollInMeeting(scheduleId, student.getCNP());
                    JOptionPane.showMessageDialog(button, "Successfully enrolled in the meeting!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    updateParticipants(scheduleId);

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
        System.out.println("Inserting enrollment for student CNP: " + cnpStudent + " and schedule ID: " + scheduleId);
        String query = "INSERT INTO programari_studenti (id_programare, CNP_student) VALUES (?, ?)";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            stmt.setString(2, cnpStudent);
            stmt.executeUpdate();
            System.out.println("Enrollment inserted.");
        }
    }

    private void updateParticipants(int scheduleId) throws SQLException {
        System.out.println("Updating participants for schedule ID: " + scheduleId);
        String query = "UPDATE programari SET nr_participanti = nr_participanti + 1 WHERE id_programare = ?";
        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, scheduleId);
            stmt.executeUpdate();
            System.out.println("Participants updated.");
        }
    }

    private boolean canEnroll(int scheduleId) throws SQLException {
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

        if (currentParticipants >= maxParticipants) {
            JOptionPane.showMessageDialog(null, "The maximum number of participants for this meeting has been reached.", "Max Participants Reached", JOptionPane.WARNING_MESSAGE);
            return false;
        }

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
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Meeting Details");
            fileChooser.setSelectedFile(new java.io.File("MeetingDetails_" + meeting[8] + ".txt"));

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();

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

                    int activityId = (int) activities.get(rowIndex)[6];
                    int groupId = (int) activities.get(rowIndex)[8];
                    String studentCNP = student.getCNP();

                    enrollInActivity(activityId, studentCNP, groupId);

                    JOptionPane.showMessageDialog(button, "Successfully enrolled in the activity!", "Success", JOptionPane.INFORMATION_MESSAGE);

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

    class AddProfessorButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<Object[]> activities;
        public AddProfessorButtonEditor(JCheckBox checkBox, List<Object[]> activities) {
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
            if (clicked) {
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int activityId = (int) activities.get(rowIndex)[6];
                int subjectId = (int) activities.get(rowIndex)[8];
                displayProfessorSelectionDialog(activityId, subjectId);
            }
            clicked = false;
            return label;
        }
        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }


    private void displayProfessorSelectionDialog(int activityId, int subjectId) {
        try {
            List<String[]> professors = fetchProfessorsForSubject(activityId, subjectId);

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");
            tableModel.addColumn("Email");
            tableModel.addColumn("Invite");

            for (String[] professor : professors) {
                tableModel.addRow(new Object[]{professor[1], professor[2], professor[3], professor[4]});
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);
            table.getColumn("Invite").setCellRenderer(new ButtonRenderer());
            table.getColumn("Invite").setCellEditor(new InviteProfessorButtonEditor(new JCheckBox(), professors, activityId, table));

            JScrollPane scrollPane = new JScrollPane(table);
            JDialog dialog = new JDialog(jFrame, "Select Professor", true);
            dialog.setSize(600, 400);
            dialog.add(scrollPane);
            dialog.setLocationRelativeTo(jFrame);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(jFrame, "Error fetching professors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<String[]> fetchProfessorsForSubject(int activityId, int subjectId) throws SQLException {
        List<String[]> professors = new ArrayList<>();
        String query = """
    SELECT
        dp.CNP AS professor_cnp,
        u.nume AS first_name,
        u.prenume AS last_name,
        u.email AS email
    FROM
        detalii_profesori dp
    JOIN
        utilizatori u ON dp.CNP = u.CNP
    JOIN
        profesori_materii pm ON dp.CNP = pm.CNP_profesor
    WHERE
        pm.id_materie = ?
        AND NOT EXISTS (
            SELECT 1
            FROM profesori_grupuri_studenti pgs
            WHERE pgs.CNP_profesor = dp.CNP
              AND pgs.id_activitate = ?
        );
""";

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setInt(1, subjectId);
            stmt.setInt(2, activityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String cnp = rs.getString("professor_cnp");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                professors.add(new String[]{cnp, firstName, lastName, email, "Invite"});
            }
        }
        return professors;
    }



    class InviteProfessorButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private List<String[]> professors;
        private int activityId;
        private JTable table;

        public InviteProfessorButtonEditor(JCheckBox checkBox, List<String[]> professors, int activityId, JTable table) {
            super(checkBox);
            this.professors = professors;
            this.activityId = activityId;
            this.table = table;
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
                int rowIndex = table.getEditingRow();
                String[] professor = professors.get(rowIndex);

                if ("Invited".equals(professor[4])) {
                    JOptionPane.showMessageDialog(button, "Professor is already invited.", "Information", JOptionPane.INFORMATION_MESSAGE);
                    clicked = false;
                    return label;
                }

                String professorCNP = professor[0];
                String professorEmail = professor[3];

                try {
                    sendInvitationToProfessor(activityId, professorCNP);
                    professor[4] = "Invited";
                    table.setValueAt("Invited", rowIndex, table.getColumn("Invite").getModelIndex());
                    JOptionPane.showMessageDialog(button, "Invitation sent to " + professorEmail, "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(button, "Error sending invitation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            clicked = false;
            return label;
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

        private void sendInvitationToProfessor(int activityId, String professorCNP) throws SQLException {
            String query = """
        INSERT INTO profesori_grupuri_studenti (id_activitate, CNP_profesor)
        VALUES (?, ?);
        """;
            try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
                stmt.setInt(1, activityId);
                stmt.setString(2, professorCNP);
                stmt.executeUpdate();
            }
        }
    }



    private void displaySuggestionsForGroup(int groupId) {
        try {
            List<String[]> suggestions = fetchSuggestionsForGroupFromDatabase(groupId);

            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("First Name");
            tableModel.addColumn("Last Name");
            tableModel.addColumn("Email");


            for (String[] suggestion : suggestions) {
                tableModel.addRow(suggestion);
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(30);


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
                int rowIndex = ((JTable) button.getParent()).getSelectedRow();
                int groupId = (int) groups.get(rowIndex)[1];

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

