package com.university;

import com.university.model.*;
import com.university.repository.*;
import com.university.service.*;
import com.university.ui.LoginFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Repositories (Data Layer)
        UserRepository userRepo = new InMemoryUserRepository();
        CourseRepository courseRepo = new InMemoryCourseRepository();
        CourseOfferingRepository offeringRepo = new InMemoryCourseOfferingRepository();
        EnrollmentRepository enrollmentRepo = new InMemoryEnrollmentRepository();

        // --- SEED MOCK DATA ---
        // (The users A01, P01, P02, S01, S02, S03 are already seeded inside
        // InMemoryUserRepository)

        // Create a base course and save it
        Course cs101 = new Course("CS101", "Intro to Programming", 3);
        courseRepo.save(cs101);

        Course cs201 = new Course("CS201", "Data Structures", 4);
        cs201.addPrerequisite(cs101); // <-- THE MAGIC LINK
        courseRepo.save(cs201);

        // 2. Create Offerings
        TimeSlot mw10 = new TimeSlot("MON/WED", 10, 12);
        TimeSlot tth14 = new TimeSlot("TUE/THU", 14, 16);

        CourseOffering offering1 = new CourseOffering("CS101-FALL2026-01", cs101, "FALL 2026", 30, mw10);
        CourseOffering offering2 = new CourseOffering("CS201-FALL2026-01", cs201, "FALL 2026", 30, tth14);

        offeringRepo.save(offering1);
        offeringRepo.save(offering2);
        // Link the Professor to the offering
        Professor p01 = (Professor) userRepo.findByUsername("P01");
        if (p01 != null) {
            p01.addCuratedCourse(offering1);
        }

        // Save the offering to the database
        offeringRepo.save(offering1);

        // Optionally, pre-enroll a student for testing purposes
        Student s01 = (Student) userRepo.findByUsername("S01");
        if (s01 != null) {
            Enrollment testEnrollment = new Enrollment(s01, offering1);
            s01.getTranscript().addActiveEnrollment(testEnrollment);
            enrollmentRepo.save(testEnrollment);
            offering1.addStudent(s01); // Update the class roster
        }
        // ----------------------

        // 2. Initialize Services (Logic Layer)
        AuthenticationService authService = new AuthenticationService(userRepo);
        AdminService adminService = new AdminService(courseRepo, offeringRepo, userRepo);
        ProfessorService profService = new ProfessorService(offeringRepo, enrollmentRepo);
        StudentService studentService = new StudentService(offeringRepo, enrollmentRepo);

        // 3. Launch UI Layer
        SwingUtilities.invokeLater(() -> {
            // Pass ALL services to the LoginFrame
            LoginFrame loginFrame = new LoginFrame(authService, adminService, profService, studentService);
            loginFrame.setVisible(true);
        });
    }
}