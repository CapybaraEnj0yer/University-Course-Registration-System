package com.university.model;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseCode;
    private String title;
    private int credits;
    private List<Course> prerequisites; // NEW: The list of required courses

    public Course(String courseCode, String title, int credits) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.prerequisites = new ArrayList<>(); // Initialize the empty list
    }

    // Add these two helper methods:
    public void addPrerequisite(Course prerequisiteCourse) {
        if (!this.prerequisites.contains(prerequisiteCourse)) {
            this.prerequisites.add(prerequisiteCourse);
        }
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    // Getters and Setters
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }
}