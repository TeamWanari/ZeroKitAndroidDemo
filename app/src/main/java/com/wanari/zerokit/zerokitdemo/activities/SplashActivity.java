package com.wanari.zerokit.zerokitdemo.activities;

import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static android.R.attr.data;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(String result) {
                if ("null".equals(result)) {
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                } else {
                    ZerokitManager.getInstance().getZerokit().login(result).subscribe(new Action1<String>() {
                        @Override
                        public void call(String userId) {
                            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        }
                    }, new Action1<ResponseZerokitError>() {
                        @Override
                        public void call(ResponseZerokitError responseZerokitError) {
                            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                            finish();
                        }
                    });
                }
            }
        });
    }
}
