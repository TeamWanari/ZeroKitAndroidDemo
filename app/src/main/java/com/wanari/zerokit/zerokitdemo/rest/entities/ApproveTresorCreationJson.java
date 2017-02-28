package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

public class ApproveTresorCreationJson {

    @SerializedName("TresorId")
    private String tresorId;

    public ApproveTresorCreationJson(String tresorId) {
        this.tresorId = tresorId;
    }

    public String getTresorId() {
        return tresorId;
    }
}
