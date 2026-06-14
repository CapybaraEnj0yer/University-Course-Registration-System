package com.university.repository;

import com.university.model.Course;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCourseRepository implements CourseRepository {
    private Map<String, Course> database = new HashMap<>();

    public InMemoryCourseRepository() {
        // Seed initial courses
        save(new Course("CS101", "Intro to Programming", 3));
        save(new Course("CS201", "Data Structures", 4));
        save(new Course("MATH101", "Calculus I", 4));
    }

    @Override
    public Course findByCode(String courseCode) {
        return database.get(courseCode);
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public void save(Course course) {
        database.put(course.getCourseCode(), course);
    }
}