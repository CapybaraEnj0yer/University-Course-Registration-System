package com.university.repository;

import com.university.model.Enrollment;
import java.util.ArrayList;
import java.util.List;

public class InMemoryEnrollmentRepository implements EnrollmentRepository {
    private List<Enrollment> database = new ArrayList<>();

    @Override
    public List<Enrollment> findByOfferingId(String offeringId) {
        List<Enrollment> roster = new ArrayList<>();
        for (Enrollment e : database) {
            if (e.getCourseOffering().getOfferingId().equals(offeringId)) {
                roster.add(e);
            }
        }
        return roster;
    }

    @Override
    public List<Enrollment> findByStudentId(String studentId) {
        List<Enrollment> history = new ArrayList<>();
        for (Enrollment e : database) {
            if (e.getStudent().getId().equals(studentId)) {
                history.add(e);
            }
        }
        return history;
    }

    @Override
    public void save(Enrollment enrollment) {
        if (!database.contains(enrollment)) {
            database.add(enrollment);
        }
    }

    @Override
    public void delete(Enrollment enrollment) {
        database.remove(enrollment);
    }
}