package org.example;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SuperAdministratorUI extends Component {
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

    private SuperAdministrator sadmin;

    public SuperAdministratorUI(SuperAdministrator sadmin) {
        this.sadmin = sadmin;

        jFrame = new JFrame("Super-Administrator Panel");
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
//        listStudentsButton.addActionListener(e -> displayStudentsForCourse());
        addUserButton.addActionListener(e -> displayAddUserPanel());
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(jFrame,
                    "Are you sure you want to log out?",
                    "Log Out Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
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

                    DBController.addUser2(sadmin, newUser);
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
        cnpField.setFont(textFieldFont);
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
                    DBController.deleteUser2(sadmin, cnp);
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

        String[] fields = {
                "nume",
                "prenume",
                "adresa",
                "numar_telefon",
                "email",
                "IBAN",
                "numar_contract",
                "parola",
                "tip_utilizator"
        };
        JComboBox<String> fieldDropdown = new JComboBox<>(fields);
        fieldDropdown.setFont(textFieldFont);

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
        panel.add(fieldDropdown, gbc);

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
            String field = (String) fieldDropdown.getSelectedItem();
            String value = valueField.getText().trim();

            if (cnp.isEmpty() || value.isEmpty()) {
                JOptionPane.showMessageDialog(jFrame, "Please provide valid inputs!");
            } else {
                try {
                    DBController.updateUser2(sadmin, cnp, field, value);
                    JOptionPane.showMessageDialog(jFrame, "User updated successfully!");
                    returnToMainPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(jFrame, "Error updating user: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

    private String showCustomInputDialog(String message) {
        JDialog dialog = new JDialog((Frame) null, "Input", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(messageLabel, gbc);


        JTextField inputField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        dialog.add(inputField, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");


        okButton.setBackground(Color.GRAY);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));

        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));


        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);


        final String[] result = {null};
        okButton.addActionListener(e -> {
            result[0] = inputField.getText().trim();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });


        dialog.setVisible(true);

        return result[0];
    }


    private void displaySearchUserPanel() {
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
            String name = showCustomInputDialog("Enter name:");
            String firstname = showCustomInputDialog("Enter firstname:");

            if (name == null || firstname == null || name.trim().isEmpty() || firstname.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Please provide both name and firstname!");
                return;
            }

            try {
                ResultSet rs = DBController.searchUser2(sadmin, name.trim(), firstname.trim());
                JPanel resultsPanel = new JPanel();
                resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
                resultsPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        "Search Results",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)
                ));

                boolean hasResults = false;

                while (rs.next()) {
                    hasResults = true;

                    String cnp = rs.getString("cnp");
                    String nume = rs.getString("nume");
                    String prenume = rs.getString("prenume");
                    String adresa = rs.getString("adresa");
                    String numarTelefon = rs.getString("numar_telefon");
                    String email = rs.getString("email");
                    String iban = rs.getString("IBAN");
                    String numarContract = rs.getString("numar_contract");
                    String tipUtilizator = rs.getString("tip_utilizator");


                    JPanel userPanel = new JPanel(new GridLayout(2, 1));
                    userPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                    userPanel.setBackground(new Color(245, 245, 245));
                    userPanel.setMaximumSize(new Dimension(580, 100));

                    JLabel userLabel = new JLabel(
                            "<html><b>CNP:</b> " + cnp +
                                    " | <b>Name:</b> " + nume + " " + prenume +
                                    " | <b>Phone:</b> " + numarTelefon +
                                    " | <b>Email:</b> " + email +
                                    "</html>"
                    );
                    userLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JLabel detailsLabel = new JLabel(
                            "<html><b>Address:</b> " + adresa +
                                    " | <b>IBAN:</b> " + iban +
                                    " | <b>Contract:</b> " + numarContract +
                                    " | <b>User Type:</b> " + tipUtilizator +
                                    "</html>"
                    );
                    detailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    userPanel.add(userLabel);
                    userPanel.add(detailsLabel);

                    resultsPanel.add(userPanel);
                    resultsPanel.add(Box.createVerticalStrut(10));
                }

                if (!hasResults) {
                    resultsPanel.add(new JLabel("No results found for the given name"));
                }


                JScrollPane scrollPane = new JScrollPane(resultsPanel);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                mainPanel.removeAll();
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private void displayFilterUserPanel() {
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
            String userType = showCustomInputDialog("Enter user type:");
            if (userType == null || userType.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Please provide a valid user type!");
                return;
            }

            try {
                ResultSet rs = DBController.filterUser2(sadmin, userType.trim());
                JPanel resultsPanel = new JPanel();
                resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
                resultsPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        "Filter Results",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)
                ));

                boolean hasResults = false;

                while (rs.next()) {
                    hasResults = true;

                    String cnp = rs.getString("cnp");
                    String nume = rs.getString("nume");
                    String prenume = rs.getString("prenume");
                    String adresa = rs.getString("adresa");
                    String numarTelefon = rs.getString("numar_telefon");
                    String email = rs.getString("email");
                    String iban = rs.getString("IBAN");
                    String numarContract = rs.getString("numar_contract");
                    String tipUtilizator = rs.getString("tip_utilizator");


                    JPanel userPanel = new JPanel(new GridLayout(2, 1));
                    userPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                    userPanel.setBackground(new Color(245, 245, 245));
                    userPanel.setMaximumSize(new Dimension(580, 100));

                    JLabel userLabel = new JLabel(
                            "<html><b>CNP:</b> " + cnp +
                                    " | <b>Name:</b> " + nume + " " + prenume +
                                    " | <b>Phone:</b> " + numarTelefon +
                                    " | <b>Email:</b> " + email +
                                    "</html>"
                    );
                    userLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JLabel detailsLabel = new JLabel(
                            "<html><b>Address:</b> " + adresa +
                                    " | <b>IBAN:</b> " + iban +
                                    " | <b>Contract:</b> " + numarContract +
                                    " | <b>User Type:</b> " + tipUtilizator +
                                    "</html>"
                    );
                    detailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    userPanel.add(userLabel);
                    userPanel.add(detailsLabel);

                    resultsPanel.add(userPanel);
                    resultsPanel.add(Box.createVerticalStrut(10));
                }

                if (!hasResults) {
                    resultsPanel.add(new JLabel("No results found for the given user type."));
                }


                JScrollPane scrollPane = new JScrollPane(resultsPanel);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

                mainPanel.removeAll();
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                mainPanel.revalidate();
                mainPanel.repaint();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error: " + ex.getMessage());
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

    private void displayStudentsForCourse(int courseId) {
        try {
            ResultSet rs = DBController.getStudentsForCourse2(sadmin, courseId);
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

    private String showCustomScrollDialog(String title, List<String> options) {
        JDialog dialog = new JDialog((Frame) null, title, true);
        dialog.setLayout(new GridBagLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;


        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(titleLabel, gbc);



        JList<String> list = new JList<>(options.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        list.setVisibleRowCount(8);

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        dialog.add(scrollPane, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");


        okButton.setBackground(Color.GRAY);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));

        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));


        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        dialog.add(buttonPanel, gbc);


        final String[] result = {null};
        okButton.addActionListener(e -> {
            if (list.getSelectedValue() != null) {
                result[0] = list.getSelectedValue();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a course!");
            }
        });

        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });


        dialog.setVisible(true);

        return result[0];
    }


    private void displayAssignProfessorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel cnpLabel = new JLabel("Press 'Assign' to assign a professor to a course:");
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
            String profCNP = showCustomInputDialog("Enter professor CNP:");

            if (profCNP == null || profCNP.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Error: You must enter a valid CNP.");
                return;
            }

            try {

                if (!DBController.doesCNPExist(sadmin, profCNP)) {
                    JOptionPane.showMessageDialog(null, "Error: No professor found with the provided CNP.");
                    return;
                }

                List<String> courses = getAllCourseNamesFromDB();
                if (courses.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No courses found in the database.");
                    return;
                }

                String selectedCourse = showCustomScrollDialog("Select a course:", courses);

                if (selectedCourse != null) {

                    int courseId = getCourseIdByName(selectedCourse);

                    DBController.assignProfessor2(sadmin, profCNP, courseId);
                    JOptionPane.showMessageDialog(null, "The professor was assigned to the course successfully.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
        mainPanel.revalidate();
        mainPanel.repaint();
    }


    private List<String> getAllCourseNamesFromDB() throws SQLException {
        List<String> courses = new ArrayList<>();
        try (Statement stmt = DBController.db.getCon().createStatement()) {
            stmt.execute("USE proiect");
        }
        String query = "SELECT nume FROM materii";
        try (PreparedStatement pstmt = DBController.db.getCon().prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                courses.add(rs.getString("nume"));
            }
        }
        return courses;
    }


    private int getCourseIdByName(String courseName) throws SQLException {
        String query = "SELECT id FROM materii WHERE nume = ?";
        try (PreparedStatement pstmt = DBController.db.getCon().prepareStatement(query)) {
            pstmt.setString(1, courseName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("Course not found: " + courseName);
                }
            }
        }
    }

    private void displaySearchCoursePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 14);

        JLabel courseLabel = new JLabel("Select a course from the list:");
        courseLabel.setFont(labelFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(courseLabel, gbc);

        String[] courses = {
                "Introduction to Programming",
                "Data Structures",
                "Operating Systems",
                "Computer Networks",
                "Database Systems",
                "Artificial Intelligence",
                "Machine Learning",
                "Web Development",
                "Cybersecurity",
                "Mobile Application Development"
        };


        JComboBox<String> courseDropdown = new JComboBox<>(courses);
        courseDropdown.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(courseDropdown, gbc);


        JButton searchButton = new JButton("Search");
        JButton cancelButton = new JButton("Cancel");

        searchButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setBackground(Color.LIGHT_GRAY);

        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 2;
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
            String selectedCourse = (String) courseDropdown.getSelectedItem();

            if (selectedCourse == null || selectedCourse.trim().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Please select a course.");
                return;
            }

            try {
                ResultSet rs = DBController.searchCourseByName2(sadmin, selectedCourse);
                List<Integer> courseIds = new ArrayList<>();
                List<String> courseDetails = new ArrayList<>();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    courseIds.add(id);

                    String course = rs.getString("nume");
                    String professorFirstName = rs.getString("prenume_profesor");
                    String professorLastName = rs.getString("nume_profesor");

                    courseDetails.add(String.format("ID: %d | Course: %s | Professor: %s %s",
                            id, course, professorFirstName, professorLastName));
                }

                if (!courseDetails.isEmpty()) {
                    String selectedCourseIdInput = showCustomScrollDialog(
                            "Search Results - Select a Course ID",
                            courseDetails
                    );

                    if (selectedCourseIdInput != null) {
                        try {
                            int selectedCourseId = Integer.parseInt(selectedCourseIdInput.split("\\|")[0].replace("ID:", "").trim());

                            if (courseIds.contains(selectedCourseId)) {
                                displayStudentsForCourse(selectedCourseId);
                            } else {
                                JOptionPane.showMessageDialog(mainPanel, "Invalid course ID. No course found with the provided ID.");
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(mainPanel, "Invalid input. Please select a valid numeric ID.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "No results found for the selected course.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> returnToMainPanel());
    }

}
