package com.university.service;

import com.university.model.Course;
import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Student;
import com.university.model.TimeSlot;
import com.university.repository.CourseOfferingRepository;
import com.university.repository.EnrollmentRepository;

import java.util.List;

public class StudentService {
    private CourseOfferingRepository offeringRepository;
    private EnrollmentRepository enrollmentRepository;

    public StudentService(CourseOfferingRepository offeringRepository, EnrollmentRepository enrollmentRepository) {
        this.offeringRepository = offeringRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<CourseOffering> getAvailableOfferings() {
        return offeringRepository.findAll();
    }

    public String enrollInCourse(Student student, String offeringId) {
        CourseOffering offering = offeringRepository.findById(offeringId);

        if (offering == null) {
            return "Error: Course not found.";
        }

        TimeSlot newTimeSlot = offering.getTimeSlot();

        if (offering.isFull()) {
            return "Registration Failed: Course is at maximum capacity.";
        }

        for (Enrollment existing : student.getTranscript().getActiveEnrollments()) {
            // Make sure we only check conflicts for classes in the SAME semester
            if (existing.getCourseOffering().getSemester().equals(offering.getSemester())) {
                TimeSlot existingSlot = existing.getCourseOffering().getTimeSlot();

                if (existingSlot.conflictsWith(newTimeSlot)) {
                    return "Registration Failed: Schedule conflict with " +
                            existing.getCourseOffering().getOfferingId() +
                            " (" + existingSlot.toString() + ")";
                }
            }
        }

        // Also check if they already passed it (looking at past records)
        for (Enrollment past : student.getTranscript().getPastRecords()) {
            if (past.getCourseOffering().getCourse().getCourseCode().equals(offering.getCourse().getCourseCode())) {
                // If they didn't fail, don't let them retake it
                if (!past.getGrade().equals("F")) {
                    return "Registration Failed: You have already completed this course.";
                }
            }
        }

        List<Course> prereqs = offering.getCourse().getPrerequisites();
        for (Course prereq : prereqs) {
            boolean hasPassed = false;

            // Look through the student's past records
            for (Enrollment past : student.getTranscript().getPastRecords()) {
                if (past.getCourseOffering().getCourse().getCourseCode().equals(prereq.getCourseCode())) {
                    if (!past.getGrade().equals("F")) { // They took it and didn't fail
                        hasPassed = true;
                        break;
                    }
                }
            }

            if (!hasPassed) {
                return "Registration Failed: You do not meet the prerequisite (" + prereq.getCourseCode() + ").";
            }
        }

        // Create the new enrollment
        Enrollment newEnrollment = new Enrollment(student, offering);

        // Update all our data structures
        offering.addStudent(student);
        student.getTranscript().addActiveEnrollment(newEnrollment);
        enrollmentRepository.save(newEnrollment);

        return "Success";
    }

    // Add this to StudentService.java
    public String dropCourse(Student student, String offeringId) {
        Enrollment targetEnrollment = null;

        // 1. Find the active enrollment in the student's transcript
        for (Enrollment e : student.getTranscript().getActiveEnrollments()) {
            if (e.getCourseOffering().getOfferingId().equals(offeringId)) {
                targetEnrollment = e;
                break;
            }
        }

        if (targetEnrollment == null) {
            return "Error: You are not currently actively enrolled in this course.";
        }

        // 2. Remove the student from the Course Offering's roster (frees up a seat)
        targetEnrollment.getCourseOffering().removeStudent(student);

        // 3. Remove the enrollment from the Student's active transcript
        student.getTranscript().getActiveEnrollments().remove(targetEnrollment);

        // 4. Delete the record from the database
        enrollmentRepository.delete(targetEnrollment);

        return "Success";
    }
}