package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ApproveInvitationAcceptionJson {

    @SerializedName("OperationId")
    private String operationId;

    public ApproveInvitationAcceptionJson(String operationId) {
        this.operationId = operationId;
    }
}
