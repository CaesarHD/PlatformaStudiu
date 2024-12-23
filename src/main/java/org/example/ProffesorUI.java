package org.example;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.xml.validation.Validator;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProffesorUI extends UI {

    private final JButton classes = new JButton("Classes");
    private final JButton currentDay = new JButton("Current Day");
    private final JButton meetings = new JButton("Meetings");
    private final JButton allActivities = new JButton("All Activities");
    private final JButton classBook = new JButton("Class Book");
    private final JButton profile = new JButton("Profile");
    private final JButton logOut = new JButton("Log Out");
    private final JButton downloadToFile = new JButton("Download to file");
    private JTable classTable;
    private JTable activitiesTable;
    private final List<JButton> classButtons = new ArrayList<>();
    private JTable gradesTable;
    private DBController dbController;
    private JPanel displayPanel;
    private Calendar calendar;
    private MeetingsCalendar meetingsCalendar;

    Professor professor;

    public ProffesorUI(Professor professor, DBController dbController) {
        initializeUI();
        this.dbController = dbController;
        this.professor = professor;
        addClassesActionListeners();
        addCurrentDayActionListener();
        addProfileActionListener();
        addClassBookActionListener();
        addAllActivitiesActionListener();
        addMeetingsActionListener();
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

    public void addAllActivitiesActionListener () {
        allActivities.addActionListener(e -> displayActivities());
    }

    public void addClassBookActionListener() {classBook.addActionListener(e -> {
        try {
            displayClassButtons();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    });}

    public void addMeetingsActionListener () {
        meetings.addActionListener(e -> displayCalendar());
    }

    public void displayCalendar() {
        displayPanel.removeAll();
        displayPanel.setLayout(new GridLayout(1, 2, 0, 0));

        LocalDate date = LocalDate.now();

        calendar = new Calendar(date.getYear(), date.getMonthValue(), date, displayPanel);
        meetingsCalendar = new MeetingsCalendar();

        displayPanel.add(calendar);
        displayPanel.add(meetingsCalendar);

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void displayClassButtons() throws SQLException {
        displayPanel.removeAll();
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));

        for(ProfessorActivity professorActivity : professor.getProfessorActivities()){
            JButton button = new JButton(professorActivity.getClassName() + " - " + professorActivity.getType());
            button.setFont(new Font("Arial", Font.BOLD, 20));
            displayPanel.add(button);
            displayPanel.add(Box.createVerticalStrut(10));
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
        // Clear the panel
        displayPanel.removeAll();

        // Use BorderLayout for the main panel
        displayPanel.setLayout(new BorderLayout());

        // Create a panel for the header with vertical alignment
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // Set equal spacing around the button using EmptyBorder
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Top, Left, Bottom, Right padding

        // Configure the button
        downloadToFile.setPreferredSize(new Dimension(300, 70)); // Button size
        downloadToFile.setFont(new Font("Arial", Font.BOLD, 20)); // Slightly larger font
        downloadToFile.setAlignmentX(JComponent.CENTER_ALIGNMENT); // Center horizontally

        // Add the button to the header panel
        headerPanel.add(downloadToFile);

        // Add the header panel to the top of the display panel
        displayPanel.add(headerPanel, BorderLayout.SOUTH);

        // Define column names and data for the table
        String[] columnNames = {"FirstName", "SecondName", "CNP", "Grade"};
        Object[][] data = convertStudentsToData(professorActivity);

        // Create and configure the table
        gradesTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only "Grade" column is editable
            }
        });

        addGradesTableEditListener(professorActivity);

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(gradesTable);

        // Adjust table appearance
        gradesTable.setFont(new Font("Arial", Font.PLAIN, 20));
        gradesTable.setRowHeight(40);
        JTableHeader tableHeader = gradesTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        // Add the table (inside its scroll pane) to the center of the display panel
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        addDownloadToFileButtonListener(gradesTable, "gradesTable.txt");

        // Refresh the panel
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void addDownloadToFileButtonListener(JTable table, String fileName) {
        downloadToFile.addActionListener(e -> saveTableToTextFile(table));
    }

    public static void saveTableToTextFile(JTable table) {
        // Show the file save dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Table Data");

        // Set file filter to only allow .txt files
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        int userChoice = fileChooser.showSaveDialog(null);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            File selectedFile = fileChooser.getSelectedFile();

            // If the user didn't add a file extension, append .txt
            if (!selectedFile.getName().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            // Save the JTable to the selected file
            writeTableToFile(table, selectedFile);
        }
    }

    // Function to write JTable data to the given file
    public static void writeTableToFile(JTable table, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            TableModel model = table.getModel();
            int rowCount = model.getRowCount();
            int columnCount = model.getColumnCount();

            // Write column names
            for (int i = 0; i < columnCount; i++) {
                writer.append(model.getColumnName(i));
                if (i < columnCount - 1) writer.append("\t"); // tab separator
            }
            writer.append('\n');

            // Write data rows
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    writer.append(model.getValueAt(row, col).toString());
                    if (col < columnCount - 1) writer.append("\t"); // tab separator
                }
                writer.append('\n');
            }

            JOptionPane.showMessageDialog(null, "Data saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void addGradesTableEditListener(ProfessorActivity professorActivity) {
        DefaultTableModel tableModel = (DefaultTableModel) gradesTable.getModel();

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Get the new value of the cell
                Object newValue = tableModel.getValueAt(row, column);
                Object CNP = tableModel.getValueAt(row, 2);

                Integer value = null;

                try {
                    // Try to convert the value to an int
                    value = Integer.parseInt(newValue.toString());
                    Student student = null;
                    for (Student s : professorActivity.getStudents()) {
                        if (s.getCNP().equals(CNP)) { // Assuming Name is unique and used as an identifier
                            student = s;
                            break;
                        }
                    }
                    professorActivity.getGrades().put(student, value);
                    dbController.changeGrades(professorActivity, student);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid input. Please enter a valid integer.");
                }

            }
        });
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
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid input. Please enter a valid integer.");
                }

                // Update the corresponding Subject object

            }
        });
    }

    public void displayActivities() {
        displayPanel.removeAll();

        // Use BorderLayout for the main panel
        displayPanel.setLayout(new BorderLayout());

        // Create a panel for the header with vertical alignment
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // Set equal spacing around the button using EmptyBorder
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // Top, Left, Bottom, Right padding

        // Configure the button
        downloadToFile.setPreferredSize(new Dimension(300, 70)); // Button size
        downloadToFile.setFont(new Font("Arial", Font.BOLD, 20)); // Slightly larger font
        downloadToFile.setAlignmentX(JComponent.CENTER_ALIGNMENT); // Center horizontally

        // Add the button to the header panel
        headerPanel.add(downloadToFile);

        // Add the header panel to the top of the display panel
        displayPanel.add(headerPanel, BorderLayout.SOUTH);

        // Define column names and data for the table
        String[] columnNames = {"Activity name", "Activity type", "Description", "Max number of students"};
        Object[][] data = convertActivityToData(professor.getProfessorActivities());

        // Create and configure the table
        activitiesTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        // Wrap the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(activitiesTable);

        // Adjust table appearance
        activitiesTable.setFont(new Font("Arial", Font.PLAIN, 20));
        activitiesTable.setRowHeight(40);
        JTableHeader tableHeader = activitiesTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        // Add the table (inside its scroll pane) to the center of the display panel
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        addDownloadToFileButtonListener(activitiesTable, "gradesTable.txt");

        // Refresh the panel
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public static Object[][] convertActivityToData(List<ProfessorActivity> professorActivities) {
        Object[][] data = new Object[professorActivities.size()][4];
        for (int i = 0; i < professorActivities.size(); i++) {
            ProfessorActivity professorActivity = professorActivities.get(i);
            data[i][0] = professorActivity.getClassName();
            data[i][1] = professorActivity.getType();
            data[i][2] = professorActivity.getDescription();
            data[i][3] = professorActivity.getMaxNb();
        }
        return data;
    }
}