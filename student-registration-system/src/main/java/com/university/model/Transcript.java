package com.university.model;

import java.util.ArrayList;
import java.util.List;

public class Transcript {
    private Student owner;
    private List<Enrollment> pastRecords;
    private List<Enrollment> activeEnrollments;

    public Transcript(Student owner) {
        this.owner = owner;
        this.pastRecords = new ArrayList<>();
        this.activeEnrollments = new ArrayList<>();
    }

    public void addActiveEnrollment(Enrollment enrollment) {
        if (!activeEnrollments.contains(enrollment)) {
            activeEnrollments.add(enrollment);
        }
    }

    public void completeEnrollment(Enrollment enrollment, String finalGrade) {
        if (activeEnrollments.remove(enrollment)) {
            enrollment.setGrade(finalGrade);
            pastRecords.add(enrollment);
        }
    }

    public double calculateCumulativeGPA() {
        if (pastRecords.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Enrollment e : pastRecords) {
            int credits = e.getCourseOffering().getCourse().getCredits();
            totalCredits += credits;
            totalPoints += getGradePoints(e.getGrade()) * credits;
        }

        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    private double getGradePoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A":
                return 4.0;
            case "B":
                return 3.0;
            case "C":
                return 2.0;
            case "D":
                return 1.0;
            case "F":
                return 0.0;
            default:
                return 0.0; // Unrecognized grades or "In Progress" do not award points
        }
    }

    // Getters and Setters
    public Student getOwner() {
        return owner;
    }

    public void setOwner(Student owner) {
        this.owner = owner;
    }

    public List<Enrollment> getPastRecords() {
        return pastRecords;
    }

    public void setPastRecords(List<Enrollment> pastRecords) {
        this.pastRecords = pastRecords;
    }

    public List<Enrollment> getActiveEnrollments() {
        return activeEnrollments;
    }

    public void setActiveEnrollments(List<Enrollment> activeEnrollments) {
        this.activeEnrollments = activeEnrollments;
    }
}