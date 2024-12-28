package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class MeetingCard extends JPanel {
    private final JLabel title;
    private final JLabel startTime;
    private final JLabel endTime;
    private final JLabel type;
    private final JLabel data;
    private final JLabel maxStudents;
    private final JLabel crtStudentsNb;

    public MeetingCard(Meeting meeting, JPanel mainPanel, Professor professor) {
        setLayout(new GridLayout(7, 1));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                BorderFactory.createMatteBorder(0, 10, 0, 0, Color.CYAN)
        ));
        this.setBackground(Color.lightGray);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        MeetingCard self = this;

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new MeetEditor(meeting, self, mainPanel, professor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        title = new JLabel(meeting.getClassName());
        title.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        title.setFont(new Font("Helvetica", Font.BOLD, 44));
        title.setForeground(Color.BLACK);
        this.add(title);

        type = new JLabel(meeting.getType());
        type.setBorder(BorderFactory.createEmptyBorder(5, 15, 4, 15));
        type.setFont(new Font("Helvetica", Font.PLAIN, 35));
        type.setForeground(Color.darkGray);
        this.add(type);

        startTime = new JLabel("Incepe la: " + meeting.getStartDate().toLocalTime().toString());
        startTime.setBorder(BorderFactory.createEmptyBorder(2, 15, 1, 5));
        startTime.setFont(new Font("Helvetica", Font.PLAIN, 28));
        startTime.setForeground(Color.darkGray);
        this.add(startTime);

        endTime = new JLabel("Se termina la: " + meeting.getEndDate().toLocalTime().toString());
        endTime.setBorder(BorderFactory.createEmptyBorder(2, 15, 1, 5));
        endTime.setFont(new Font("Helvetica", Font.PLAIN, 28));
        endTime.setForeground(Color.darkGray);
        this.add(endTime);

        data = new JLabel("In data de: " + meeting.getStartDate().toLocalDate().toString());
        data.setBorder(BorderFactory.createEmptyBorder(2, 15, 1, 5));
        data.setFont(new Font("Helvetica", Font.PLAIN, 28));
        data.setForeground(Color.darkGray);
        this.add(data);

        maxStudents = new JLabel("Nr maxim studenti: " + meeting.getMaxNb());
        maxStudents.setBorder(BorderFactory.createEmptyBorder(2, 15, 1, 5));
        maxStudents.setFont(new Font("Helvetica", Font.PLAIN, 28));
        maxStudents.setForeground(Color.darkGray);
        this.add(maxStudents);

        crtStudentsNb = new JLabel(meeting.getCrtNb() + "/" + meeting.getMaxNb() + " participanti");
        crtStudentsNb.setBorder(BorderFactory.createEmptyBorder(2, 15, 1, 5));
        crtStudentsNb.setFont(new Font("Helvetica", Font.PLAIN, 28));
        crtStudentsNb.setForeground(Color.darkGray);
        this.add(crtStudentsNb);


    }


    public JLabel getTitle() {
        return title;
    }

    public JLabel getEndTime() {
        return endTime;
    }

    public JLabel getStartTime() {
        return startTime;
    }

    public JLabel getType() {
        return type;
    }

    public JLabel getData() {
        return data;
    }

    public JLabel getMaxStudents() {
        return maxStudents;
    }

    public JLabel getCrtStudentsNb() {
        return crtStudentsNb;
    }
}
