package com.university.model;

public class Enrollment {
    private Student student;
    private CourseOffering courseOffering;
    private String grade;

    public Enrollment(Student student, CourseOffering courseOffering) {
        this.student = student;
        this.courseOffering = courseOffering;
        this.grade = "In Progress";
    }

    public boolean isCompleted() {
        return !this.grade.equals("In Progress");
    }

    // Getters and Setters
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseOffering getCourseOffering() {
        return courseOffering;
    }

    public void setCourseOffering(CourseOffering courseOffering) {
        this.courseOffering = courseOffering;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}