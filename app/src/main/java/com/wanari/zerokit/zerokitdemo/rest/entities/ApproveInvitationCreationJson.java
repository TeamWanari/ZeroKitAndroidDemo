package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ApproveInvitationCreationJson {

    @SerializedName("OperationId")
    private String operationId;

    public ApproveInvitationCreationJson(String operationId) {
        this.operationId = operationId;
    }
}
