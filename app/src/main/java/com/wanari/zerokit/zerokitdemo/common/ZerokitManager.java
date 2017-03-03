package com.wanari.zerokit.zerokitdemo.common;

import com.tresorit.zerokit.Zerokit;

public class ZerokitManager {

    private static ZerokitManager instance;

    private final Zerokit mZerokit;

    private ZerokitManager() {
        mZerokit = Zerokit.getInstance();
    }

    public static ZerokitManager getInstance() {
        if (instance == null) {
            instance = new ZerokitManager();
        }
        return instance;
    }

    public Zerokit getZerokit() {
        return mZerokit;
    }
}
