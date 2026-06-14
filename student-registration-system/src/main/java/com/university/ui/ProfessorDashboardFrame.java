package com.university.ui;

import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Professor;
import com.university.service.AdminService;
import com.university.service.AuthenticationService;
import com.university.service.ProfessorService;
import com.university.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProfessorDashboardFrame extends BaseDashboardFrame {
    private Professor loggedInProfessor;

    private JTable coursesTable;
    private DefaultTableModel coursesTableModel;

    private JTable rosterTable;
    private DefaultTableModel rosterTableModel;

    private List<CourseOffering> myOfferings;
    private List<Enrollment> currentRoster;

    public ProfessorDashboardFrame(Professor loggedInProfessor, AuthenticationService authService,
            AdminService adminService, ProfessorService profService,
            StudentService studentService) {

        super("Professor Portal - Dr. " + loggedInProfessor.getName(),
                authService, adminService, profService, studentService);

        this.loggedInProfessor = loggedInProfessor;

        initUI();
        loadCourses();
    }

    private void initUI() {
        // --- Top Panel (Master): List of Courses ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("My Assigned Courses"));

        String[] courseCols = { "Offering ID", "Course", "Semester", "Schedule", "Capacity" };
        coursesTableModel = new DefaultTableModel(courseCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursesTable = new JTable(coursesTableModel);
        topPanel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);

        // Add a click listener to the courses table
        coursesTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && coursesTable.getSelectedRow() != -1) {
                loadRoster(coursesTable.getSelectedRow());
            }
        });

        // --- Bottom Panel (Detail): Class Roster & Grading ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Class Roster & Grading"));

        String[] rosterCols = { "Student ID", "Student Name", "Current Grade", "Status" };
        rosterTableModel = new DefaultTableModel(rosterCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        rosterTable = new JTable(rosterTableModel);
        bottomPanel.add(new JScrollPane(rosterTable), BorderLayout.CENTER);

        JPanel gradingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton gradeButton = new JButton("Assign Final Grade to Selected Student");

        gradeButton.addActionListener(e -> assignGrade());

        gradingPanel.add(gradeButton);
        bottomPanel.add(gradingPanel, BorderLayout.SOUTH);

        // --- Split Pane to hold both ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);

        // Add the shared Logout Panel from BaseDashboardFrame
        add(createLogoutPanel(), BorderLayout.SOUTH);
    }

    private void loadCourses() {
        coursesTableModel.setRowCount(0);
        myOfferings = profService.getMyOfferings(loggedInProfessor);

        for (CourseOffering offering : myOfferings) {
            String schedule = (offering.getTimeSlot() != null) ? offering.getTimeSlot().toString() : "TBD";
            coursesTableModel.addRow(new Object[] {
                    offering.getOfferingId(), offering.getCourse().getTitle(),
                    offering.getSemester(), schedule, offering.getMaxCapacity()
            });
        }
    }

    private void loadRoster(int rowIndex) {
        rosterTableModel.setRowCount(0);
        String offeringId = myOfferings.get(rowIndex).getOfferingId();

        currentRoster = profService.getRoster(offeringId);

        for (Enrollment e : currentRoster) {
            rosterTableModel.addRow(new Object[] {
                    e.getStudent().getId(),
                    e.getStudent().getName(),
                    e.getGrade(),
                    e.isCompleted() ? "Completed" : "In Progress"
            });
        }
    }

    private void assignGrade() {
        int selectedRow = rosterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the roster first.");
            return;
        }

        Enrollment selectedEnrollment = currentRoster.get(selectedRow);

        if (selectedEnrollment.isCompleted()) {
            JOptionPane.showMessageDialog(this, "This student has already been graded.");
            return;
        }

        String[] grades = { "A", "B", "C", "D", "F" };
        String grade = (String) JOptionPane.showInputDialog(this,
                "Select final grade for " + selectedEnrollment.getStudent().getName() + ":",
                "Assign Grade", JOptionPane.QUESTION_MESSAGE, null, grades, grades[0]);

        if (grade != null) {
            profService.assignGrade(selectedEnrollment, grade);
            loadRoster(coursesTable.getSelectedRow()); // Refresh the visual roster
            JOptionPane.showMessageDialog(this, "Grade assigned successfully.");
        }
    }
}