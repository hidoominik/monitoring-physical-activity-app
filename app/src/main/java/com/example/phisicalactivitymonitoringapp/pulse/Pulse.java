package com.example.phisicalactivitymonitoringapp.pulse;

public class Pulse {

    private String key;

    private String value;

    private String date;

    private String time;

    private String username;

    public Pulse() {
    }

    public Pulse(String key, String value, String date, String time, String username) {
        this.key = key;
        this.value = value;
        this.date = date;
        this.time = time;
        this.username = username;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
