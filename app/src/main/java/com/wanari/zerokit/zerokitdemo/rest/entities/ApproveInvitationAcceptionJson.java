package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ApproveInvitationAcceptionJson {

    @SerializedName("OperationId")
    private String operationId;

    @SerializedName("AdditionalInfo")
    private String additionalInfo;

    public ApproveInvitationAcceptionJson(String operationId, String additionalInfo) {
        this.operationId = operationId;
        this.additionalInfo = additionalInfo;
    }
}
