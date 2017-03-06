package com.wanari.zerokit.zerokitdemo.entities;

public class LoginData {

    private String username;

    private String userId;

    public LoginData(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }
}
