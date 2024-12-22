package org.example;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProffesorUI extends UI {

    private final JButton classes = new JButton("Classes");
    private final JButton currentDay = new JButton("Current Day");
    private final JButton meetings = new JButton("Meetings");
    private final JButton allActivities = new JButton("All Activities");
    private final JButton classBook = new JButton("Class Book");
    private final JButton profile = new JButton("Profile");
    private final JButton logOut = new JButton("Log Out");
    private final List<JButton> classButtons = new ArrayList<>();
    private JTable classTable;
    private JTable gradesTable;
    private DBController dbController;
    private JPanel displayPanel;

    Professor professor;

    public ProffesorUI(Professor professor, DBController dbController) {
        initializeUI();
        this.dbController = dbController;
        this.professor = professor;
        addClassesActionListeners();
        addCurrentDayActionListener();
        addProfileActionListener();
        addClassBookActionListener();
    }

    private void initializeUI() {
        JPanel buttonsPanel = createButtonsPanel();
        displayPanel = new JPanel();
        displayPanel.setBackground(Color.WHITE);

        getjFrame().add(buttonsPanel, BorderLayout.WEST);
        getjFrame().add(displayPanel, BorderLayout.CENTER);
    }

    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(7, 1));
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.add(classes);
        buttonsPanel.add(currentDay);
        buttonsPanel.add(meetings);
        buttonsPanel.add(allActivities);
        buttonsPanel.add(classBook);
        buttonsPanel.add(profile);
        buttonsPanel.add(logOut);
        return buttonsPanel;
    }

    public void addClassesActionListeners() {
        classes.addActionListener(e -> displayCoursesTable());
    }

    public void addCurrentDayActionListener() {
        currentDay.addActionListener(e -> clearPanel());
    }

    public void addProfileActionListener() {
        profile.addActionListener(e -> displayUserData());
    }

    public void addClassBookActionListener() {classBook.addActionListener(e -> {
        try {
            displayClassButtons();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    });}

    public void displayClassButtons() throws SQLException {
        displayPanel.removeAll();
        displayPanel.setLayout(new FlowLayout());


        for(ProfessorActivity professorActivity : professor.getProfessorActivities()){
            JButton button = new JButton(professorActivity.getClassName() + " - " + professorActivity.getType());
            displayPanel.add(button);
            classButtons.add(button);
            createActivityActionListener(button, professorActivity);
        }

        displayPanel.revalidate();
        displayPanel.repaint();
    }


    public void createActivityActionListener(JButton button, ProfessorActivity professorActivity) {
        button.addActionListener(e -> displayClassBook(professorActivity));
    }

    public void displayClassBook(ProfessorActivity professorActivity) {
        displayPanel.removeAll();

        String[] columnNames = {"FirstName", "SecondName", "CNP", "Grade"};
        Object[][] data = convertStudentsToData(professorActivity);


        // Initialize the global classTable here
        gradesTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        });


        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        // Adjust table appearance
        gradesTable.setFont(new Font("Arial", Font.PLAIN, 20));
        gradesTable.setRowHeight(40);
        JTableHeader tableHeader = gradesTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(scrollPane, BorderLayout.CENTER);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private Object[][] convertStudentsToData (ProfessorActivity professorActivity) {
        Object[][] data = new Object[professorActivity.getStudents().size()][4];
        for (int i = 0; i < professorActivity.getStudents().size(); i++) {
            Student student = professorActivity.getStudents().get(i);
            data[i][0] = student.getFirstName();
            data[i][1] = student.getSecondName();
            data[i][2] = student.getCNP();
            data[i][3] = professorActivity.getGrades().get(student);
        }
        return data;
    }

    public void displayUserData() {
        displayPanel.removeAll();

        // Set layout to center the components
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE; // Position components vertically
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;

        // Create JLabel components with larger font
        JLabel firstNameLabel = new JLabel("Prenume: " + professor.firstName);
        JLabel secondNameLabel = new JLabel("Nume: " + professor.secondName);
        JLabel CNPLabel = new JLabel("CNP: " + professor.CNP);
        JLabel emailLabel = new JLabel("Email: " + professor.email);
        JLabel addressLabel = new JLabel("Adresa: " + professor.address);
        JLabel phoneNumberLabel = new JLabel("Numar de telefon: " + professor.phoneNumber);
        JLabel IBANLabel = new JLabel("Iban: " + professor.iban);
        JLabel contractNumberLabel = new JLabel("Numar de contract: " + professor.contractNumber);

        Font largeFont = new Font("Arial", Font.BOLD, 35); // Define larger font
        firstNameLabel.setFont(largeFont);
        secondNameLabel.setFont(largeFont);
        CNPLabel.setFont(largeFont);
        emailLabel.setFont(largeFont);
        addressLabel.setFont(largeFont);
        phoneNumberLabel.setFont(largeFont);
        IBANLabel.setFont(largeFont);
        contractNumberLabel.setFont(largeFont);

        // Add components to the panel with constraints
        displayPanel.add(firstNameLabel, constraints);
        displayPanel.add(secondNameLabel, constraints);
        displayPanel.add(CNPLabel, constraints);
        displayPanel.add(emailLabel, constraints);
        displayPanel.add(addressLabel, constraints);
        displayPanel.add(phoneNumberLabel, constraints);
        displayPanel.add(IBANLabel, constraints);
        displayPanel.add(contractNumberLabel, constraints);
        // Revalidate and repaint panel to reflect changes
        displayPanel.revalidate();
        displayPanel.repaint();
    }



    private void clearPanel() {
        displayPanel.removeAll();
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void displayCoursesTable() {
        // Clear the existing content in the displayPanel
        displayPanel.removeAll();

        createTable(professor.getSubjects());

        // Revalidate and repaint to make sure the table is displayed properly
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void createTable(List<Subject> subjects) {
        String[] columnNames = {"Name", "Lab Weight", "Sem Weight", "Class Weight"};
        Object[][] data = convertSubjectsToData(subjects);

        // Initialize the global classTable here
        classTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        });

        addClassTableEditListener();

        JScrollPane scrollPane = new JScrollPane(classTable);

        // Adjust table appearance
        classTable.setFont(new Font("Arial", Font.PLAIN, 20));
        classTable.setRowHeight(40);
        JTableHeader tableHeader = classTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private Object[][] convertSubjectsToData(List<Subject> subjects) {
        Object[][] data = new Object[subjects.size()][4];
        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);
            data[i][0] = subject.getName();
            data[i][1] = subject.getLabWeight();
            data[i][2] = subject.getSemWeight();
            data[i][3] = subject.getClassWeight();
        }
        return data;
    }

    public void addClassTableEditListener() {
        DefaultTableModel tableModel = (DefaultTableModel) classTable.getModel();

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Get the new value of the cell
                Object newValue = tableModel.getValueAt(row, column);
                Object id = tableModel.getValueAt(row, 0);

                int value;

                try {
                    // Try to convert the value to an int
                    value = Integer.parseInt(newValue.toString());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid input. Please enter a valid integer.");
                }

                // Update the corresponding Subject object
                Subject subject = null;
                for (Subject s : professor.getSubjects()) {
                    if (id.equals(s.getName())) { // Assuming Name is unique and used as an identifier
                        subject = s;
                        break;
                    }
                }
                assert subject != null;
                switch (column) {
                    case 1:
                        subject.setLabWeight(value);
                        dbController.changeLabWeight(subject);
                        break;
                    case 2:
                        subject.setSemWeight(value);
                        dbController.changeSemWeight(subject);
                        break;
                    case 3:
                        subject.setClassWeight(value);
                        dbController.changeClassWeight(subject);
                        break;
                }
            }
        });
    }
}