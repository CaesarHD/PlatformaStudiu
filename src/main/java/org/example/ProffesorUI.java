package org.example;

import org.example.out.production.example.org.example.InvitationCard;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ProffesorUI extends UI {

    private final JButton classes = new JButton("Classes");
    private final JButton meetings = new JButton("Meetings");
    private final JButton allActivities = new JButton("All Activities");
    private final JButton classBook = new JButton("Class Book");
    private final JButton profile = new JButton("Profile");
    private final JButton studentActivities = new JButton("Student Activities");
    private final JButton logOut = new JButton("Log Out");
    private final JButton downloadToFile = new JButton("Download to file");
    private JTable classTable;
    private final List<JButton> classButtons = new ArrayList<>();
    private JTable gradesTable;
    private JPanel displayPanel;

    Professor professor;

    public ProffesorUI(Professor professor) {
        initializeUI();
        this.professor = professor;
        addClassesActionListener();
        addProfileActionListener();
        addClassBookActionListener();
        addAllActivitiesActionListener();
        addMeetingsActionListener();
        addStudentActivitiesActionListener();
        addLogOutActionListener();
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

        classes.setFont(new Font("Helvetica", Font.PLAIN, 30));
        classes.setBackground(Color.lightGray);

        meetings.setFont(new Font("Helvetica", Font.PLAIN, 30));
        meetings.setBackground(Color.lightGray);

        allActivities.setFont(new Font("Helvetica", Font.PLAIN, 30));
        allActivities.setBackground(Color.lightGray);

        classBook.setFont(new Font("Helvetica", Font.PLAIN, 30));
        classBook.setBackground(Color.lightGray);

        profile.setFont(new Font("Helvetica", Font.PLAIN, 30));
        profile.setBackground(Color.lightGray);

        studentActivities.setFont(new Font("Helvetica", Font.PLAIN, 30));
        studentActivities.setBackground(Color.lightGray);

        logOut.setFont(new Font("Helvetica", Font.PLAIN, 30));
        logOut.setBackground(Color.lightGray);

        buttonsPanel.add(classes);
        buttonsPanel.add(meetings);
        buttonsPanel.add(allActivities);
        buttonsPanel.add(classBook);
        buttonsPanel.add(profile);
        buttonsPanel.add(studentActivities);
        buttonsPanel.add(logOut);
        return buttonsPanel;
    }

    public void addLogOutActionListener (){
        JFrame jFrame = new JFrame();
        logOut.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(jFrame,
                    "Are you sure you want to log out?",
                    "Log Out Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                this.getjFrame().dispose();
                new LogInUI();
            }
        });
    }

    public void addStudentActivitiesActionListener() {
        studentActivities.addActionListener(e -> {
            displayStudentActivitiesForProfessor();
        });
    }

    private void displayStudentActivitiesForProfessor() {
        displayPanel.removeAll();
        JPanel studentActivitiesPanel = new JPanel();

        displayPanel.setLayout(new BorderLayout());
        displayPanel.add(getNotificationPanel(studentActivitiesPanel), BorderLayout.NORTH);


        displayStudentsActivityEnrolledForProfessor(professor, studentActivitiesPanel);

        displayPanel.add(studentActivitiesPanel, BorderLayout.CENTER);

        displayPanel.revalidate();
        displayPanel.repaint();

    }

    private static void displayStudentsActivityEnrolledForProfessor(Professor professor, JPanel studentActivitiesPanel) {

        studentActivitiesPanel.setLayout(new BorderLayout());

        try {
            List<Object[]> activities = DBController.fetchActivitiesForProfessor(professor);

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tableModel.addColumn("Activity Name");
            tableModel.addColumn("Date");
            tableModel.addColumn("Hours");
            tableModel.addColumn("Minimum Participants");
            tableModel.addColumn("Current Participants");
            tableModel.addColumn("Expiration Time");
            tableModel.addColumn("Status");

            for (Object[] activity : activities) {
                tableModel.addRow(new Object[]{
                        activity[0],
                        activity[1],
                        activity[2],
                        activity[3],
                        activity[4],
                        activity[5],
                        activity[6]
                });
            }

            JTable table = new JTable(tableModel);
            table.setRowHeight(60);
            table.setFont(new Font("Arial", Font.PLAIN, 18));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));

            table.getColumnModel().getColumn(0).setPreferredWidth(300);
            table.getColumnModel().getColumn(1).setPreferredWidth(200);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(250);
            table.getColumnModel().getColumn(4).setPreferredWidth(250);
            table.getColumnModel().getColumn(5).setPreferredWidth(250);
            table.getColumnModel().getColumn(6).setPreferredWidth(150);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(1000, 500));

            studentActivitiesPanel.add(scrollPane, BorderLayout.CENTER);

            studentActivitiesPanel.revalidate();
            studentActivitiesPanel.repaint();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error fetching activities: " + ex.getMessage());
        }
    }




    private JPanel getNotificationPanel(JPanel studentActivityPanel) {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(displayPanel.getWidth(), 100));


        JButton notificationButton = new JButton("Invitations");
        notificationButton.setFont(new Font("Arial", Font.BOLD, 30));
        notificationButton.setFocusPainted(false);
        notificationButton.setBackground(Color.WHITE);
        notificationButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        notificationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationButton.setToolTipText("View Notifications");

        notificationButton.addActionListener(e -> {
            JFrame notificationFrame = new JFrame("Invitations");
            notificationFrame.setSize(1000, 750);
            notificationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            notificationFrame.setLocationRelativeTo(null);
            notificationFrame.setVisible(true);

            JPanel panel = new JPanel();
            panel.setBackground(Color.LIGHT_GRAY);

            notificationFrame.add(panel);

            displayInvitations(panel, studentActivityPanel);

        });

        topPanel.add(notificationButton, BorderLayout.EAST);
        return topPanel;
    }

        public void displayInvitations(JPanel panel, JPanel studentActivityPanel) {
            try {
                List<Object[]> activities = DBController.getProfessorStudentActivities(professor);
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                for (Object[] activity : activities) {
                    JPanel cardPanel = new InvitationCard(activity, panel, professor, activities, studentActivityPanel);
                    panel.add(cardPanel);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this.getjFrame(), "Error fetching activities: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    public void addClassesActionListener() {
        classes.addActionListener(e -> displayCoursesTable());
    }

    public void addProfileActionListener() {
        profile.addActionListener(e -> displayUserData());
    }

    public void addAllActivitiesActionListener() {
        allActivities.addActionListener(e -> displayActivities());
    }

    public void addClassBookActionListener() {
        classBook.addActionListener(e -> {
            try {
                displayClassButtons();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void addMeetingsActionListener() {
        meetings.addActionListener(e -> displayCalendar());
    }

    public void displayCalendar() {
        displayPanel.removeAll();
        displayPanel.setLayout(new GridLayout(1, 2, 0, 0));

        LocalDateTime date = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        Calendar calendar = new Calendar(date.getYear(), date.getMonthValue(), date, displayPanel, professor);
        MeetingsCalendar meetingsCalendar = new MeetingsCalendar(professor, date, displayPanel);

        displayPanel.add(calendar);
        displayPanel.add(meetingsCalendar);

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void displayClassButtons() throws SQLException {
        displayPanel.removeAll();
        displayPanel.setLayout(new BorderLayout());

        JPanel classBookPanel = new JPanel();
        classBookPanel.setLayout(new BoxLayout(classBookPanel, BoxLayout.Y_AXIS));
        classBookPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        for (ProfessorActivity professorActivity : professor.getProfessorActivities()) {
            JButton button = getJButton(professorActivity);

            classBookPanel.add(button);
            classBookPanel.add(Box.createVerticalStrut(10));
            classButtons.add(button);
            createActivityActionListener(button, professorActivity);
        }

        JScrollPane scrollPane = new JScrollPane(classBookPanel);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private static JButton getJButton(ProfessorActivity professorActivity) {
        JButton button = new JButton(professorActivity.getClassName() + " - " + professorActivity.getType());
        button.setBackground(Color.LIGHT_GRAY);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(300, 60));

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 10, 20, 10),
                BorderFactory.createMatteBorder(0, 0, 0, 0, Color.CYAN)
        ));
        return button;
    }


    public void createActivityActionListener(JButton button, ProfessorActivity professorActivity) {
        button.addActionListener(e -> displayClassBook(professorActivity));
    }

    public void displayClassBook(ProfessorActivity professorActivity) {
        displayPanel.removeAll();

        displayPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        downloadToFile.setPreferredSize(new Dimension(300, 70));
        downloadToFile.setFont(new Font("Arial", Font.BOLD, 20));
        downloadToFile.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        headerPanel.add(downloadToFile);

        displayPanel.add(headerPanel, BorderLayout.SOUTH);

        String[] columnNames = {"FirstName", "SecondName", "CNP", "Grade"};
        Object[][] data = convertStudentsToData(professorActivity);

        gradesTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        });

        addGradesTableEditListener(professorActivity);

        JScrollPane scrollPane = new JScrollPane(gradesTable);

        gradesTable.setFont(new Font("Arial", Font.PLAIN, 20));
        gradesTable.setRowHeight(40);
        JTableHeader tableHeader = gradesTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        displayPanel.add(scrollPane, BorderLayout.CENTER);

        addDownloadToFileButtonListener(gradesTable, "gradesTable.txt");
        
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void addDownloadToFileButtonListener(JTable table, String fileName) {
        downloadToFile.addActionListener(e -> saveTableToTextFile(table));
    }

    public static void saveTableToTextFile(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Table Data");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        int userChoice = fileChooser.showSaveDialog(null);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            writeTableToFile(table, selectedFile);
        }
    }

    public static void saveMeetingsToTextFile(List<Meeting> meetings) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Meetings");

        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Meetings Files", "txt"));

        int userChoice = fileChooser.showSaveDialog(null);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            writeMeetingsToFile(meetings, selectedFile);
        }
    }

    public static void writeMeetingsToFile(List<Meeting> meetings, File file) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Meeting meeting : meetings) {
                writer.write("ClassName: " + meeting.getClassName());
                writer.newLine();
                writer.write("Type: " + meeting.getType());
                writer.newLine();
                writer.write("MaxNb: " + meeting.getMaxNb());
                writer.newLine();
                writer.write("CrtNb: " + meeting.getCrtNb());
                writer.newLine();
                writer.write("Description: " + (meeting.getDescription() != null ? meeting.getDescription() : "N/A"));
                writer.newLine();
                writer.write("StartDate: " + meeting.getStartDate().format(formatter));
                writer.newLine();
                writer.write("EndDate: " + meeting.getEndDate().format(formatter));
                writer.newLine();
                writer.newLine();
            }
        }
    }



    public static void writeTableToFile(JTable table, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            TableModel model = table.getModel();
            int rowCount = model.getRowCount();
            int columnCount = model.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                writer.append(model.getColumnName(i));
                if (i < columnCount - 1) writer.append("\t");
            }
            writer.append('\n');

            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    writer.append(model.getValueAt(row, col).toString());
                    if (col < columnCount - 1) writer.append("\t");
                }
                writer.append('\n');
            }

            JOptionPane.showMessageDialog(null, "Data saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Object[][] convertStudentsToData(ProfessorActivity professorActivity) {
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

                Object newValue = tableModel.getValueAt(row, column);
                Object CNP = tableModel.getValueAt(row, 2);

                int value;

                try {
                    value = Integer.parseInt(newValue.toString());
                    Student student = null;
                    for (Student s : professorActivity.getStudents()) {
                        if (s.getCNP().equals(CNP)) {
                            student = s;
                            break;
                        }
                    }
                    professorActivity.getGrades().put(student, value);
                    DBController.changeGrades(professorActivity, student);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid input. Please enter a valid integer.");
                }

            }
        });
    }

    public void displayUserData() {
        displayPanel.removeAll();

        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;

        JLabel firstNameLabel = new JLabel("Prenume: " + professor.firstName);
        JLabel secondNameLabel = new JLabel("Nume: " + professor.secondName);
        JLabel CNPLabel = new JLabel("CNP: " + professor.CNP);
        JLabel emailLabel = new JLabel("Email: " + professor.email);
        JLabel addressLabel = new JLabel("Adresa: " + professor.address);
        JLabel phoneNumberLabel = new JLabel("Numar de telefon: " + professor.phoneNumber);
        JLabel IBANLabel = new JLabel("Iban: " + professor.iban);
        JLabel contractNumberLabel = new JLabel("Numar de contract: " + professor.contractNumber);

        Font largeFont = new Font("Arial", Font.BOLD, 35);
        firstNameLabel.setFont(largeFont);
        secondNameLabel.setFont(largeFont);
        CNPLabel.setFont(largeFont);
        emailLabel.setFont(largeFont);
        addressLabel.setFont(largeFont);
        phoneNumberLabel.setFont(largeFont);
        IBANLabel.setFont(largeFont);
        contractNumberLabel.setFont(largeFont);

        displayPanel.add(firstNameLabel, constraints);
        displayPanel.add(secondNameLabel, constraints);
        displayPanel.add(CNPLabel, constraints);
        displayPanel.add(emailLabel, constraints);
        displayPanel.add(addressLabel, constraints);
        displayPanel.add(phoneNumberLabel, constraints);
        displayPanel.add(IBANLabel, constraints);
        displayPanel.add(contractNumberLabel, constraints);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void clearPanel() {
        displayPanel.removeAll();
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void displayCoursesTable() {
        displayPanel.removeAll();

        createTable(professor.getSubjects());

        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void createTable(List<Subject> subjects) {
        String[] columnNames = {"Name", "Lab Weight", "Sem Weight", "Class Weight"};
        Object[][] data = convertSubjectsToData(subjects);

        classTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        });

        addClassTableEditListener();

        JScrollPane scrollPane = new JScrollPane(classTable);

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

                Object newValue = tableModel.getValueAt(row, column);
                Object id = tableModel.getValueAt(row, 0);

                int value;

                try {
                    value = Integer.parseInt(newValue.toString());

                    Subject subject = null;
                    for (Subject s : professor.getSubjects()) {
                        if (id.equals(s.getName())) {
                            subject = s;
                            break;
                        }
                    }
                    assert subject != null;
                    switch (column) {
                        case 1:
                            subject.setLabWeight(value);
                            DBController.changeLabWeight(subject);
                            break;
                        case 2:
                            subject.setSemWeight(value);
                            DBController.changeSemWeight(subject);
                            break;
                        case 3:
                            subject.setClassWeight(value);
                            DBController.changeClassWeight(subject);
                            break;
                    }
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Invalid input. Please enter a valid integer.");
                }
            }
        });
    }

    public void displayActivities() {
        displayPanel.removeAll();

        displayPanel.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        downloadToFile.setPreferredSize(new Dimension(300, 70));
        downloadToFile.setFont(new Font("Arial", Font.BOLD, 20));
        downloadToFile.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        headerPanel.add(downloadToFile);

        displayPanel.add(headerPanel, BorderLayout.SOUTH);

        String[] columnNames = {"Activity name", "Activity type", "Description", "Max number of students"};
        Object[][] data = convertActivityToData(professor.getProfessorActivities());

        JTable activitiesTable = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JScrollPane scrollPane = new JScrollPane(activitiesTable);

        activitiesTable.setFont(new Font("Arial", Font.PLAIN, 20));
        activitiesTable.setRowHeight(40);
        JTableHeader tableHeader = activitiesTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        displayPanel.add(scrollPane, BorderLayout.CENTER);

        addDownloadToFileButtonListener(activitiesTable, "gradesTable.txt");

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