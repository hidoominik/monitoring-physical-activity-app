package com.example.phisicalactivitymonitoringapp.workouts;

public class Workout {

    private String name;

    private String date;

    private String startTime;

    private String endTime;

    private String username;

    public Workout() {
    }

    public Workout(String name, String date, String startTime, String endTime, String username) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
