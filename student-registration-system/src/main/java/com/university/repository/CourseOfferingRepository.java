package com.university.repository;

import com.university.model.CourseOffering;
import java.util.List;

public interface CourseOfferingRepository {
    CourseOffering findById(String offeringId);

    List<CourseOffering> findAll();

    void save(CourseOffering offering);
}