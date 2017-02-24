package com.wanari.zerokit.zerokitdemo.utils;

import com.wanari.zerokit.zerokitdemo.R;

import android.support.design.widget.TextInputLayout;


public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static boolean hasText(TextInputLayout textInputLayout) {

        String text = textInputLayout.getEditText().getText().toString().trim();
        textInputLayout.setError(null);

        if (text.length() == 0) {
            String msg = textInputLayout.getResources().getString(
                    R.string.alert_empty);
            textInputLayout.setError(msg);
            return false;
        }

        return true;
    }
}
