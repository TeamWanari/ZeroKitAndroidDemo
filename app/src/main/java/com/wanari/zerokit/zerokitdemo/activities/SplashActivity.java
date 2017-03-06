package com.wanari.zerokit.zerokitdemo.activities;

import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.common.AppConf;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.entities.LoginData;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        LoginData loginData = AppConf.getLoginData();
        if (loginData == null) {
            startSignIn();
        } else {
            ZerokitManager.getInstance().getZerokit().login(loginData.getUserId()).subscribe(new Action1<String>() {
                @Override
                public void call(String userId) {
                    Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    if (getIntent() != null) {
                        mainIntent.setData(getIntent().getData());
                    }
                    startActivity(mainIntent);
                    finish();
                }
            }, new Action1<ResponseZerokitError>() {
                @Override
                public void call(ResponseZerokitError responseZerokitError) {
                    startSignIn();
                }
            });
        }
    }

    private void startSignIn() {
        Intent signInIntent = new Intent(SplashActivity.this, SignInActivity.class);
        if (getIntent() != null) {
            // in case of deeplink from invitation
            signInIntent.setData(getIntent().getData());
        }
        startActivity(signInIntent);
        finish();
    }
}
