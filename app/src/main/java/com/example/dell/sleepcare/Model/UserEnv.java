package com.example.dell.sleepcare.Model;

import com.google.gson.annotations.SerializedName;

public class UserEnv {
    @SerializedName("ENV_LOUD")
    private String envLoud;
    @SerializedName("ENV_LIGHT")
    private String envLight;
    @SerializedName("ENV_TEMP")
    private String envTemp;
    @SerializedName("ENV_HUMID")
    private String envHumid;
    @SerializedName("ENV_TIME")
    private String envTime;
    @SerializedName("USER_EMAIL")
    private String userEmail;

    public String getEnvLoud() {
        return envLoud;
    }

    public void setEnvLoud(String envLoud) {
        this.envLoud = envLoud;
    }

    public String getEnvLight() {
        return envLight;
    }

    public void setEnvLight(String envLight) {
        this.envLight = envLight;
    }

    public String getEnvTemp() {
        return envTemp;
    }

    public void setEnvTemp(String envTemp) {
        this.envTemp = envTemp;
    }

    public String getEnvHumid() {
        return envHumid;
    }

    public void setEnvHumid(String envHumid) {
        this.envHumid = envHumid;
    }

    public String getEnvTime() {
        return envTime;
    }

    public void setEnvTime(String envTime) {
        this.envTime = envTime;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
