package com.wanari.zerokit.zerokitdemo.activities;

import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import static com.wanari.zerokit.zerokitdemo.R.id.userId;

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
                            mainIntent.putExtra(MainActivity.BUNDLE_USERID, userId);
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
