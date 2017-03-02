package com.wanari.zerokit.zerokitdemo.utils;

import com.tresorit.zerokit.PasswordEditText;
import com.wanari.zerokit.zerokitdemo.R;

import android.support.design.widget.TextInputLayout;
import android.widget.EditText;


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

    public static boolean hasText(TextInputLayout textInputLayout, PasswordEditText.PasswordExporter passwordExporter) {
        textInputLayout.setError(null);

        if (passwordExporter.isEmpty()) {
            String msg = textInputLayout.getResources().getString(
                    R.string.alert_empty);
            textInputLayout.setError(msg);
            return false;
        }
        return true;
    }

    public static boolean hasText(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            String msg = editText.getResources().getString(
                    R.string.alert_empty);
            editText.setError(msg);
            return false;
        }

        return true;
    }
}
