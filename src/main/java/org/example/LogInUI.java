package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogInUI {

    private JFrame frame;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel imageLabel;


    public LogInUI() {


        initUI();
    }


    private void initUI() {
        frame = new JFrame("Login Panel - StudyPlatform");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);


        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("faculty.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this); // Redimensionează imaginea să ocupe tot fundalul
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(new Color(255, 255, 255, 200)); // Fundal semi-transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Enter username (CNP):");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Enter password:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Log In");
        loginButton.addActionListener(this::handleLogin);

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginPanel.add(loginButton, gbc);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> System.exit(0)); // Închide aplicația

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(closeButton, gbc);

        backgroundPanel.add(loginPanel);

        frame.setContentPane(backgroundPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    /**
     * Retrieves the user type (e.g., student, professor) by checking credentials in the database.
     */
    private String retrieveUserType(String cnp, String password) {
        String userType = null;

        String query = "SELECT tip_utilizator FROM proiect.utilizatori WHERE CNP = ? AND parola = ?";

        try (PreparedStatement stmt = DBController.db.getCon().prepareStatement(query)) {
            stmt.setString(1, cnp);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userType = rs.getString("tip_utilizator");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        return userType;
    }

    /**
     * Retrieves the user data (all fields) from the database table `utilizatori` based on the provided CNP.
     */
    private ResultSet getUserDataFromDB(String cnp) throws SQLException {
        DBController.db.execute("use proiect");

        String query = "SELECT CNP, nume, prenume, adresa, numar_telefon, email, IBAN, numar_contract, parola, tip_utilizator FROM utilizatori WHERE CNP = ?";

        PreparedStatement pstmt = DBController.db.getCon().prepareStatement(query);
        pstmt.setString(1, cnp);

        return pstmt.executeQuery();
    }

    /**
     * Handles the login button behavior.
     * Validates the credentials and initiates the appropriate UI based on the user type.
     */
    private void handleLogin(ActionEvent event) {
        String cnp = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validate empty fields
        if (cnp.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userType = retrieveUserType(cnp, password);
        if (userType == null) {
            JOptionPane.showMessageDialog(frame, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.dispose();

        try {
            ResultSet resultSet = getUserDataFromDB(cnp);
            if (resultSet != null && resultSet.next()) {
                switch (userType) {
                    case "student" -> {
                        Student student = new Student(
                                resultSet.getString("CNP"),
                                resultSet.getString("nume"),
                                resultSet.getString("prenume"),
                                resultSet.getString("adresa"),
                                resultSet.getString("numar_telefon"),
                                resultSet.getString("email"),
                                resultSet.getString("IBAN"),
                                resultSet.getInt("numar_contract"),
                                resultSet.getString("parola"),
                                resultSet.getString("tip_utilizator")
                        );
                        new StudentUI(student);
                    }
                    case "professor" -> {
                        Professor professor = new Professor(
                                resultSet.getString("CNP"),
                                resultSet.getString("nume"),
                                resultSet.getString("prenume"),
                                resultSet.getString("adresa"),
                                resultSet.getString("numar_telefon"),
                                resultSet.getString("email"),
                                resultSet.getString("IBAN"),
                                resultSet.getInt("numar_contract"),
                                resultSet.getString("parola"),
                                resultSet.getString("tip_utilizator")
                        );
                        new ProffesorUI(professor);
                    }
                    case "administrator" -> {
                        Admin admin = new Admin(
                                resultSet.getString("CNP"),
                                resultSet.getString("nume"),
                                resultSet.getString("prenume"),
                                resultSet.getString("adresa"),
                                resultSet.getString("numar_telefon"),
                                resultSet.getString("email"),
                                resultSet.getString("IBAN"),
                                resultSet.getInt("numar_contract"),
                                resultSet.getString("parola"),
                                resultSet.getString("tip_utilizator")
                        );
                        new AdminUI(admin);
                    }
                    case "super-administrator" -> {
                        SuperAdministrator sadmin = new SuperAdministrator(
                                resultSet.getString("CNP"),
                                resultSet.getString("nume"),
                                resultSet.getString("prenume"),
                                resultSet.getString("adresa"),
                                resultSet.getString("numar_telefon"),
                                resultSet.getString("email"),
                                resultSet.getString("IBAN"),
                                resultSet.getInt("numar_contract"),
                                resultSet.getString("parola"),
                                resultSet.getString("tip_utilizator")
                        );
                        new SuperAdministratorUI(sadmin);
                    }
                    default -> JOptionPane.showMessageDialog(frame, "Unknown user type!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error retrieving user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}