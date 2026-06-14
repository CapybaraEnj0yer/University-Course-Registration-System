package com.university.model;

import java.util.ArrayList;
import java.util.List;

public class Professor extends User {
    private List<CourseOffering> curatedCourses;

    public Professor(String id, String username, String name) {
        super(id, username, name);
        this.curatedCourses = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "PROFESSOR";
    }

    public void addCuratedCourse(CourseOffering offering) {
        this.curatedCourses.add(offering);
        offering.setProfessor(this);
    }

    // Getters and Setters
    public List<CourseOffering> getCuratedCourses() {
        return curatedCourses;
    }

    public void setCuratedCourses(List<CourseOffering> curatedCourses) {
        this.curatedCourses = curatedCourses;
    }
}