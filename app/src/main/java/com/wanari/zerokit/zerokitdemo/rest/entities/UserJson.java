package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class UserJson {

    @SerializedName("UserId")
    private String userId;

    @SerializedName("UserRegistrationState")
    private String registrationState;

    public String getUserId() {
        return userId;
    }

    public String getRegistrationState() {
        return registrationState;
    }
}
