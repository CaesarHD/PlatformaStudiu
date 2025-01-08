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

        jFrame = new JFrame("Administrator Panel");
        jFrame.setSize(800, 600);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());


        menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        menuBar.add(menu);
        jFrame.setJMenuBar(menuBar);

        addUserButton = createStyledButton("Add User");
        deleteUserButton = createStyledButton("Delete User");
        updateUserButton = createStyledButton("Update User");
        searchUserButton = createStyledButton("Search User");
        filterUserButton = createStyledButton("Filter Users");
        assignProfessorButton = createStyledButton("Assign Professor");
        searchCourseButton = createStyledButton("Search Course");
        logoutButton = createStyledButton("Log Out");

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
        addUserButton.addActionListener(e -> displayAddUserPanel());
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JTextField addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Font labelFont, Font textFieldFont) {
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        JTextField textField = new JTextField(20);
        textField.setFont(textFieldFont);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(textField, gbc);

        return textField;
    }

    private void displayAddUserPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);

        JTextField cnpField = addRow(panel, gbc, 0, "CNP:", labelFont, textFieldFont);
        JTextField firstNameField = addRow(panel, gbc, 1, "First Name:", labelFont, textFieldFont);
        JTextField secondNameField = addRow(panel, gbc, 2, "Second Name:", labelFont, textFieldFont);
        JTextField addressField = addRow(panel, gbc, 3, "Address:", labelFont, textFieldFont);
        JTextField phoneNumberField = addRow(panel, gbc, 4, "Phone Number:", labelFont, textFieldFont);
        JTextField emailField = addRow(panel, gbc, 5, "Email:", labelFont, textFieldFont);
        JTextField ibanField = addRow(panel, gbc, 6, "IBAN:", labelFont, textFieldFont);
        JTextField contractNumberField = addRow(panel, gbc, 7, "Contract Number:", labelFont, textFieldFont);
        JTextField userTypeField = addRow(panel, gbc, 8, "User Type (student, professor):", labelFont, textFieldFont);


        JButton addButton = createStyledButton("Add ");
        JButton cancelButton = createStyledButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

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

                    DBController.addUser(admin, newUser);
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
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel cnpLabel = new JLabel("Enter CNP:");
        cnpLabel.setFont(labelFont);
        JTextField cnpField = new JTextField(20);

        cnpField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(cnpLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(cnpField, gbc);


        JButton deleteButton = createStyledButton("Delete");
        JButton cancelButton = createStyledButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);


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


    private void returnToMainPanel()
    {
        mainPanel.removeAll();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void displayUpdateUserPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font textFieldFont = new Font("Arial", Font.PLAIN, 14);


        JLabel cnpLabel = new JLabel("Enter CNP:");
        cnpLabel.setFont(labelFont);
        JTextField cnpField = new JTextField(20);
        cnpField.setFont(textFieldFont);
        cnpField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));


        JLabel fieldLabel = new JLabel("Field to update:");
        fieldLabel.setFont(labelFont);
        JTextField fieldField = new JTextField(20);
        fieldField.setFont(textFieldFont);
        fieldField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));


        JLabel valueLabel = new JLabel("New value:");
        valueLabel.setFont(labelFont);
        JTextField valueField = new JTextField(20);
        valueField.setFont(textFieldFont);
        valueField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(cnpLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(cnpField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(fieldField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(valueLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(valueField, gbc);

        JButton updateButton = createStyledButton("Update");
        JButton cancelButton = createStyledButton("Cancel");

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

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
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel nameLabel = new JLabel("Press 'Search' to find a user by name:");
        nameLabel.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(nameLabel, gbc);


        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        searchButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setBackground(Color.LIGHT_GRAY);

        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();


        searchButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter name: ");
            String firstname = JOptionPane.showInputDialog("Enter firstname: ");
            try {
                ResultSet rs = DBController.searchUser(admin, name, firstname);
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
                            .append(", Contract number: ").append(numarContract).append(", user type: ").append(tipUtilizator)
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


    private void displayFilterUserPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel typeLabel = new JLabel("Press 'Filter' to enter a user type (e.g., professor, administrator):");
        typeLabel.setFont(labelFont);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(typeLabel, gbc);


        JButton filterButton = new JButton("Filter");
        JButton cancelButton = new JButton("Cancel");

        filterButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setBackground(Color.LIGHT_GRAY);

        filterButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(filterButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);


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
                            .append(", Adress: ").append(adresa).append(", Telephone number: ").append(numarTelefon)
                            .append(", Email: ").append(email).append(", IBAN: ").append(iban)
                            .append(", Contract number: ").append(numarContract).append(", User type: ").append(tipUtilizator)
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


    private void displayAssignProfessorPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel cnpLabel = new JLabel("Press 'Assign' and enter the CNP of the professor and the ID of the course:");
        cnpLabel.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(cnpLabel, gbc);


        JButton assignButton = new JButton("Assign");
        JButton cancelButton = new JButton("Cancel");

        assignButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setBackground(Color.LIGHT_GRAY);

        assignButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(assignButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);


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
                JOptionPane.showMessageDialog(null, "The professor was assigned to the course successfully.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "The ID has to be a valid number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void displaySearchCoursePanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);


        JLabel courseLabel = new JLabel("Press 'Search' to find a course by name:");
        courseLabel.setFont(labelFont);


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(courseLabel, gbc);


        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        searchButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setBackground(Color.LIGHT_GRAY);

        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);


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
        mainPanel.revalidate();
        mainPanel.repaint();
    }

}
