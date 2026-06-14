package com.university.service;

import com.university.model.CourseOffering;
import com.university.model.Enrollment;
import com.university.model.Professor;
import com.university.repository.CourseOfferingRepository;
import com.university.repository.EnrollmentRepository;

import java.util.ArrayList;
import java.util.List;

public class ProfessorService {
    private CourseOfferingRepository offeringRepository;
    private EnrollmentRepository enrollmentRepository;

    public ProfessorService(CourseOfferingRepository offeringRepository, EnrollmentRepository enrollmentRepository) {
        this.offeringRepository = offeringRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    // Find all courses assigned to this specific professor
    public List<CourseOffering> getMyOfferings(Professor professor) {
        List<CourseOffering> myCourses = new ArrayList<>();
        for (CourseOffering offering : offeringRepository.findAll()) {
            if (offering.getProfessor() != null && offering.getProfessor().getId().equals(professor.getId())) {
                myCourses.add(offering);
            }
        }
        return myCourses;
    }

    // Get the roster of students for a specific course
    public List<Enrollment> getRoster(String offeringId) {
        return enrollmentRepository.findByOfferingId(offeringId);
    }

    // Assign a grade and update the student's transcript
    public boolean assignGrade(Enrollment enrollment, String finalGrade) {
        if (enrollment == null)
            return false;

        // Use the Transcript logic we built earlier to complete the course!
        enrollment.getStudent().getTranscript().completeEnrollment(enrollment, finalGrade);
        return true;
    }
}