package com.university.ui;

import com.university.service.*;
import javax.swing.*;
import java.awt.*;

public abstract class BaseDashboardFrame extends JFrame {
    // Protected so subclasses can access them if needed
    protected AuthenticationService authService;
    protected AdminService adminService;
    protected ProfessorService profService;
    protected StudentService studentService;

    public BaseDashboardFrame(String title, AuthenticationService authService,
            AdminService adminService, ProfessorService profService,
            StudentService studentService) {
        this.authService = authService;
        this.adminService = adminService;
        this.profService = profService;
        this.studentService = studentService;

        // Centralize all the standard window setups here!
        setTitle(title);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    /**
     * A helper method that subclasses can call to get a pre-built,
     * fully functioning logout panel.
     */
    protected JPanel createLogoutPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");

        logoutButton.addActionListener(e -> {
            this.dispose(); // Close current dashboard
            // Open a fresh login screen with all services attached
            new LoginFrame(authService, adminService, profService, studentService).setVisible(true);
        });

        footer.add(logoutButton);
        return footer;
    }
}