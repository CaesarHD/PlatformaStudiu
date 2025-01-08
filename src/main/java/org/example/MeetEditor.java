package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class MeetEditor extends JPanel {
    public MeetEditor(Meeting meet, MeetingCard meetingCard, JPanel parent, Professor professor, JPanel cardList) {

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

        List<ProfessorActivity> options = new ArrayList<>(professor.getProfessorActivities());

        JComboBox<ProfessorActivity> titleComboBox = new JComboBox<>();
        for(ProfessorActivity professorActivity : options){
            titleComboBox.addItem(professorActivity);
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

        JLabel l6 = new JLabel("Descriere");
        l6.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l6.setHorizontalAlignment(JLabel.CENTER);
        center.add(l6);

        JTextField description = new JTextField();
        description.setFont(new Font("Helvectica", Font.PLAIN, 20));
        description.setHorizontalAlignment(JLabel.CENTER);
        center.add(description);

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
        description.setText(meet.getDescription());

        if (meet.getClassName() != null) {

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
                                    ProfessorActivity selectedActivity = (ProfessorActivity)titleComboBox.getSelectedItem();
                                    meet.setClassName(selectedActivity.getClassName());
                                    meet.setClassId(options.get(titleComboBox.getItemCount() - 1).getId());
                                    meet.setProfessorActivityId(selectedActivity.getId());
                                    meet.setDescription(description.getText());

                                    DBController.updateMeeting(meet);

                                    SwingUtilities.invokeLater(() -> {
                                        meetingCard.getTitle().setText(selectedActivity.getClassName());
                                        meetingCard.getStartTime().setText("Incepe la: " + startTime.getText());
                                        meetingCard.getEndTime().setText("Se termina la: " + endTime.getText());
                                        meetingCard.getCrtStudentsNb().setText(meet.getCrtNb() + "/" + professor.getProfessorActivityById(meet.getProfessorActivityId()).getMaxNb() + " participanti");
                                        meetingCard.getDescription().setText(meet.getDescription());
                                        meetingCard.getType().setText(selectedActivity.getType());
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
                    cardList.remove(meetingCard);
                    SwingUtilities.invokeLater(() -> {
                        parent.revalidate();
                        parent.repaint();
                        frame.dispose();
                    });
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
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
                                    ProfessorActivity selectedActivity = (ProfessorActivity)titleComboBox.getSelectedItem();
                                    meet.setEndDate(parsedDate);
                                    meet.setClassName(selectedActivity.getClassName());
                                    meet.setClassId(options.get(titleComboBox.getItemCount() - 1).getId());
                                    meet.setDescription(description.getText());
                                    meet.setProfessorActivityId(selectedActivity.getId());

                                    DBController.createNewMeeting(professor, meet);

                                    SwingUtilities.invokeLater(() -> {
                                        meetingCard.getTitle().setText(selectedActivity.getClassName());
                                        meetingCard.getStartTime().setText("Incepe la: " + startTime.getText());
                                        meetingCard.getEndTime().setText("Se termina la: " + endTime.getText());
                                        meetingCard.getCrtStudentsNb().setText(meet.getCrtNb() + "/" + professor.getProfessorActivityById(meet.getProfessorActivityId()).getMaxNb() + " participanti");
                                        meetingCard.getType().setText(selectedActivity.getType());
                                        meetingCard.getDescription().setText(meet.getDescription());

//                                        parent.removeAll();
//                                        parent.add(new Calendar(year, month, meet.getStartDate(), mainPanel, professor));
//                                        parent.add(new MeetingsCalendar(professor, meet.getStartDate(), mainPanel));
                                        cardList.add(meetingCard);
                                        professor.getMeetings().add(meet);

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
                } catch (Exception ex ) {
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
