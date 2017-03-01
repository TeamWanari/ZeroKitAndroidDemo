package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class UserJson {

    @SerializedName("UserId")
    private String userId;

    @SerializedName("UserName")
    private String userName;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
