package org.example;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingsCalendar extends JPanel {
    public MeetingsCalendar(Professor professor, LocalDateTime date, JPanel mainPanel) {

        List<Meeting> crtMeetings = getMeetings(professor.getMeetings(), date);

        setLayout(new BorderLayout(20, 20));
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));

        JPanel list = new JPanel(new GridLayout(4, 2, 10, 10));
        list.setBackground(Color.white);

        JScrollPane scrollPane = new JScrollPane(list);

        for (Meeting m : crtMeetings) {
            MeetingCard meeting = new MeetingCard(m, mainPanel, professor);
            list.add(meeting);
        }
        add(scrollPane, BorderLayout.CENTER);

        JButton newMeetingBtn = new JButton("New");

        newMeetingBtn.setFont(new Font("Helvetica", Font.PLAIN, 20));
        newMeetingBtn.setBackground(Color.red);
        newMeetingBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Meeting meeting = new Meeting(date, date);

        //TODO: figure out where to add new MeetingCard to UI parent
        newMeetingBtn.addActionListener(e -> new MeetEditor(meeting, new MeetingCard(meeting, mainPanel, professor), mainPanel, professor));
        add(newMeetingBtn, BorderLayout.SOUTH);

        if (date.toLocalDate().isBefore(LocalDate.now())) {
            newMeetingBtn.setVisible(false);
        } else {
            newMeetingBtn.setVisible(true);
        }
    }


    private static List<Meeting> getMeetings(List<Meeting> meetings, LocalDateTime date) {
        List<Meeting> crtMeetings = new ArrayList<>();
        for (Meeting meeting : meetings) {
            if(meeting.getStartDate().getYear() == date.getYear() && meeting.getStartDate().getMonthValue() == date.getMonthValue() && meeting.getStartDate().getDayOfMonth() == date.getDayOfMonth()){
                crtMeetings.add(meeting);
            }
        }
        return crtMeetings;
    }
}
