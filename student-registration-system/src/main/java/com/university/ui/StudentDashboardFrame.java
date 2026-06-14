package com.university.ui;

import com.university.model.Course;
import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Student;
import com.university.service.AdminService;
import com.university.service.AuthenticationService;
import com.university.service.ProfessorService;
import com.university.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentDashboardFrame extends BaseDashboardFrame {
    private Student loggedInStudent;

    private DefaultTableModel catalogTableModel;
    private JTable catalogTable;

    private JTable transcriptTable;
    private DefaultTableModel transcriptTableModel;
    private JLabel gpaLabel;

    public StudentDashboardFrame(Student loggedInStudent, AuthenticationService authService,
            AdminService adminService, ProfessorService profService,
            StudentService studentService) {

        // Pass the title and all services up to the BaseDashboardFrame
        super("Student Portal - " + loggedInStudent.getName(), authService, adminService, profService, studentService);

        this.loggedInStudent = loggedInStudent;

        // The size, layout, and centering are already handled by the super() call!
        initUI();
        refreshAllData();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // ==========================================
        // --- TAB 1: Course Registration ---
        // ==========================================
        JPanel registrationPanel = new JPanel(new BorderLayout());
        String[] catalogCols = { "Offering ID", "Course", "Semester", "Schedule", "Credits", "Prerequisites",
                "Seats Available" };
        // Custom DefaultTableModel to make cells read-only (prevents double-click
        // editing)
        catalogTableModel = new DefaultTableModel(catalogCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        catalogTable = new JTable(catalogTableModel);
        registrationPanel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        // Bottom panel for Tab 1 (Enroll Button)
        JPanel enrollPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton enrollButton = new JButton("Enroll in Selected Course");
        enrollButton.addActionListener(e -> handleEnrollment());
        enrollPanel.add(enrollButton);
        registrationPanel.add(enrollPanel, BorderLayout.SOUTH);

        // ==========================================
        // --- TAB 2: Transcript & Schedule ---
        // ==========================================
        JPanel transcriptPanel = new JPanel(new BorderLayout());
        String[] transcriptCols = { "Offering ID", "Course", "Semester", "Schedule", "Credits", "Status", "Grade" };

        // Custom DefaultTableModel to make cells read-only
        transcriptTableModel = new DefaultTableModel(transcriptCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transcriptTable = new JTable(transcriptTableModel);
        transcriptPanel.add(new JScrollPane(transcriptTable), BorderLayout.CENTER);

        // Combined Bottom Panel for Tab 2 (GPA on left, Drop on right)
        JPanel transcriptBottomPanel = new JPanel(new BorderLayout());

        // Left side: GPA Label
        JPanel gpaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gpaLabel = new JLabel("Cumulative GPA: 0.0");
        gpaLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gpaPanel.add(gpaLabel);

        // Right side: Drop Button
        JPanel dropPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton dropButton = new JButton("Drop Selected Course");
        dropButton.addActionListener(e -> handleDropCourse());
        dropPanel.add(dropButton);

        // Assemble the bottom panel
        transcriptBottomPanel.add(gpaPanel, BorderLayout.WEST);
        transcriptBottomPanel.add(dropPanel, BorderLayout.EAST);

        transcriptPanel.add(transcriptBottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // --- Final Assembly ---
        // ==========================================

        // Add both tabs to the main pane
        tabbedPane.addTab("Course Registration", registrationPanel);
        tabbedPane.addTab("My Transcript & Schedule", transcriptPanel);

        // Add the tabbed pane to the center of the Frame
        add(tabbedPane, BorderLayout.CENTER);

        // Add the shared Logout Panel from BaseDashboardFrame to the bottom
        add(createLogoutPanel(), BorderLayout.SOUTH);
    }

    private void handleEnrollment() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to enroll in.");
            return;
        }

        // Get the Offering ID from the first column of the selected row
        String offeringId = (String) catalogTableModel.getValueAt(selectedRow, 0);

        // Let the service handle the business logic
        String result = studentService.enrollInCourse(loggedInStudent, offeringId);

        if (result.equals("Success")) {
            JOptionPane.showMessageDialog(this, "Successfully enrolled in " + offeringId + "!");
            refreshAllData(); // Update tables to show new seats available and update transcript
        } else {
            JOptionPane.showMessageDialog(this, result, "Enrollment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllData() {
        // 1. Refresh Catalog Table
        catalogTableModel.setRowCount(0);
        for (CourseOffering o : studentService.getAvailableOfferings()) {
            int seatsAvailable = o.getMaxCapacity() - o.getEnrolledStudents().size();
            String schedule = (o.getTimeSlot() != null) ? o.getTimeSlot().toString() : "TBD";

            // --- NEW: Build Prerequisite String ---
            String prereqString = "None";
            if (!o.getCourse().getPrerequisites().isEmpty()) {
                prereqString = "";
                for (Course p : o.getCourse().getPrerequisites()) {
                    prereqString += p.getCourseCode() + " ";
                }
            }
            // --------------------------------------

            // Make sure the array matches the new 7-column layout!
            catalogTableModel.addRow(new Object[] {
                    o.getOfferingId(), o.getCourse().getTitle(), o.getSemester(), schedule,
                    o.getCourse().getCredits(), prereqString, seatsAvailable
            });
        }
        // 2. Refresh Transcript Table
        transcriptTableModel.setRowCount(0);

        // Add active courses (In Progress)
        for (Enrollment e : loggedInStudent.getTranscript().getActiveEnrollments()) {
            String schedule = (e.getCourseOffering().getTimeSlot() != null)
                    ? e.getCourseOffering().getTimeSlot().toString()
                    : "TBD";
            transcriptTableModel.addRow(new Object[] {
                    e.getCourseOffering().getOfferingId(), e.getCourseOffering().getCourse().getTitle(),
                    e.getCourseOffering().getSemester(), schedule, e.getCourseOffering().getCourse().getCredits(),
                    "In Progress", "N/A"
            });
        }

        // Add completed courses
        for (Enrollment e : loggedInStudent.getTranscript().getPastRecords()) {
            String schedule = (e.getCourseOffering().getTimeSlot() != null)
                    ? e.getCourseOffering().getTimeSlot().toString()
                    : "TBD";
            transcriptTableModel.addRow(new Object[] {
                    e.getCourseOffering().getOfferingId(), e.getCourseOffering().getCourse().getTitle(),
                    e.getCourseOffering().getSemester(), schedule, e.getCourseOffering().getCourse().getCredits(),
                    "Completed", e.getGrade()
            });
        }

        // 3. Update GPA
        double gpa = loggedInStudent.getTranscript().calculateCumulativeGPA();
        gpaLabel.setText(String.format("Cumulative GPA: %.2f", gpa));
    }

    private void handleDropCourse() {
        int selectedRow = transcriptTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course from your transcript to drop.");
            return;
        }

        // Prevent dropping past courses (Checking the "Status" column, which is index
        // 5)
        String status = (String) transcriptTableModel.getValueAt(selectedRow, 5);
        if (status.equals("Completed")) {
            JOptionPane.showMessageDialog(this, "You cannot drop a course that has already been completed and graded.");
            return;
        }

        // Get the Offering ID (Column 0)
        String offeringId = (String) transcriptTableModel.getValueAt(selectedRow, 0);

        // Double-check with the user
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + offeringId + "?",
                "Confirm Drop", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = studentService.dropCourse(loggedInStudent, offeringId);

            if (result.equals("Success")) {
                JOptionPane.showMessageDialog(this, "Successfully dropped " + offeringId + ".");
                refreshAllData(); // Updates both the transcript AND the catalog seats!
            } else {
                JOptionPane.showMessageDialog(this, result, "Drop Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}