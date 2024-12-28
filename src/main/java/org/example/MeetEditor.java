package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MeetEditor extends JPanel {
    public MeetEditor(Meeting meet, MeetingCard meetingCard, JPanel parent, Professor professor) {

        int year = meet.getStartDate().getYear();
        int month = meet.getStartDate().getMonthValue();

        JFrame frame = new JFrame("Calendar");
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.white);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        mainPanel.setBackground(Color.white);

        JPanel center = new JPanel(new GridLayout(5, 2, 20, 20));
        center.setBackground(Color.white);

        JLabel l1 = new JLabel("Title");
        l1.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l1.setHorizontalAlignment(JLabel.CENTER);
        center.add(l1);

        List<Subject> options = new ArrayList<>(professor.getSubjects());

        JComboBox<String> titleComboBox = new JComboBox<>();
        for(Subject subject : options){
            titleComboBox.addItem(subject.getName());
        }
        titleComboBox.setFont(new Font("Helvetica", Font.PLAIN, 20));
        titleComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        center.add(titleComboBox);

        JLabel l2 = new JLabel("Type");
        l2.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l2.setHorizontalAlignment(JLabel.CENTER);
        center.add(l2);

        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.addItem("curs");
        typeComboBox.addItem("laborator");
        typeComboBox.addItem("seminar");
        typeComboBox.setFont(new Font("Helvetica", Font.PLAIN, 20));
        typeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        center.add(typeComboBox);

        JLabel l3 = new JLabel("Start Time");
        l3.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l3.setHorizontalAlignment(JLabel.CENTER);
        center.add(l3);

        JTextField startTime = new JTextField();
        startTime.setFont(new Font("Helvectica", Font.PLAIN, 20));
        startTime.setHorizontalAlignment(JLabel.CENTER);
        center.add(startTime);

        JLabel l4 = new JLabel("End Time");
        l4.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l4.setHorizontalAlignment(JLabel.CENTER);
        center.add(l4);

        JTextField endTime = new JTextField();
        endTime.setFont(new Font("Helvectica", Font.PLAIN, 20));
        endTime.setHorizontalAlignment(JLabel.CENTER);
        center.add(endTime);

        JLabel l5 = new JLabel("Nr max students");
        l5.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l5.setHorizontalAlignment(JLabel.CENTER);
        center.add(l5);

        JTextField maxStudents = new JTextField();
        maxStudents.setFont(new Font("Helvectica", Font.PLAIN, 20));
        maxStudents.setHorizontalAlignment(JLabel.CENTER);
        center.add(maxStudents);

        mainPanel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2, 20, 20));
        bottom.setBackground(null);

        JButton delete = new JButton("Delete");
        delete.setFont(new Font("Helvectica", Font.PLAIN, 20));
        delete.setBackground(Color.red);
        delete.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottom.add(delete);

        JButton save = new JButton("Save");
        save.setFont(new Font("Helvectica", Font.PLAIN, 20));
        save.setBackground(Color.green);
        save.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottom.add(save);

        startTime.setText(meet.getStartDate().toLocalTime().toString());
        endTime.setText(meet.getEndDate().toLocalTime().toString());
        maxStudents.setText("" + meet.getMaxNb());

        if (meet.getClassName() != null) {
//            save.setEnabled(true);

            save.addActionListener(e -> {
                LocalDateTime parsedDate;
                try {
                    parsedDate = LocalDateTime.parse(meet.getStartDate().toLocalDate() + "T" + startTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    try {
                        if (parsedDate.isBefore(LocalDateTime.now())) {
                            throw new IllegalArgumentException("The meet must be in the future!");
                        }
                        else{
                            meet.setStartDate(parsedDate);
                            try {
                                parsedDate = LocalDateTime.parse(meet.getEndDate().toLocalDate() + "T" + endTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (Exception ex) {
                                System.err.printf("Could not parse Date: %s", ex);
                                JOptionPane.showMessageDialog(null, "Time format not valid. Expected: hh:mm");
                            }

                            try {
                                if (parsedDate.isBefore(meet.getStartDate()) || parsedDate.isEqual(meet.getStartDate())) {
                                    throw new IllegalArgumentException("The end date must be after the start date!");
                                }
                                else{
                                    meet.setEndDate(parsedDate);
                                    meet.setType((String)typeComboBox.getSelectedItem());
                                    meet.setClassName((String)titleComboBox.getSelectedItem());
                                    meet.setClassId(options.get(titleComboBox.getItemCount() - 1).getId());

                                    meet.setMaxNb(Integer.parseInt(maxStudents.getText()));

                                    DBController.updateMeeting(meet);

                                    SwingUtilities.invokeLater(() -> {
                                        meetingCard.getTitle().setText((String) titleComboBox.getSelectedItem());
                                        meetingCard.getType().setText((String) typeComboBox.getSelectedItem());
                                        meetingCard.getStartTime().setText("Incepe la: " + startTime.getText());
                                        meetingCard.getEndTime().setText("Se termina la: " + endTime.getText());
                                        meetingCard.getMaxStudents().setText("Nr maxim studenti: " + maxStudents.getText());
                                        meetingCard.getCrtStudentsNb().setText(meet.getCrtNb() + "/" + meet.getMaxNb() + " participanti");
                                        meetingCard.revalidate();
                                        meetingCard.repaint();

                                        parent.revalidate();
                                        parent.repaint();
                                        frame.dispose();
                                    });
                                }
                            } catch (IllegalArgumentException ex) {
                                System.err.printf("The end date must be after the start date!: %s", ex);
                                JOptionPane.showMessageDialog(null, ex.getMessage());
                            }
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                } catch (Exception ex) {
                    System.err.printf("Could not parse Date: %s", ex);
                    JOptionPane.showMessageDialog(null, "Time format not valid. Expected: hh:mm");
                }

            });


            delete.addActionListener(e -> {
                try {
                    DBController.deleteMeeting(meet, professor);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                SwingUtilities.invokeLater(frame::dispose);
            });
        } else {
            delete.setVisible(false);
            save.addActionListener(e -> {
                LocalDateTime parsedDate;
                try {
                    parsedDate = LocalDateTime.parse(meet.getStartDate().toLocalDate() + "T" + startTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    try {
                        if (parsedDate.isBefore(LocalDateTime.now())) {
                            throw new IllegalArgumentException("The meet must be in the future!");
                        }
                        else{
                            meet.setStartDate(parsedDate);
                            try {
                                parsedDate = LocalDateTime.parse(meet.getEndDate().toLocalDate() + "T" + endTime.getText(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                            } catch (Exception ex) {
                                System.err.printf("Could not parse Date: %s", ex);
                                JOptionPane.showMessageDialog(null, "Time format not valid. Expected: hh:mm");
                            }

                            try {
                                if (parsedDate.isBefore(meet.getStartDate()) || parsedDate.isEqual(meet.getStartDate())) {
                                    throw new IllegalArgumentException("The end date must be after the start date!");
                                }
                                else{
                                    meet.setEndDate(parsedDate);
                                    meet.setType((String)typeComboBox.getSelectedItem());
                                    meet.setClassName((String)titleComboBox.getSelectedItem());
                                    meet.setClassId(options.get(titleComboBox.getItemCount() - 1).getId());

                                    meet.setMaxNb(Integer.parseInt(maxStudents.getText()));

                                    DBController.createNewMeeting(professor, meet);

                                    SwingUtilities.invokeLater(() -> {
                                        meetingCard.getTitle().setText((String) titleComboBox.getSelectedItem());
                                        meetingCard.getType().setText((String) typeComboBox.getSelectedItem());
                                        meetingCard.getStartTime().setText("Incepe la: " + startTime.getText());
                                        meetingCard.getEndTime().setText("Se termina la: " + endTime.getText());
                                        meetingCard.getMaxStudents().setText("Nr maxim studenti: " + maxStudents.getText());
                                        meetingCard.getCrtStudentsNb().setText(meet.getCrtNb() + "/" + meet.getMaxNb() + " participanti");
                                        meetingCard.revalidate();
                                        meetingCard.repaint();

                                        parent.removeAll();
                                        parent.add(new Calendar(year, month, meet.getStartDate(), mainPanel, professor));
                                        parent.add(new MeetingsCalendar(professor, meet.getStartDate(), mainPanel));
                                        parent.revalidate();
                                        parent.repaint();
                                        frame.dispose();
                                    });
                                }
                            } catch (IllegalArgumentException ex) {
                                System.err.printf("The end date must be after the start date!: %s", ex);
                                JOptionPane.showMessageDialog(null, ex.getMessage());
                            }
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(null, ex.getMessage());
                    }
                } catch (Exception ex) {
                    System.err.printf("Could not parse Date: %s", ex);
                    JOptionPane.showMessageDialog(null, "Time format not valid. Expected: hh:mm");
                }
            });
        }

        mainPanel.add(bottom, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        frame.setVisible(true);
    }
}
