package com.university.repository;

import com.university.model.Course;
import java.util.List;

public interface CourseRepository {
    Course findByCode(String courseCode);

    List<Course> findAll();

    void save(Course course);
}