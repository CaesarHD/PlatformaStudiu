package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminUI extends Component
{
    private JFrame jFrame;
    private JPanel mainPanel;
    private JMenuBar menuBar;
    private JButton deleteUserButton;
    private JButton updateUserButton;
    private JButton searchUserButton;
    private JButton filterUserButton;
    private JButton assignProfessorButton;
    private JButton searchCourseButton;
    private JButton logoutButton;
    private JButton addUserButton;

    private Admin admin;


    public AdminUI(Admin admin) {
        this.admin = admin;

        jFrame = new JFrame("Admin Panel");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());


        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        jFrame.setJMenuBar(menuBar);

        addUserButton = new JButton("Add User");
        deleteUserButton = new JButton("Delete User");
        updateUserButton = new JButton("Update User");
        searchUserButton = new JButton("Search User");
        filterUserButton = new JButton("Filter Users");
        assignProfessorButton = new JButton("Assign Professor");
        searchCourseButton = new JButton("Search Course");
        logoutButton = new JButton("Log Out");

        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        buttonPanel.add(addUserButton);
        buttonPanel.add(deleteUserButton);
        buttonPanel.add(updateUserButton);
        buttonPanel.add(searchUserButton);
        buttonPanel.add(filterUserButton);
        buttonPanel.add(assignProfessorButton);
        buttonPanel.add(searchCourseButton);
        buttonPanel.add(logoutButton);

        jFrame.add(buttonPanel, BorderLayout.WEST);

        mainPanel = new JPanel(new BorderLayout());
        jFrame.add(mainPanel, BorderLayout.CENTER);

        deleteUserButton.addActionListener(e -> displayDeleteUserPanel());
        updateUserButton.addActionListener(e -> displayUpdateUserPanel());
        searchUserButton.addActionListener(e -> displaySearchUserPanel());
        filterUserButton.addActionListener(e -> displayFilterUserPanel());
        assignProfessorButton.addActionListener(e -> displayAssignProfessorPanel());
        searchCourseButton.addActionListener(e -> displaySearchCoursePanel());
//        listStudentsButton.addActionListener(e -> displayStudentsForCourse());
        addUserButton.addActionListener(e -> displayAddUserPanel());
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(jFrame,
                    "Are you sure you want to log out?",
                    "Log Out Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION)
            {
                jFrame.dispose();
                new LogInUI();
            }
        });

        jFrame.setVisible(true);
    }

    private void displayAddUserPanel() {
        JPanel panel = new JPanel(new GridLayout(10, 1));
        JLabel cnpLabel = new JLabel("CNP:");
        JTextField cnpField = new JTextField();
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();
        JLabel secondNameLabel = new JLabel("Second Name:");
        JTextField secondNameField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField();
        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        JTextField phoneNumberField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel ibanLabel = new JLabel("IBAN:");
        JTextField ibanField = new JTextField();
        JLabel contractNumberLabel = new JLabel("Contract Number:");
        JTextField contractNumberField = new JTextField();
        JLabel userTypeLabel = new JLabel("User Type (student, professor):");
        JTextField userTypeField = new JTextField();

        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        panel.add(cnpLabel);
        panel.add(cnpField);
        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(secondNameLabel);
        panel.add(secondNameField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(phoneNumberLabel);
        panel.add(phoneNumberField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(ibanLabel);
        panel.add(ibanField);
        panel.add(contractNumberLabel);
        panel.add(contractNumberField);
        panel.add(userTypeLabel);
        panel.add(userTypeField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        addButton.addActionListener(e -> {
            String cnp = cnpField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String secondName = secondNameField.getText().trim();
            String address = addressField.getText().trim();
            String phoneNumber = phoneNumberField.getText().trim();
            String email = emailField.getText().trim();
            String iban = ibanField.getText().trim();
            String contractNumber = contractNumberField.getText().trim();
            String userType = userTypeField.getText().trim();
            String password = "defaultPassword";

            int parsedContractNumber;
            try {
                parsedContractNumber = Integer.parseInt(contractNumber.trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(jFrame, "Invalid contract number! Please enter a numeric value.");
                return;
            }

            if (cnp.isEmpty() || firstName.isEmpty() || secondName.isEmpty() || address.isEmpty() ||
                    phoneNumber.isEmpty() || email.isEmpty() || iban.isEmpty() || contractNumber.isEmpty() ||
                    userType.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please fill in all fields!");
            } else {
                try {
                    User newUser = new User(cnp, firstName, secondName, address, phoneNumber, email, iban,
                            parsedContractNumber, password, userType);

                    DBController.addUser(admin, newUser); // Apel DBController
                    JOptionPane.showMessageDialog(jFrame, "User added successfully!");
                    returnToMainPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error adding user: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

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

        deleteButton.addActionListener(e -> {
            String cnp = cnpField.getText().trim();
            if (cnp.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide a valid CNP!");
            } else {
                try {
                    DBController.deleteUser(admin, cnp);
                    JOptionPane.showMessageDialog(jFrame, "User deleted successfully!");
                    returnToMainPanel();
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

        updateButton.addActionListener(e -> {
            String cnp = cnpField.getText().trim();
            String field = fieldField.getText().trim();
            String value = valueField.getText().trim();

            if (cnp.isEmpty() || field.isEmpty() || value.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide valid inputs!");
            } else {
                try {
                    DBController.updateUser(admin, cnp, field, value);
                    JOptionPane.showMessageDialog(jFrame, "User updated successfully!");
                    returnToMainPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error updating user: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

    private void displaySearchUserPanel()
    {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel nameLabel = new JLabel("Press search to find a user by name");
        //JTextField nameField = new JTextField();
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        panel.add(nameLabel);
        //panel.add(nameField);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        searchButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter name: ");
            String firstname = JOptionPane.showInputDialog("Enter firstname: ");
            try {
                ResultSet rs = DBController.searchUser(admin, name,firstname);
                StringBuilder results = new StringBuilder();


                while (rs.next()) {
                    String cnp = rs.getString("cnp");
                    String nume = rs.getString("nume");
                    String prenume = rs.getString("prenume");
                    String adresa = rs.getString("adresa");
                    String numarTelefon = rs.getString("numar_telefon");
                    String email = rs.getString("email");
                    String iban = rs.getString("IBAN");
                    String numarContract = rs.getString("numar_contract");
                    String tipUtilizator = rs.getString("tip_utilizator");

                    results.append("CNP: ").append(cnp).append(", Last Name: ").append(nume).append(", First Name: ").append(prenume)
                            .append(", Adress: ").append(adresa).append(", Phone number: ").append(numarTelefon)
                            .append(", Email: ").append(email).append(", IBAN: ").append(iban)
                            .append(", Contract number: ").append(numarContract).append(",user type: ").append(tipUtilizator)
                            .append("\n");
                }


                JOptionPane.showMessageDialog(mainPanel, results.length() > 0 ? results.toString() : "No results found for the given name.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void displayFilterUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel typeLabel = new JLabel("Press 'Filter' to enter an user type (e.g., professor, administrator):");
       // JTextField typeField = new JTextField();
        JButton filterButton = new JButton("Filter");
        JButton cancelButton = new JButton("Cancel");

        JPanel inputPanel = new JPanel(new GridLayout(2, 1));
        inputPanel.add(typeLabel);
        //inputPanel.add(typeField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(filterButton);
        buttonPanel.add(cancelButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        filterButton.addActionListener(e -> {
            String userType = JOptionPane.showInputDialog("Enter user type:").trim();
            try {
                ResultSet rs = DBController.filterUser(admin, userType);
                StringBuilder results = new StringBuilder();

                while (rs.next()) {
                    String cnp = rs.getString("cnp");
                    String nume = rs.getString("nume");
                    String prenume = rs.getString("prenume");
                    String adresa = rs.getString("adresa");
                    String numarTelefon = rs.getString("numar_telefon");
                    String email = rs.getString("email");
                    String iban = rs.getString("IBAN");
                    String numarContract = rs.getString("numar_contract");
                    String tipUtilizator = rs.getString("tip_utilizator");

                    results.append("CNP: ").append(cnp).append(", Name: ").append(nume).append(", First name: ").append(prenume)
                            .append(", Adress: ").append(adresa).append(", Telephone number : ").append(numarTelefon)
                            .append(", Email: ").append(email).append(", IBAN: ").append(iban)
                            .append(", Contract number : ").append(numarContract).append(", user type: ").append(tipUtilizator)
                            .append("\n");
                }

                JOptionPane.showMessageDialog(null, results.length() > 0 ? results.toString() : "No results found for the given user type.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JTable buildUserTable(ResultSet resultSet) throws SQLException, SQLException {

        String[] columns = {"CNP", "First Name", "Second Name", "User Type"};
        List<String[]> data = new ArrayList<>();

        while (resultSet.next()) {
            String[] row = {
                    resultSet.getString("CNP"),
                    resultSet.getString("nume"),
                    resultSet.getString("prenume"),
                    resultSet.getString("tip_utilizator")
            };
            data.add(row);
        }


        String[][] dataArray = data.toArray(new String[0][]);
        return new JTable(dataArray, columns);
    }

    private void displayStudentsForCourse(int courseId)
    {
        try {
            ResultSet rs = DBController.getStudentsForCourse(admin, courseId);
            StringBuilder result = new StringBuilder("All students enrolled:\n");

            while (rs.next()) {
                String firstName = rs.getString("prenume");
                String lastName = rs.getString("nume");
                result.append(String.format("Student: %s %s\n", firstName, lastName));
            }

            if (result.length() > 0) {
                JOptionPane.showMessageDialog(null, result.toString());
            } else {
                JOptionPane.showMessageDialog(null, "No students enrolled for this course. ");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error " + ex.getMessage());
        }
    }


    private void displayAssignProfessorPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));
        JLabel cnpLabel = new JLabel(" Press 'Assign' and enter the CNP of the professor and the ID of the course:");
//        JTextField cnpField = new JTextField();
//        JLabel courseIdLabel = new JLabel("Enter Course ID:");
//        JTextField courseIdField = new JTextField();
        JButton assignButton = new JButton("Assign");
        JButton cancelButton = new JButton("Cancel");

        panel.add(cnpLabel);
//        panel.add(cnpField);
//        panel.add(courseIdLabel);
//        panel.add(courseIdField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        assignButton.addActionListener(e -> {
            String profCNP = JOptionPane.showInputDialog("Enter professor CNP: ");
            String idMaterieStr = JOptionPane.showInputDialog("Enter course ID:");

            try {
                int idMaterie = Integer.parseInt(idMaterieStr);
                DBController.assignProfessor(admin, profCNP, idMaterie);
                JOptionPane.showMessageDialog(null, "The professor was assigned to the course successfully. ");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "The id has to be a valid number. ");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void displaySearchCoursePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JLabel courseLabel = new JLabel("Press 'Search' to find a course by name:");
        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        panel.add(courseLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        searchButton.addActionListener(e -> {
            String courseName = JOptionPane.showInputDialog("Enter course name:");

            if (courseName == null || courseName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Course name cannot be empty.");
                return;
            }

            try {
                ResultSet rs = DBController.searchCourseByName(admin, courseName);
                List<Integer> courseIds = new ArrayList<>();
                StringBuilder result = new StringBuilder();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    courseIds.add(id);
                    String course = rs.getString("nume");
                    String professorFirstName = rs.getString("prenume_profesor");
                    String professorLastName = rs.getString("nume_profesor");

                    result.append(String.format("ID: %d, Course: %s, Professor: %s %s\n", id, course, professorFirstName, professorLastName));
                }

                if (result.length() > 0) {
                    JOptionPane.showMessageDialog(null, result.toString());
                    String selectedCourseIdInput = JOptionPane.showInputDialog("Enter the course ID to view enrolled students:");

                    try {
                        int selectedCourseId = Integer.parseInt(selectedCourseIdInput);

                        if (courseIds.contains(selectedCourseId)) {
                            displayStudentsForCourse(selectedCourseId);
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid course ID. No course found with the provided ID.");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid numeric ID.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No results found for the given course name.");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }
}
