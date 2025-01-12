package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingsCalendar extends JPanel {
    private boolean isEmpty;
    public MeetingsCalendar(Professor professor, LocalDateTime date, JPanel mainPanel) {

        List<Meeting> crtMeetings = getMeetings(professor.getMeetings(), date);

        setLayout(new BorderLayout(20, 20));
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));

        JPanel cardList = new JPanel(new GridLayout(4, 2, 10, 10));
        cardList.setBackground(Color.white);

        JScrollPane scrollPane = new JScrollPane(cardList);

        for (Meeting m : crtMeetings) {
            MeetingCard meeting = new MeetingCard(m, mainPanel, professor, cardList);
            cardList.add(meeting);
        }
        add(scrollPane, BorderLayout.CENTER);


        JButton newMeetingBtn = new JButton("New");

        newMeetingBtn.setFont(new Font("Helvetica", Font.PLAIN, 20));
        newMeetingBtn.setBackground(Color.green);
        newMeetingBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Meeting meeting = new Meeting(date, date);

        newMeetingBtn.addActionListener(e -> new MeetEditor(meeting, new MeetingCard(meeting, mainPanel, professor, cardList), mainPanel, professor, cardList));

        if (date.toLocalDate().isBefore(LocalDate.now())) {
            newMeetingBtn.setVisible(false);
        } else {
            newMeetingBtn.setVisible(true);
        }

        JButton download = new JButton("Download");
        download.setFont(new Font("Helvetica", Font.PLAIN, 20));
        download.setBackground(Color.lightGray);
        download.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setPreferredSize(new Dimension(100, 150));

        newMeetingBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));
        download.setMaximumSize(new Dimension(Integer.MAX_VALUE, Short.MAX_VALUE));

        bottomPanel.add(newMeetingBtn);
        bottomPanel.add(download);

        add(bottomPanel, BorderLayout.SOUTH);

        if(cardList.getComponentCount() == 0) {
            this.setEmpty(true);
        }
        else{
            this.setEmpty(false);
        }

        if(this.isEmpty) {
            download.setVisible(false);
        }

        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProffesorUI.saveMeetingsToTextFile(crtMeetings);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

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


    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
