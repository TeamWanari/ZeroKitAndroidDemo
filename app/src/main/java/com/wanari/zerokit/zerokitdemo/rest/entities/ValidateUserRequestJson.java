package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ValidateUserRequestJson {

    @SerializedName("UserId")
    private String userId;

    @SerializedName("UserName")
    private String username;

    @SerializedName("RegSessionId")
    private String regSessionId;

    @SerializedName("RegValidationVerifier")
    private String regValidationVerifier;

    public ValidateUserRequestJson(String userId, String username, String regSessionId, String regValidationVerifier) {
        this.userId = userId;
        this.username = username;
        this.regSessionId = regSessionId;
        this.regValidationVerifier = regValidationVerifier;
    }
}
