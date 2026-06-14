package com.university.model;

public class Student extends User {
    private Transcript transcript;

    public Student(String id, String username, String name) {
        super(id, username, name);
        this.transcript = new Transcript(this);
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }

    public Transcript getTranscript() {
        return transcript;
    }
}