package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminUI {
    private JFrame jFrame;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JButton deleteUserButton;
    private JButton updateUserButton;
    private JButton searchUserButton;
    private JButton filterUserButton;
    private JButton assignProfessorButton;
    private JButton searchCourseButton;
    private JButton listStudentsButton;

    private Admin admin;
    private DBController dbController;

    public AdminUI(Admin admin, DBController dbController) {
        this.admin = admin;
        this.dbController = dbController;

        // Set up JFrame
        jFrame = new JFrame("Admin Panel");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        // Menu bar
        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        jFrame.setJMenuBar(menuBar);

        // Buttons
        deleteUserButton = new JButton("Delete User");
        updateUserButton = new JButton("Update User");
        searchUserButton = new JButton("Search User");
        filterUserButton = new JButton("Filter Users");
        assignProfessorButton = new JButton("Assign Professor");
        searchCourseButton = new JButton("Search Course");
        listStudentsButton = new JButton("List Students");

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(updateUserButton);
        buttonPanel.add(searchUserButton);
        buttonPanel.add(filterUserButton);
        buttonPanel.add(assignProfessorButton);
        buttonPanel.add(searchCourseButton);
        buttonPanel.add(listStudentsButton);

        jFrame.add(buttonPanel, BorderLayout.WEST);

        // Main panel
        mainPanel = new JPanel(new BorderLayout());
        jFrame.add(mainPanel, BorderLayout.CENTER);

        // Action listeners
        deleteUserButton.addActionListener(e -> displayDeleteUserPanel());
        updateUserButton.addActionListener(e -> displayUpdateUserPanel());
        searchUserButton.addActionListener(e -> displaySearchUserPanel());
        filterUserButton.addActionListener(e -> displayFilterUserPanel());
        assignProfessorButton.addActionListener(e -> displayAssignProfessorPanel());
        searchCourseButton.addActionListener(e -> displaySearchCoursePanel());
      //  listStudentsButton.addActionListener(e -> displayListStudentsPanel());

        // Show JFrame
        jFrame.setVisible(true);
    }

    // Example: Delete User Panel
    private void displayDeleteUserPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel cnpLabel = new JLabel("Enter CNP:");
        JTextField cnpField = new JTextField();
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        panel.add(cnpLabel);
        panel.add(cnpField);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        // Delete action
        deleteButton.addActionListener(e -> {
            String cnp = cnpField.getText().trim();
            if (cnp.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide a valid CNP!");
            } else {
                try {
                    String query = admin.deleteUser(cnp); // Admin method throws SQLException
                    dbController.db.execute(query);
                    JOptionPane.showMessageDialog(jFrame, "User deleted successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error deleting user: " + ex.getMessage());
                }
            }
        });


        cancelButton.addActionListener(e -> returnToMainPanel());
    }


    private void returnToMainPanel() {
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Example: Update User Panel
    private void displayUpdateUserPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JLabel cnpLabel = new JLabel("Enter CNP:");
        JTextField cnpField = new JTextField();
        JLabel fieldLabel = new JLabel("Field to update:");
        JTextField fieldField = new JTextField();
        JLabel valueLabel = new JLabel("New value:");
        JTextField valueField = new JTextField();

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");

        panel.add(cnpLabel);
        panel.add(cnpField);
        panel.add(fieldLabel);
        panel.add(fieldField);
        panel.add(valueLabel);
        panel.add(valueField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        // Update action
        updateButton.addActionListener(e -> {
            String cnp = cnpField.getText().trim();
            String field = fieldField.getText().trim();
            String value = valueField.getText().trim();

            if (cnp.isEmpty() || field.isEmpty() || value.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide valid inputs!");
            } else {
                try {
                    String query = admin.updateUser(cnp, field, value); // Admin method throws SQLException
                    dbController.db.execute(query);
                    JOptionPane.showMessageDialog(jFrame, "User updated successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error updating user: " + ex.getMessage());
                }
            }
        });

        // Cancel action
        cancelButton.addActionListener(e -> returnToMainPanel());
    }

    private void displaySearchUserPanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel nameLabel = new JLabel("Enter User Name:");
        JTextField nameField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        panel.add(nameLabel);
        panel.add(nameField);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        searchButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide a valid name!");
            } else {
                try {
                    String result = admin.searchUser(name); // Admin method to search users by name
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(jFrame, "No users found with the given name.");
                    } else {
                        JOptionPane.showMessageDialog(jFrame, "Results:\n" + result);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error searching user: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

    // Filter User Panel
    private void displayFilterUserPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel typeLabel = new JLabel("Enter User Type (e.g., profesor, administrator):");
        JTextField typeField = new JTextField();
        JButton filterButton = new JButton("Filter");
        JButton cancelButton = new JButton("Cancel");

        panel.add(typeLabel);
        panel.add(typeField);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(filterButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        filterButton.addActionListener(e -> {
            String userType = typeField.getText().trim();
            if (userType.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide a valid user type!");
            } else {
                try {
                    String result = admin.filterUser(userType); // Admin method to filter users by type
                    if (result.isEmpty()) {
                        JOptionPane.showMessageDialog(jFrame, "No users found for the given type.");
                    } else {
                        JOptionPane.showMessageDialog(jFrame, "Filtered Users:\n" + result);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error filtering users: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

    // Assign Professor Panel
    private void displayAssignProfessorPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JLabel cnpLabel = new JLabel("Enter Professor CNP:");
        JTextField cnpField = new JTextField();
        JLabel courseIdLabel = new JLabel("Enter Course ID:");
        JTextField courseIdField = new JTextField();
        JButton assignButton = new JButton("Assign");
        JButton cancelButton = new JButton("Cancel");

        panel.add(cnpLabel);
        panel.add(cnpField);
        panel.add(courseIdLabel);
        panel.add(courseIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        assignButton.addActionListener(e -> {
            String cnp = cnpField.getText().trim();
            String courseId = courseIdField.getText().trim();

            if (cnp.isEmpty() || courseId.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide valid inputs!");
            } else {
                try {
                    admin.assignProfessor(cnp, Integer.parseInt(courseId)); // Admin method to assign professor
                    JOptionPane.showMessageDialog(jFrame, "Professor assigned successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error assigning professor: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

    // Search Course Panel
    private void displaySearchCoursePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel courseLabel = new JLabel("Enter Course Name:");
        JTextField courseField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        panel.add(courseLabel);
        panel.add(courseField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        searchButton.addActionListener(e -> {
            String courseName = courseField.getText().trim();
            if (courseName.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide a valid course name!");
            } else {
                try {
                    String courses = admin.searchCourseByName(courseName); // Updated to return a string
                    if (courses.isEmpty()) {
                        JOptionPane.showMessageDialog(jFrame, "No courses found with the given name.");
                    } else {
                        JOptionPane.showMessageDialog(jFrame, "Results:\n" + courses);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error searching courses: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }


}
