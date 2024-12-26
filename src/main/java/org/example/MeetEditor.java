package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MeetEditor extends JPanel {
    public MeetEditor(ProfessorActivity meet) {

        JFrame frame = new JFrame("Calendar");
        frame.setSize(700, 350);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.white);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        mainPanel.setBackground(Color.white);

        JPanel center = new JPanel(new GridLayout(3, 2, 20, 20));
        center.setBackground(Color.white);

        JLabel l1 = new JLabel("Title");
        l1.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l1.setHorizontalAlignment(JLabel.CENTER);
        center.add(l1);

        JTextField title = new JTextField();
        title.setFont(new Font("Helvectica", Font.PLAIN, 20));
        title.setHorizontalAlignment(JLabel.CENTER);
        center.add(title);

        JLabel l2 = new JLabel("Type");
        l2.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l2.setHorizontalAlignment(JLabel.CENTER);
        center.add(l2);

        JTextField type = new JTextField();
        type.setFont(new Font("Helvectica", Font.PLAIN, 20));
        type.setHorizontalAlignment(JLabel.CENTER);
        center.add(type);

        JLabel l3 = new JLabel("Time");
        l3.setFont(new Font("Helvectica", Font.PLAIN, 20));
        l3.setHorizontalAlignment(JLabel.CENTER);
        center.add(l3);

        JTextField time = new JTextField();
        time.setFont(new Font("Helvectica", Font.PLAIN, 20));
        time.setHorizontalAlignment(JLabel.CENTER);
        center.add(time);

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

        time.setText(meet.getStartLocalDate().toString());

        if(meet.getClassName() != null) {
            title.setText(meet.getClassName());
            type.setText(meet.getType());

            save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(title.getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "Title cannot be empty");
                        return;
                    }
                    meet.setClassName(title.getText());
                    meet.setType(type.getText());
                    try{
                        meet.setStartLocalDateToString(time.getText());

                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(null, "Check time format");
                        return;
                    }
                }
            });
        }

        mainPanel.add(bottom, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        frame.setVisible(true);
    }
}
