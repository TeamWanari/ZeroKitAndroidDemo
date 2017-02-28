package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ValidateUserRequestJson {

    @SerializedName("UserId")
    private String userId;

    @SerializedName("RegSessionId")
    private String regSessionId;

    @SerializedName("RegValidationVerifier")
    private String regValidationVerifier;

    public ValidateUserRequestJson(String userId, String regSessionId, String regValidationVerifier) {
        this.userId = userId;
        this.regSessionId = regSessionId;
        this.regValidationVerifier = regValidationVerifier;
    }
}
