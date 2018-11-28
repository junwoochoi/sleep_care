package com.example.dell.sleepcare.Model;

import com.google.gson.annotations.SerializedName;

public class UserEnvDay {

    @SerializedName("ENV_DAY_SLEEPTIME")
    private String envDaySleeptime;
    @SerializedName("ENV_DAY_SNORE")
    private String envDaySnore;
    @SerializedName("ENV_DAY_MOVE")
    private String envDayMove;
    @SerializedName("ENV_DAY_TIME")
    private String envDayTime;
    @SerializedName("USER_EMAIL")
    private String userEmail;

    public String getEnvDaySleeptime() {
        return envDaySleeptime;
    }

    public void setEnvDaySleeptime(String envDaySleeptime) {
        this.envDaySleeptime = envDaySleeptime;
    }

    public String getEnvDaySnore() {
        return envDaySnore;
    }

    public void setEnvDaySnore(String envDaySnore) {
        this.envDaySnore = envDaySnore;
    }

    public String getEnvDayMove() {
        return envDayMove;
    }

    public void setEnvDayMove(String envDayMove) {
        this.envDayMove = envDayMove;
    }

    public String getEnvDayTime() {
        return envDayTime;
    }

    public void setEnvDayTime(String envDayTime) {
        this.envDayTime = envDayTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
