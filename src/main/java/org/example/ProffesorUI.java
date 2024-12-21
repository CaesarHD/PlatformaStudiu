package org.example;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class ProffesorUI extends UI {

    private final JButton classes = new JButton("Classes");
    private final JButton currentDay = new JButton("Current Day");
    private final JButton meetings = new JButton("Meetings");
    private final JButton allActivities = new JButton("All Activities");
    private final JButton classBook = new JButton("Class Book");
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menu = new JMenu("Menu");
    private JTable classTable;

    private JPanel displayPanel;

    Professor professor;

    public ProffesorUI(Professor professor) {
        initializeUI();
        this.professor = professor;
        addClassesActionListeners();
        addCurrentDayActionListener();
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
        buttonsPanel.setLayout(new GridLayout(5, 1));
        buttonsPanel.setBackground(Color.LIGHT_GRAY);
        buttonsPanel.add(classes);
        buttonsPanel.add(currentDay);
        buttonsPanel.add(meetings);
        buttonsPanel.add(allActivities);
        buttonsPanel.add(classBook);
        return buttonsPanel;
    }

    public void addClassesActionListeners() {
        classes.addActionListener(e -> displayCoursesTable());
    }

    public void addCurrentDayActionListener() {
        currentDay.addActionListener(e -> clearPanel());
    }

    private void clearPanel () {
        displayPanel.removeAll();
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    private void displayCoursesTable() {
        // Clear the existing content in the displayPanel
        displayPanel.removeAll();

        // Create the table with the professor's subjects and add it to the displayPanel
        createTable(professor.getSubjects());

        // Revalidate and repaint to make sure the table is displayed properly
        displayPanel.revalidate();
        displayPanel.repaint();
    }


    public void createTable(List<Subject> subjects) {
        String[] columnNames = {"Name", "Lab Weight", "Sem Weight", "Class Weight"};
        Object[][] data = convertSubjectsToData(subjects);
        JScrollPane scrollPane = createTableScrollPane(data, columnNames);

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

    private JScrollPane createTableScrollPane(Object[][] data, String[] columnNames) {
        JTable table = createTable(data, columnNames);
        adjustColumnWidths(table);
        return new JScrollPane(table);
    }

    private JTable createTable(Object[][] data, String[] columnNames) {
        JTable table = new JTable(new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        });

        // Set font for the table and header
        Font tableFont = new Font("Arial", Font.PLAIN, 20);
        table.setFont(tableFont);

        // Set row height to make cells bigger
        table.setRowHeight(40); // Adjust this value to increase or decrease the cell height

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 30));

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setFillsViewportHeight(true); // Ensure the table fills the available height
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Single row selection

        return table;
    }

    private void adjustColumnWidths(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(getPreferredWidth(table, i));
        }
    }

    private int getPreferredWidth(JTable table, int columnIndex) {
        int width = 0;
        for (int row = 0; row < table.getRowCount(); row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, columnIndex);
            Component comp = table.prepareRenderer(renderer, row, columnIndex);
            width = Math.max(comp.getPreferredSize().width, width);
        }
        return width + 10;
    }

    public static void addTableEditListener(JTable table) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Obține valoarea nouă a celulei
                Object newValue = tableModel.getValueAt(row, column);

                try {
                    // Încercăm să converim valoarea la int
                    int value = Integer.parseInt(newValue.toString());
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Valoarea introdusă nu este un număr întreg valid.");
                }

                // Aici poți adăuga logica ta pentru a reacționa la modificarea celulei
                // De exemplu, salvezi schimbarea într-o bază de date sau într-o variabilă


            }
        });

    }
}
