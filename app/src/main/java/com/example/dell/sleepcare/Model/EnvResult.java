package com.example.dell.sleepcare.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EnvResult {

    @SerializedName("user_env")
    private List<UserEnv> userEnv;
    @SerializedName("stamp")
    private String stamp;

    public List<UserEnv> getUserEnv() {
        return userEnv;
    }

    public void setUserEnv(List<UserEnv> userEnv) {
        this.userEnv = userEnv;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }
}