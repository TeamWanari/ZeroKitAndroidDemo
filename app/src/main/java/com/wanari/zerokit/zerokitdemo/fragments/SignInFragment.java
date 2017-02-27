package com.wanari.zerokit.zerokitdemo.fragments;

import com.tresorit.zerokit.PasswordEditText;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.interfaces.ISignIn;
import com.wanari.zerokit.zerokitdemo.utils.ValidationUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SignInFragment extends Fragment implements TextWatcher, View.OnFocusChangeListener {

    private TextInputEditText usernameEditText;

    private TextInputLayout usernameContainer;

    private PasswordEditText passwordEditText;

    private TextInputLayout passwordContainer;

    private Button signInBtn;

    private ISignIn parentListener;

    private PasswordEditText.PasswordExporter mPasswordExporter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        mPasswordExporter = new PasswordEditText.PasswordExporter();
        usernameEditText = (TextInputEditText) view.findViewById(R.id.usernameEditText);
        usernameContainer = (TextInputLayout) view.findViewById(R.id.usernameTextInputLayout);

        passwordEditText = (PasswordEditText) view.findViewById(R.id.passwordEditText);
        passwordContainer = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);

        signInBtn = (Button) view.findViewById(R.id.signInBtn);
        setListeners();
        return view;
    }

    private void setListeners() {
        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        usernameEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);

        passwordEditText.setPasswordExporter(mPasswordExporter);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        if (ValidationUtils.hasText(usernameContainer) && ValidationUtils.hasText(passwordContainer, mPasswordExporter)) {
            parentListener.showProgress();
//            TODO signin
        }
    }

    private void showError(String error) {
        parentListener.showError(error);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof ISignIn) {
            parentListener = (ISignIn) getActivity();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof ISignIn) {
            parentListener = (ISignIn) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentListener = null;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        clearErrors();
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        clearErrors();
    }

    private void clearErrors() {
        usernameContainer.setError(null);
        passwordContainer.setError(null);
    }
}
