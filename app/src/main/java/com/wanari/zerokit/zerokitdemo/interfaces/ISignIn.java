package com.wanari.zerokit.zerokitdemo.interfaces;

public interface ISignIn {

    void showProgress();

    void hideProgress();

    void showError(String message);

    void navigateToSignIn();

    void loginSuccess(String userId);
}
