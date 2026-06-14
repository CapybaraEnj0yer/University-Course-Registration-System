package com.university.ui;

import com.university.model.Admin;
import com.university.model.Course;
import com.university.model.CourseOffering;
import com.university.model.Professor;
import com.university.model.TimeSlot;
import com.university.model.User;
import com.university.service.AdminService;
import com.university.service.AuthenticationService;
import com.university.service.ProfessorService;
import com.university.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends BaseDashboardFrame {
    private DefaultTableModel catalogTableModel;
    private DefaultTableModel offeringsTableModel;
    private DefaultTableModel usersTableModel;

    public AdminDashboardFrame(Admin loggedInAdmin, AuthenticationService authService,
            AdminService adminService, ProfessorService profService,
            StudentService studentService) {

        super("Admin Dashboard - Logged in as: " + loggedInAdmin.getName(),
                authService, adminService, profService, studentService);

        initUI();
        refreshAllData();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: Course Catalog ---
        JPanel catalogPanel = new JPanel(new BorderLayout());
        String[] catalogColumns = { "Course Code", "Title", "Credits", "Prerequisites" };

        catalogTableModel = new DefaultTableModel(catalogColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable catalogTable = new JTable(catalogTableModel);
        catalogPanel.add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        JPanel catalogButtonPanel = new JPanel(new FlowLayout());
        JButton addCourseButton = new JButton("Add New Base Course");
        addCourseButton.addActionListener(e -> showAddCourseDialog());
        catalogButtonPanel.add(addCourseButton);

        JButton addPrereqButton = new JButton("Set Prerequisite");
        addPrereqButton.addActionListener(e -> showAddPrerequisiteDialog());
        catalogButtonPanel.add(addPrereqButton);
        catalogPanel.add(catalogButtonPanel, BorderLayout.SOUTH);

        // --- TAB 2: Course Offerings (Semesters) ---
        JPanel offeringsPanel = new JPanel(new BorderLayout());
        String[] offeringColumns = { "Offering ID", "Course Code", "Semester", "Schedule", "Capacity", "Enrolled" };
        offeringsTableModel = new DefaultTableModel(offeringColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable offeringsTable = new JTable(offeringsTableModel);
        offeringsPanel.add(new JScrollPane(offeringsTable), BorderLayout.CENTER);

        JPanel offeringButtonPanel = new JPanel(new FlowLayout());
        JButton addOfferingButton = new JButton("Open Course for Semester");
        JButton assignProfButton = new JButton("Assign Professor");
        assignProfButton.addActionListener(e -> showAssignProfessorDialog(offeringsTable));
        offeringButtonPanel.add(assignProfButton);
        JButton modifyScheduleButton = new JButton("Modify Schedule");
        modifyScheduleButton.addActionListener(e -> showModifyScheduleDialog(offeringsTable));
        offeringButtonPanel.add(modifyScheduleButton);
        addOfferingButton.addActionListener(e -> showAddOfferingDialog());
        offeringButtonPanel.add(addOfferingButton);
        offeringsPanel.add(offeringButtonPanel, BorderLayout.SOUTH);

        // --- TAB 3: User Management ---
        JPanel usersPanel = new JPanel(new BorderLayout());
        String[] userColumns = { "ID / Username", "Name", "Role" };
        usersTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable usersTable = new JTable(usersTableModel);
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JPanel userButtonPanel = new JPanel(new FlowLayout());
        JButton addUserButton = new JButton("Create New User");
        addUserButton.addActionListener(e -> showAddUserDialog());
        userButtonPanel.add(addUserButton);
        usersPanel.add(userButtonPanel, BorderLayout.SOUTH);

        // Add to the tabbed pane:
        tabbedPane.addTab("Course Catalog", catalogPanel);
        tabbedPane.addTab("Active Offerings", offeringsPanel);
        tabbedPane.addTab("User Management", usersPanel); // NEW TAB

        // Add everything to frame
        add(new JLabel("  Welcome to the System Management Console"), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Add the shared Logout Panel from BaseDashboardFrame
        add(createLogoutPanel(), BorderLayout.SOUTH);
    }

    private void showAddCourseDialog() {
        JTextField codeField = new JTextField(7);
        JTextField titleField = new JTextField(15);
        JTextField creditsField = new JTextField(3);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Code (e.g., CS101):"));
        panel.add(codeField);
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Credits:"));
        panel.add(creditsField);

        if (JOptionPane.showConfirmDialog(this, panel, "New Base Course",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                boolean success = adminService.createNewCourse(
                        codeField.getText().trim().toUpperCase(),
                        titleField.getText().trim(),
                        Integer.parseInt(creditsField.getText().trim()));
                if (success)
                    refreshAllData();
                else
                    JOptionPane.showMessageDialog(this, "Course Code already exists.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Credits must be a number.");
            }
        }
    }

    private void showModifyScheduleDialog(JTable offeringsTable) {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering from the table first.");
            return;
        }

        // Get the ID of the selected offering
        String offeringId = (String) offeringsTableModel.getValueAt(selectedRow, 0);

        // Setup the UI components
        String[] days = { "MON/WED", "TUE/THU", "FRI" };
        JComboBox<String> daysDropdown = new JComboBox<>(days);

        Integer[] hours = { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
        JComboBox<Integer> startHourDropdown = new JComboBox<>(hours);
        JComboBox<Integer> endHourDropdown = new JComboBox<>(hours);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("New Meeting Days:"));
        panel.add(daysDropdown);
        panel.add(new JLabel("New Start Hour (24h):"));
        panel.add(startHourDropdown);
        panel.add(new JLabel("New End Hour (24h):"));
        panel.add(endHourDropdown);

        if (JOptionPane.showConfirmDialog(this, panel, "Modify Schedule: " + offeringId,
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int start = (int) startHourDropdown.getSelectedItem();
                int end = (int) endHourDropdown.getSelectedItem();

                if (start >= end) {
                    JOptionPane.showMessageDialog(this, "Error: Class must end after it starts.");
                    return;
                }

                TimeSlot newSlot = new TimeSlot(daysDropdown.getSelectedItem().toString(), start, end);

                if (adminService.modifyOfferingSchedule(offeringId, newSlot)) {
                    JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
                    refreshAllData(); // Instantly updates the UI table
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Could not update schedule.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddOfferingDialog() {
        List<Course> courses = adminService.getAllCourses();
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Create a Base Course first!");
            return;
        }

        // 1. Course Dropdown
        JComboBox<String> courseDropdown = new JComboBox<>();
        for (Course c : courses) {
            courseDropdown.addItem(c.getCourseCode());
        }

        // 2. Safe Semester Dropdown
        String[] semesters = { "SPRING 2026", "FALL 2026", "SPRING 2027", "FALL 2027" };
        JComboBox<String> semesterDropdown = new JComboBox<>(semesters);

        // 3. Professor Dropdown
        JComboBox<String> profDropdown = new JComboBox<>();
        profDropdown.addItem("UNASSIGNED");
        for (Professor p : adminService.getAllProfessors()) {
            profDropdown.addItem(p.getId() + " - " + p.getName());
        }

        // 4. TimeSlot Fields
        String[] days = { "MON/WED", "TUE/THU", "FRI" };
        JComboBox<String> daysDropdown = new JComboBox<>(days);

        Integer[] hours = { 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
        JComboBox<Integer> startHourDropdown = new JComboBox<>(hours);
        JComboBox<Integer> endHourDropdown = new JComboBox<>(hours);

        JTextField capacityField = new JTextField("30");

        // Layout the panel
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.add(new JLabel("Base Course:"));
        panel.add(courseDropdown);
        panel.add(new JLabel("Semester:"));
        panel.add(semesterDropdown);
        panel.add(new JLabel("Professor:"));
        panel.add(profDropdown);
        panel.add(new JLabel("Max Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Meeting Days:"));
        panel.add(daysDropdown);
        panel.add(new JLabel("Start Hour (24h):"));
        panel.add(startHourDropdown);
        panel.add(new JLabel("End Hour (24h):"));
        panel.add(endHourDropdown);

        if (JOptionPane.showConfirmDialog(this, panel, "Open Course Section",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                int start = (int) startHourDropdown.getSelectedItem();
                int end = (int) endHourDropdown.getSelectedItem();

                if (start >= end) {
                    JOptionPane.showMessageDialog(this, "Error: Class must end after it starts.");
                    return;
                }

                TimeSlot newSlot = new TimeSlot(daysDropdown.getSelectedItem().toString(), start, end);

                String profSelection = profDropdown.getSelectedItem().toString();
                String profId = profSelection.equals("UNASSIGNED") ? null : profSelection.split(" - ")[0];

                boolean success = adminService.createCourseOffering(
                        courseDropdown.getSelectedItem().toString(),
                        semesterDropdown.getSelectedItem().toString(),
                        Integer.parseInt(capacityField.getText().trim()),
                        newSlot,
                        profId);

                if (success)
                    refreshAllData();
                else
                    JOptionPane.showMessageDialog(this, "Error creating offering.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddUserDialog() {
        String[] roles = { "STUDENT", "PROFESSOR" };
        JComboBox<String> roleDropdown = new JComboBox<>(roles);
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Role:"));
        panel.add(roleDropdown);
        panel.add(new JLabel("ID / Username (e.g. S04):"));
        panel.add(idField);
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);

        if (JOptionPane.showConfirmDialog(this, panel, "Create New User",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String role = roleDropdown.getSelectedItem().toString();
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (adminService.createUser(role, id, name)) {
                JOptionPane.showMessageDialog(this, "User created successfully!");
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Username/ID already exists.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAssignProfessorDialog(JTable offeringsTable) {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering from the table first.");
            return;
        }

        String offeringId = (String) offeringsTableModel.getValueAt(selectedRow, 0);
        List<Professor> professors = adminService.getAllProfessors();

        if (professors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No professors exist in the system!");
            return;
        }

        JComboBox<String> profDropdown = new JComboBox<>();
        for (Professor p : professors) {
            profDropdown.addItem(p.getId() + " - " + p.getName());
        }

        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Professor for " + offeringId + ":"));
        panel.add(profDropdown);

        if (JOptionPane.showConfirmDialog(this, panel, "Assign Professor",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            // Extract just the ID from the dropdown string (e.g., "P01 - Dr. Smith" ->
            // "P01")
            String selectedItem = profDropdown.getSelectedItem().toString();
            String profId = selectedItem.split(" - ")[0];

            if (adminService.assignProfessorToOffering(offeringId, profId)) {
                JOptionPane.showMessageDialog(this, "Professor assigned successfully!");
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign professor.");
            }
        }
    }

    private void refreshAllData() {
        catalogTableModel.setRowCount(0);
        for (Course c : adminService.getAllCourses()) {

            // Build the string of prerequisite codes
            String prereqString = "None";
            if (!c.getPrerequisites().isEmpty()) {
                prereqString = "";
                for (Course p : c.getPrerequisites()) {
                    prereqString += p.getCourseCode() + " ";
                }
            }

            // Add the prereqString as the 4th item in the row!
            catalogTableModel.addRow(new Object[] {
                    c.getCourseCode(),
                    c.getTitle(),
                    c.getCredits(),
                    prereqString.trim()
            });
        }

        offeringsTableModel.setRowCount(0);
        for (CourseOffering o : adminService.getAllOfferings()) {
            String profName = (o.getProfessor() != null) ? o.getProfessor().getName() : "Unassigned";
            String schedule = (o.getTimeSlot() != null) ? o.getTimeSlot().toString() : "TBD";

            offeringsTableModel.addRow(new Object[] {
                    o.getOfferingId(), o.getCourse().getCourseCode(), o.getSemester(),
                    schedule, o.getMaxCapacity(), o.getEnrolledStudents().size() + " / " + profName
            });
        }

        usersTableModel.setRowCount(0);
        for (User u : adminService.getAllUsers()) {
            usersTableModel.addRow(new Object[] { u.getUsername(), u.getName(), u.getRole() });
        }
    }

    // Inside AdminDashboardFrame.java
    private void showAddPrerequisiteDialog() {
        List<Course> courses = adminService.getAllCourses();
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You need to create base courses first.");
            return;
        }

        JComboBox<String> targetCourseDropdown = new JComboBox<>();
        JComboBox<String> prereqCourseDropdown = new JComboBox<>();

        // --- NEW: Add the clear option as the very first item ---
        prereqCourseDropdown.addItem("NONE (Clear All)");

        for (Course c : courses) {
            targetCourseDropdown.addItem(c.getCourseCode());
            prereqCourseDropdown.addItem(c.getCourseCode());
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Target Course:"));
        panel.add(targetCourseDropdown);
        panel.add(new JLabel("Add Prerequisite:"));
        panel.add(prereqCourseDropdown);

        if (JOptionPane.showConfirmDialog(this, panel, "Modify Prerequisites",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String target = targetCourseDropdown.getSelectedItem().toString();

            // Format the string depending on what they picked
            String prereqRaw = prereqCourseDropdown.getSelectedItem().toString();
            String prereq = prereqRaw.equals("NONE (Clear All)") ? "NONE" : prereqRaw;

            if (adminService.addPrerequisite(target, prereq)) {
                // Give accurate feedback to the user
                if (prereq.equals("NONE")) {
                    JOptionPane.showMessageDialog(this, "All prerequisites cleared for " + target + ".");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Prerequisite linked successfully! (Course now requires: " + target + ")");
                }
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: Cannot link these courses (ensure a course isn't its own prerequisite).", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}