package com.wanari.zerokit.zerokitdemo;

import com.tresorit.zerokit.observer.Action1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int REQ_DEFAULT = 0;

    private boolean start = true;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        start = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!start) finish();
        else {
            start = false;

            ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
                @Override
                public void call(String result) {
                    if ("null".equals(result))
                        startActivityForResult(new Intent(SplashActivity.this, SignInActivity.class), REQ_DEFAULT);
                    else
                        startActivityForResult(new Intent(SplashActivity.this, MainActivity.class), REQ_DEFAULT);
                }
            });
        }
    }
}
