package com.example.dell.sleepcare.Model;

import com.google.gson.annotations.SerializedName;

public class EnvDayResult {

    @SerializedName("user_env")
    private UserEnvDay userEnvDay;
    @SerializedName("stamp")
    private String stamp;

    public UserEnvDay getUserEnvDay() {
        return userEnvDay;
    }

    public void setUserEnvDay(UserEnvDay userEnvDay) {
        this.userEnvDay = userEnvDay;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }
}