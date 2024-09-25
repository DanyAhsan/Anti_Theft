package com.atexcode.antitheft;

public class DeviceInfo {

    private String email;
    private int status;
    private String location;

    // Default constructor with default values
    public DeviceInfo() {

    }
    public DeviceInfo(String e, String l, int s) {

        this.email = e;
        this.location = l;
        this.status = s;

    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int stolen) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail(){ return email;}

}
