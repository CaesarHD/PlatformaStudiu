package org.example;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.util.List;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static javax.swing.text.StyleConstants.setBackground;

public class MeetingsCalendar extends JPanel {
    public MeetingsCalendar(List<ProfessorActivity> meetings) {


        setLayout(new BorderLayout(20, 20));
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));

        int rows = meetings.size();

        JPanel list = new JPanel(new GridLayout(4, 2, 10, 10));
        list.setBackground(Color.white);

        JScrollPane sp = new JScrollPane(list);

        for (int i = 0; i < meetings.size(); i ++){
            JPanel meeting = new JPanel(new GridLayout(2, 1));
            meeting.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createMatteBorder(0, 10, 0, 0, Color.CYAN)
            ));
            meeting.setBackground(Color.lightGray);
            meeting.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel title = new JLabel(meetings.get(i).getClassName() + " " + meetings.get(i).getType());
            title.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
            title.setFont(new Font("Helvetica", Font.BOLD, 44));
            title.setForeground(Color.BLACK);
            meeting.add(title);

            JLabel time = new JLabel(meetings.get(i).getStartDate().toString());
            time.setBorder(BorderFactory.createEmptyBorder(5, 15, 4, 15));
            time.setFont(new Font("Helvetica", Font.PLAIN, 28));
            time.setForeground(Color.darkGray);
            meeting.add(time);


            list.add(meeting);
        }
        add(sp, BorderLayout.CENTER);

        JButton newMeeting = new JButton("New");
        newMeeting.setFont(new Font("Helvetica", Font.PLAIN, 20));
        newMeeting.setBackground(Color.red);
        newMeeting.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(newMeeting, BorderLayout.SOUTH);


    }
}
