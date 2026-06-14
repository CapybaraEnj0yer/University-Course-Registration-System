package com.university.model;

public class TimeSlot {
    private String days; // "MON/WED", "TUE/THU", "FRI"
    private int startHour; // 0-23
    private int endHour; // 0-23

    public TimeSlot(String days, int startHour, int endHour) {
        this.days = days;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public boolean conflictsWith(TimeSlot other) {
        // If they meet on completely different days, no conflict
        if (!this.days.equals(other.days) && !this.days.contains(other.days) && !other.days.contains(this.days)) {
            return false;
        }

        // If days overlap, check if the times overlap
        // A conflict occurs if one starts before the other ends, AND ends after the
        // other starts
        return (this.startHour < other.endHour) && (this.endHour > other.startHour);
    }

    @Override
    public String toString() {
        return String.format("%s %02d:00-%02d:00", days, startHour, endHour);
    }

    // Getters
    public String getDays() {
        return days;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getEndHour() {
        return endHour;
    }
}