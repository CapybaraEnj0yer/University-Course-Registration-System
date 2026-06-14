package com.university;

import com.university.model.*;
import com.university.repository.*;
import com.university.service.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class AppTest {

    // Repositories
    private UserRepository userRepo;
    private CourseRepository courseRepo;
    private CourseOfferingRepository offeringRepo;
    private EnrollmentRepository enrollmentRepo;

    // Services
    private AuthenticationService authService;
    private AdminService adminService;
    private ProfessorService profService;
    private StudentService studentService;

    // Shared Test Data
    private Student johnbarneyCalhoun;
    private Professor drFreeman;
    private Admin alyxAdmin;
    private CourseOffering cs101Offering;
    private CourseOffering cs201Offering;

    @Before
    public void setUp() {
        // 1. Initialize fresh repositories
        userRepo = new InMemoryUserRepository();
        courseRepo = new InMemoryCourseRepository();
        offeringRepo = new InMemoryCourseOfferingRepository();
        enrollmentRepo = new InMemoryEnrollmentRepository();

        // 2. Initialize Services
        authService = new AuthenticationService(userRepo);
        adminService = new AdminService(courseRepo, offeringRepo, userRepo);
        profService = new ProfessorService(offeringRepo, enrollmentRepo);
        studentService = new StudentService(offeringRepo, enrollmentRepo);

        // 3. Seed Realistic Users (Black Mesa / City 17 Edition)
        johnbarneyCalhoun = new Student("S99", "S99", "Barney Calhoun");
        drFreeman = new Professor("P99", "P99", "Dr. Gordon Freeman");
        alyxAdmin = new Admin("A99", "A99", "Alyx Vance");

        userRepo.save(johnbarneyCalhoun);
        userRepo.save(drFreeman);
        userRepo.save(alyxAdmin);

        // 4. Seed Courses & Prerequisites
        Course cs101 = new Course("CS101", "Intro to Programming", 3);
        Course cs201 = new Course("CS201", "Data Structures", 4);
        cs201.addPrerequisite(cs101);
        courseRepo.save(cs101);
        courseRepo.save(cs201);

        // 5. Seed Offerings (CS101 has a strict capacity of 1 for testing limits)
        TimeSlot mw10 = new TimeSlot("MON/WED", 10, 12);
        TimeSlot f10 = new TimeSlot("FRI", 10, 12);

        cs101Offering = new CourseOffering("CS101-01", cs101, "FALL2026", 1, mw10);
        cs201Offering = new CourseOffering("CS201-01", cs201, "FALL2026", 30, f10);

        offeringRepo.save(cs101Offering);
        offeringRepo.save(cs201Offering);
    }

    // ==========================================
    // 1. AUTHENTICATION SERVICE TESTS
    // ==========================================
    @Test
    public void testLoginSuccess() {
        User loggedInUser = authService.login("S99");
        assertNotNull("User should be found", loggedInUser);
        assertTrue("User should be a student", loggedInUser instanceof Student);
        assertEquals("Barney Calhoun", loggedInUser.getName());
    }

    @Test
    public void testLoginFailureUnknownUser() {
        User loggedInUser = authService.login("WALLACE_BREEN");
        assertNull("Login should fail and return null for non-existent users", loggedInUser);
    }

    // ==========================================
    // 2. ADMIN SERVICE TESTS
    // ==========================================
    @Test
    public void testAdminCreatesCourseSuccessfully() {
        int initialSize = adminService.getAllCourses().size();

        boolean success = adminService.createNewCourse("PHYS101", "Physics I", 4);
        assertTrue("Course creation should return true", success);

        assertEquals("Course list size should increase by 1", initialSize + 1, adminService.getAllCourses().size());

        boolean found = false;
        for (Course c : adminService.getAllCourses()) {
            if (c.getCourseCode().equals("MATH101")) {
                found = true;
                break;
            }
        }
        assertTrue("PHYS101 should be found in the catalog", found);
    }

    @Test
    public void testAdminCannotCreateDuplicateCourse() {
        // Using PHYS101 here to absolutely prevent collision with the test above
        adminService.createNewCourse("PHYS101", "Physics I", 4);
        boolean success = adminService.createNewCourse("PHYS101", "Fake Physics", 3);
        assertFalse("Should not allow duplicate course codes", success);
    }

    @Test
    public void testAdminAssignsProfessor() {
        boolean success = adminService.assignProfessorToOffering("CS101-01", "P99");
        assertTrue(success);
        assertTrue("Dr. Freeman should have the course in their curated list",
                drFreeman.getCuratedCourses().contains(cs101Offering));
    }

    // ==========================================
    // 3. STUDENT SERVICE TESTS (The core rules)
    // ==========================================
    @Test
    public void testEnrollmentSuccess() {
        String result = studentService.enrollInCourse(johnbarneyCalhoun, "CS101-01");
        assertEquals("Success", result);
        assertEquals(1, johnbarneyCalhoun.getTranscript().getActiveEnrollments().size());
    }

    @Test
    public void testEnrollmentFailsCapacity() {
        // Create and enroll a dummy student to fill the only seat
        Student dummy = new Student("D01", "D01", "Isaac Kleiner");
        userRepo.save(dummy);
        String dummyResult = studentService.enrollInCourse(dummy, "CS101-01");
        assertEquals("Dummy student should successfully take the only seat", "Success", dummyResult);

        // Try to enroll Barney (Should fail)
        String result = studentService.enrollInCourse(johnbarneyCalhoun, "CS101-01");
        assertNotEquals("Enrollment should fail for Barney because it is full", "Success", result);
        assertEquals("Barney's transcript should remain empty", 0,
                johnbarneyCalhoun.getTranscript().getActiveEnrollments().size());
    }

    @Test
    public void testEnrollmentFailsScheduleConflict() {
        // Enroll in CS101 (MON/WED 10-12)
        studentService.enrollInCourse(johnbarneyCalhoun, "CS101-01");

        // Create a conflicting course (MON/WED 11-13)
        Course dummyCourse = new Course("DUMMY", "Hazard Course", 3);
        TimeSlot conflictTime = new TimeSlot("MON/WED", 11, 13);
        CourseOffering conflictOffering = new CourseOffering("DUMMY-01", dummyCourse, "FALL2026", 30, conflictTime);
        offeringRepo.save(conflictOffering);

        // Try to enroll
        String result = studentService.enrollInCourse(johnbarneyCalhoun, "DUMMY-01");
        assertNotEquals("Should fail due to overlapping times", "Success", result);
    }

    @Test
    public void testEnrollmentFailsMissingPrerequisite() {
        // Try to enroll in CS201 without taking CS101
        String result = studentService.enrollInCourse(johnbarneyCalhoun, "CS201-01");
        assertNotEquals("Should fail due to missing prerequisite", "Success", result);
    }

    @Test
    public void testStudentDropsCourse() {
        studentService.enrollInCourse(johnbarneyCalhoun, "CS101-01");
        String result = studentService.dropCourse(johnbarneyCalhoun, "CS101-01");

        assertEquals("Success", result);
        assertEquals("Transcript should be empty", 0, johnbarneyCalhoun.getTranscript().getActiveEnrollments().size());
        assertEquals("Roster should be empty", 0, cs101Offering.getEnrolledStudents().size());
    }

    // ==========================================
    // 4. PROFESSOR SERVICE TESTS
    // ==========================================
    @Test
    public void testProfessorGradesStudent() {
        studentService.enrollInCourse(johnbarneyCalhoun, "CS101-01");
        adminService.assignProfessorToOffering("CS101-01", "P99");

        List<Enrollment> roster = profService.getRoster("CS101-01");
        Enrollment studentEnrollment = roster.get(0);
        profService.assignGrade(studentEnrollment, "A");

        assertTrue(studentEnrollment.isCompleted());
        assertEquals("A", studentEnrollment.getGrade());
        assertEquals("Should move to past records", 1, johnbarneyCalhoun.getTranscript().getPastRecords().size());
        assertEquals("Active enrollments should be 0", 0,
                johnbarneyCalhoun.getTranscript().getActiveEnrollments().size());
    }
}