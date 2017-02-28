package com.wanari.zerokit.zerokitdemo.rest.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserResponseJson {

    @SerializedName("Users")
    private List<UserJson> users;

    public UserResponseJson(List<UserJson> users) {
        this.users = users;
    }

    public List<UserJson> getUsers() {
        return users;
    }
}
