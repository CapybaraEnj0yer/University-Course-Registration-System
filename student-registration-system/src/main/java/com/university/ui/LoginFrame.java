package com.university.ui;

import com.university.model.Admin;
import com.university.model.Professor;
import com.university.model.Student;
import com.university.model.User;
import com.university.service.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private AuthenticationService authService;
    private AdminService adminService;
    private ProfessorService profService;
    private StudentService studentService;

    private JTextField usernameField;
    private JButton loginButton;

    // Accept all 4 services
    public LoginFrame(AuthenticationService authService, AdminService adminService,
            ProfessorService profService, StudentService studentService) {
        this.authService = authService;
        this.adminService = adminService;
        this.profService = profService;
        this.studentService = studentService;

        setTitle("University Registration System - Login");
        setSize(450, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel hintPanel = new JPanel();
        hintPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel hintLabel = new JLabel("<html><center><b>Available Test Accounts:</b><br/>" +
                "Admin: <i>admin</i> | Professors: <i>P01, P02</i> | Students: <i>S01, S02, S03</i></center></html>");
        hintLabel.setForeground(Color.DARK_GRAY);
        hintPanel.add(hintLabel);

        JPanel loginPanel = new JPanel(new FlowLayout());
        loginPanel.add(new JLabel("Username: "));
        usernameField = new JTextField(15);
        loginPanel.add(usernameField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        getRootPane().setDefaultButton(loginButton);
        buttonPanel.add(loginButton);

        add(hintPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        User loggedInUser = authService.login(username);

        if (loggedInUser != null) {
            // Route to the correct dashboard and pass ALL services
            if (loggedInUser.getRole().equals("ADMIN")) {
                new AdminDashboardFrame((Admin) loggedInUser, authService, adminService, profService, studentService)
                        .setVisible(true);
            } else if (loggedInUser.getRole().equals("PROFESSOR")) {
                new ProfessorDashboardFrame((Professor) loggedInUser, authService, adminService, profService,
                        studentService).setVisible(true);
            } else if (loggedInUser.getRole().equals("STUDENT")) {
                new StudentDashboardFrame((Student) loggedInUser, authService, adminService, profService,
                        studentService).setVisible(true);
            }

            this.dispose(); // Close login window
        } else {
            JOptionPane.showMessageDialog(this, "User not found. Please check the hint above.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}