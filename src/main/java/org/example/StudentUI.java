package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentUI {

    private final JFrame jFrame;
    private final JPanel mainPanel; // Panel to dynamically update content
    private final Student student;
    private final DBController dbController;

    public StudentUI(Student student, DBController dbController) {
        this.student = student;
        this.dbController = dbController;

        // Initialize JFrame
        jFrame = new JFrame("StudyPlatform");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        // Initialize Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);

        // Initialize Buttons
        JButton subjectsButton = new JButton("My subjects");
        JButton studentsGroupsButton = new JButton("Students groups");
        JButton gradesButton = new JButton("Grades");
        menuBar.add(subjectsButton);
        menuBar.add(studentsGroupsButton);
        menuBar.add(gradesButton);

        // Add Menu Bar to the Frame
        jFrame.setJMenuBar(menuBar);

        // Initialize Main Panel (Initially Empty)
        mainPanel = new JPanel(new BorderLayout());
        jFrame.add(mainPanel, BorderLayout.CENTER);

        // Add Action Listener for "My Subjects" Button
        subjectsButton.addActionListener(e -> displaySubjectsTable());
        gradesButton.addActionListener(e -> displayGradesTable());

        // Show JFrame
        jFrame.setVisible(true);
    }

    private void displaySubjectsTable() {
        try {
            // Fetch subjects and final grades
            List<Object[]> subjectsAndGrades = fetchSubjectsAndGradesFromDatabase();

            // Create Table Model with Two Columns: "Subjects" and "Final Grade"
            DefaultTableModel tableModel = new DefaultTableModel();
            tableModel.addColumn("Subjects");       // Column 1
            tableModel.addColumn("Final Grade");    // Column 2

            // Add rows to the table model
            for (Object[] row : subjectsAndGrades) {
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
            JOptionPane.showMessageDialog(jFrame, "Error fetching subjects and grades: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Object[]> fetchSubjectsAndGradesFromDatabase() throws SQLException {
        List<Object[]> data = new ArrayList<>();
        String query = student.getSubjectsFinalGrade(); // Query already fetches subject name and grade

        // Execute the query and fetch results
        try (Statement stmt = dbController.db.getCon().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String subjectName = rs.getString("nume");      // Get subject name
                int finalGrade = rs.getInt("nota_finala");      // Get final grade
                data.add(new Object[]{subjectName, finalGrade}); // Add to the list as an Object array
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
                float grade = rs.getFloat("grade");                // Get grade (can be null)
                data.add(new Object[]{subjectName, activityType, grade != 0 ? grade : "N/A"}); // Handle null grades
            }
        }

        return data;
    }


}
