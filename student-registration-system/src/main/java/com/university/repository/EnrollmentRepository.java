package com.university.repository;

import com.university.model.Enrollment;
import java.util.List;

public interface EnrollmentRepository {
    List<Enrollment> findByOfferingId(String offeringId);

    List<Enrollment> findByStudentId(String studentId);

    void save(Enrollment enrollment);

    void delete(Enrollment enrollment);
}