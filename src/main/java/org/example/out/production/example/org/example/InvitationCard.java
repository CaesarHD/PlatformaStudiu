package org.example.out.production.example.org.example;

import org.example.DBController;
import org.example.Professor;
import org.example.ProffesorUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.List;

public class InvitationCard extends JPanel {
    private JButton acceptButton = new JButton("Accept");
    private JButton rejectButton = new JButton("Reject");

    public InvitationCard(Object[] activity, JPanel parentPanel, Professor professor, List<Object[]> activities, JPanel studentActivityPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        setPreferredSize(new Dimension(950, 150));

        JLabel titleLabel = new JLabel("Activity: " + activity[0].toString());
        JLabel dateLabel = new JLabel("Date: " + activity[1].toString());
        JLabel hoursLabel = new JLabel("Hours: " + activity[2].toString());
        JLabel minParticipantsLabel = new JLabel("Minimum Participants: " + activity[3].toString());
        JLabel currentParticipantsLabel = new JLabel("Current Participants: " + activity[4].toString());
        JLabel expirationLabel = new JLabel("Expiration Time: " + activity[5].toString());
        JLabel statusLabel = new JLabel("Status: " + activity[6].toString());

        Font defaultFont = new Font("Arial", Font.PLAIN, 20);
        titleLabel.setFont(defaultFont);
        dateLabel.setFont(defaultFont);
        hoursLabel.setFont(defaultFont);
        minParticipantsLabel.setFont(defaultFont);
        currentParticipantsLabel.setFont(defaultFont);
        expirationLabel.setFont(defaultFont);
        statusLabel.setFont(defaultFont);

        add(titleLabel);
        add(dateLabel);
        add(hoursLabel);
        add(minParticipantsLabel);
        add(currentParticipantsLabel);
        add(expirationLabel);
        add(statusLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        acceptButton.setFont(new Font("Arial", Font.PLAIN, 16));
        acceptButton.setPreferredSize(new Dimension(150, 60));
        rejectButton.setFont(new Font("Arial", Font.PLAIN, 16));
        rejectButton.setPreferredSize(new Dimension(150, 60));
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel);

        add(Box.createVerticalStrut(10));

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int newFontSize = Math.max(14, (int) (width * 0.025));

                Font newFont = new Font("Arial", Font.PLAIN, newFontSize);

                titleLabel.setFont(new Font("Arial", Font.BOLD, newFontSize + 4));
                dateLabel.setFont(newFont);
                hoursLabel.setFont(newFont);
                minParticipantsLabel.setFont(newFont);
                currentParticipantsLabel.setFont(newFont);
                expirationLabel.setFont(newFont);
                statusLabel.setFont(newFont);
            }
        });


        rejectButton.addActionListener(e -> {
            activities.remove(activity);
            DBController.deleteInvitation(professor.getCNP(), (int) activity[7]);
            parentPanel.remove(InvitationCard.this);
            parentPanel.revalidate();
            parentPanel.repaint();
        });

        acceptButton.addActionListener(e -> {
            parentPanel.remove(InvitationCard.this);

            DBController.deleteInvitation(professor.getCNP(), (int) activity[7]);
            DBController.addProfessorToStudentActivity(professor, (int) activity[7]);
            activities.remove(activity);
            studentActivityPanel.revalidate();
            studentActivityPanel.repaint();

            parentPanel.revalidate();
            parentPanel.repaint();
        });
    }

    public JButton getAcceptButton() {
        return acceptButton;
    }

    public void setAcceptButton(JButton acceptButton) {
        this.acceptButton = acceptButton;
    }

    public JButton getRejectButton() {
        return rejectButton;
    }

    public void setRejectButton(JButton rejectButton) {
        this.rejectButton = rejectButton;
    }
}
