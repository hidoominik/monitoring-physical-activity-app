package com.example.phisicalactivitymonitoringapp.sleep;

public class Sleep {

    private String key;

    private String date;

    private String startTime;

    private String endTime;

    private String username;

    public Sleep() {
    }

    public Sleep(String key, String date, String startTime, String endTime, String username) {
        this.key = key;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
