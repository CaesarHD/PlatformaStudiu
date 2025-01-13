package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Calendar extends JPanel {
    public static final String HELVETICA = "Helvetica";
    public final JButton todayBtn = new JButton("Today");

    public Calendar(int year, int month, LocalDateTime selectedDay, JPanel mainPanel, Professor professor) {

        setLayout(new BorderLayout(30, 30));
        setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));
        setBackground(Color.white);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setBackground(null);

        JLabel date = new JLabel(LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        date.setHorizontalAlignment(JLabel.CENTER);
        date.setFont(new Font(HELVETICA, Font.BOLD, 30));
        date.setForeground(Color.DARK_GRAY);
        top.add(date, BorderLayout.CENTER);

        JLabel leftArrow = new JLabel("\u2B05");
        JLabel rightArrow = new JLabel("\u27A1");
        rightArrow.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPanel.removeAll();
                if (month != 12) {
                    mainPanel.add(new Calendar(year, month + 1, selectedDay, mainPanel, professor));
                } else {
                    mainPanel.add(new Calendar(year + 1, 1, selectedDay, mainPanel, professor));
                }
                mainPanel.add(new MeetingsCalendar(professor, selectedDay, mainPanel));
                mainPanel.revalidate();
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

        leftArrow.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPanel.removeAll();
                if (month != 1) {
                    mainPanel.add(new Calendar(year, month - 1, selectedDay, mainPanel, professor));
                } else {
                    mainPanel.add(new Calendar(year - 1, 12, selectedDay, mainPanel, professor));
                }
                mainPanel.add(new MeetingsCalendar(professor, selectedDay, mainPanel));
                mainPanel.revalidate();
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

        leftArrow.setFont(new Font("Arial", Font.PLAIN, 100));
        rightArrow.setFont(new Font("Arial", Font.PLAIN, 100));

        leftArrow.setForeground(Color.DARK_GRAY);
        rightArrow.setForeground(Color.DARK_GRAY);

        leftArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        top.add(leftArrow, BorderLayout.WEST);

        rightArrow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        top.add(rightArrow, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        JPanel days = new JPanel(new GridLayout(7, 7));
        days.setBackground(null);

        Color header = Color.DARK_GRAY;
        days.add(new DayLabel("Mo", header, Color.white, false));
        days.add(new DayLabel("Tu", header, Color.white, false));
        days.add(new DayLabel("We", header, Color.white, false));
        days.add(new DayLabel("Th", header, Color.white, false));
        days.add(new DayLabel("Fr", header, Color.white, false));
        days.add(new DayLabel("Sa", header, Color.white, false));
        days.add(new DayLabel("Su", header, Color.white, false));

        String[] weekDays = new String[]{"Monday", "Tuesday",
                "Thursday", "Friday", "Saturday", "Sunday"};

        LocalDate firstDay = LocalDate.of(year, month, 1);

        int j = 0;

        while (firstDay.getDayOfWeek().toString().equals(weekDays[j])) {
            days.add(new DayLabel("", Color.CYAN, Color.black, false));
            j++;
        }

        int daysNum = YearMonth.of(year, month).lengthOfMonth();
        for (int i = 1; i <= daysNum; i++) {
            final int day = i;
            DayLabel dayLabel;
            if (selectedDay.getYear() == year && selectedDay.getMonthValue() == month && selectedDay.getDayOfMonth() == i) {
                dayLabel = new DayLabel(i + "", Color.orange, Color.black, true);
            } else if (hasMeeting(professor.getMeetings(), year, month, i)) {
                dayLabel = new DayLabel(i + "", Color.pink, Color.black, true);
                dayLabel.setBusyMarker(true);
            } else {
                dayLabel = new DayLabel(i + "", Color.LIGHT_GRAY, Color.black, true);
            }

            dayLabel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainPanel.removeAll();
                    
                    LocalDateTime selected = LocalDateTime.of(year, month, day, 0, 0, 0);

                    mainPanel.add(new Calendar(year, month, selected, mainPanel, professor));
                    mainPanel.add(new MeetingsCalendar(professor, selected, mainPanel));
                    mainPanel.revalidate();
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


            days.add(dayLabel);
        }

        for (int i = 0; i < (42 - (j + daysNum)); i++) {
            days.add(new DayLabel("", Color.LIGHT_GRAY, Color.black, true));
        }


        add(days, BorderLayout.CENTER);


        todayBtn.setFont(new Font("Helvetica", Font.PLAIN, 20));
        todayBtn.setBackground(Color.DARK_GRAY);
        todayBtn.setForeground(Color.WHITE);
        todayBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(todayBtn, BorderLayout.SOUTH);

        todayBtn.addActionListener(e -> {
            LocalDateTime today = LocalDateTime.now();
            mainPanel.removeAll();
            mainPanel.add(new Calendar(today.getYear(), today.getMonthValue(), today, mainPanel, professor));
            mainPanel.add(new MeetingsCalendar(professor, today, mainPanel));
            mainPanel.revalidate();
        });

    }

    public boolean hasMeeting(List<Meeting> meetings, int year, int month, int i) {
        for (Meeting meeting : meetings) {
            if (year == meeting.getStartDate().getYear() && month == meeting.getStartDate().getMonthValue()
                    && i == meeting.getStartDate().getDayOfMonth()) {
                return true;
            }
        }
        return false;
    }

}
