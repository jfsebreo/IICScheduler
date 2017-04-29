package com.utilities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jace on 11/21/2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Schedule {

    private String email;
    private String time;
    private String day;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
