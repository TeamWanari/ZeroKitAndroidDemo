package com.wanari.zerokit.zerokitdemo.activities;

import com.tresorit.zerokit.observer.Action1;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(String result) {
                if ("null".equals(result)) {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
            }
        });
    }
}
