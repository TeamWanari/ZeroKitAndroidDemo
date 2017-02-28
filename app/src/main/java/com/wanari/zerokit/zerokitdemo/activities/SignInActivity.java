package com.wanari.zerokit.zerokitdemo.activities;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.interfaces.ISignIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;

public class SignInActivity extends AppCompatActivity implements ISignIn {

    private static final int REQ_DEFAULT = 0;

    private BottomNavigationView mBottomNavigationView;

    private ViewFlipper mViewFlipper;

    private FrameLayout mProgressContainer;

    private CoordinatorLayout mParent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomBar);
        mViewFlipper = (ViewFlipper) findViewById(R.id.signInViewFlipper);
        mParent = (CoordinatorLayout) findViewById(R.id.container);
        mProgressContainer = (FrameLayout) findViewById(R.id.progressBarContainer);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mViewFlipper.setDisplayedChild(item.getItemId() == R.id.tab_signin ? 0 : 1);
                return true;
            }
        });
    }

    @Override
    public void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showError(String message) {
        hideProgress();
        Snackbar.make(mParent, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToSignIn() {
        hideProgress();
        mViewFlipper.setDisplayedChild(0);
    }

    @Override
    public void loginSuccess() {
        hideProgress();
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }
}
