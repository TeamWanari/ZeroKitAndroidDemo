package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class InitUserResponseJson {

    @SerializedName("UserId")
    private String userId;

    @SerializedName("RegSessionId")
    private String regSessionId;

    public InitUserResponseJson(String userId, String regSessionId) {
        this.userId = userId;
        this.regSessionId = regSessionId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRegSessionId() {
        return regSessionId;
    }
}
