package com.wanari.zerokit.zerokitdemo;

import com.google.firebase.database.FirebaseDatabase;

import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;

import android.app.Application;

public class ZerokitApplication extends Application {

    private static ZerokitApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public static ZerokitApplication getInstance() {
        return instance;
    }
}
