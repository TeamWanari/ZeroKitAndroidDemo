package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ApproveShareJson {

    @SerializedName("OperationId")
    private String operationId;

    public ApproveShareJson(String operationId) {
        this.operationId = operationId;
    }
}
