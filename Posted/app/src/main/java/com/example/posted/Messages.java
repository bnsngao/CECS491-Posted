package com.example.posted;

public class Messages {
    // Strings to hold database data
    public String message, time, date, from;

    public Messages() {
        // default constructor
    }

    public Messages(String message, String time, String date, String from) {
        this.message = message;
        this.time = time;
        this.date = date;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
