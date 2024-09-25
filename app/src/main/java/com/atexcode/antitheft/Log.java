
package com.atexcode.antitheft;

public class Log {
    private String event;
    private String details;
    private String time;
    public Log() {
        // Default constructor required for Firebase Realtime Database
    }

    public Log(String event, String details, String time) {
        this.event = event;
        this.details = details;
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public String getDetails() {
        return details;
    }

    public String getTime() {
        return time;
    }


}
