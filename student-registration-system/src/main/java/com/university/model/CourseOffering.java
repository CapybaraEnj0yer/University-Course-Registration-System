package com.university.model;

import java.util.ArrayList;
import java.util.List;

public class CourseOffering {
    private String offeringId;
    private Course course;
    private String semester;
    private Professor professor;
    private int maxCapacity;
    private List<Student> enrolledStudents;
    private TimeSlot timeSlot;

    // Update the constructor to require it
    public CourseOffering(String offeringId, Course course, String semester, int maxCapacity, TimeSlot timeSlot) {
        this.offeringId = offeringId;
        this.course = course;
        this.semester = semester;
        this.maxCapacity = maxCapacity;
        this.timeSlot = timeSlot; // NEW
        this.enrolledStudents = new ArrayList<>();
    }

    // Add getter and setter
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public boolean isFull() {
        return enrolledStudents.size() >= maxCapacity;
    }

    public void addStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
        }
    }

    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    // Getters and Setters
    public String getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(List<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }
}