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
        try {

            DataBase database = new DataBase();
            database.connect("root", "root");
            DBController.setDbConnection(database);

            initUI();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the database: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
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
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(new Color(255, 255, 255, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Enter email:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Enter password:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Log In");
        loginButton.setBackground(Color.LIGHT_GRAY);

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
        closeButton.setBackground(Color.LIGHT_GRAY);

        closeButton.addActionListener(e -> System.exit(0));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginPanel.add(closeButton, gbc);

        backgroundPanel.add(loginPanel);

        frame.setContentPane(backgroundPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String getUserType(String email, String password) throws SQLException
    {
        DBController.db.execute("use proiect");

        String query = "SELECT tip_utilizator FROM utilizatori WHERE email = '" + email + "' AND parola = '" + password + "';";

        PreparedStatement pstmt = DBController.db.getCon().prepareStatement(query);

        ResultSet resultSet = pstmt.executeQuery();

        String type=null;

        if (resultSet.next())
        {
            type = resultSet.getString("tip_utilizator");
        }
        return type;
    }

    private void handleLogin(ActionEvent event) {
        String email = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userType;
        try {
            userType = DBController.retrieveUserType(email, password);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(frame,
                    "An error occurred while connecting to the database: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userType == null) {
            JOptionPane.showMessageDialog(frame, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.dispose();

        try {
            String type = getUserType(email, password);
            if (type != null )
            {
                switch (type)
                {
                    case "student" ->
                    {
                        User user = DBController.getUser(User.findUser(usernameField.getText(), passwordField.getText()));
                        if (user instanceof Student student) {

                            new StudentUI(student);
                        } else {
                            System.out.println("Error: Retrieved user is not a Student.");
                        }
                    }
                    case "profesor" -> {
                        Professor professor;
                        professor = DBController.initializeProfessor(usernameField.getText(), passwordField.getText());

                        ProffesorUI pUi = new ProffesorUI(professor);
                        pUi.show();

                    }
                    case "administrator" -> {
                        User user = DBController.getUser(User.findUser(usernameField.getText(), passwordField.getText()));
                        if (user instanceof Admin admin) {

                            new AdminUI(admin);
                        } else {
                            System.out.println("Error: Retrieved user is not an Admin.");
                        }

                    }
                    case "super-administrator" -> {
                        User user = DBController.getUser(User.findUser(usernameField.getText(), passwordField.getText()));
                        if (user instanceof SuperAdministrator sadmin) {

                            new SuperAdministratorUI(sadmin);
                        } else {
                            System.out.println("Error: Retrieved user is not a Super-Administrator.");
                        }
                    }
                    default -> JOptionPane.showMessageDialog(frame, "Unknown user type!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error retrieving user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            new LogInUI();
        }
    }
}