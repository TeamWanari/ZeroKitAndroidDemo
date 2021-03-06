package com.wanari.zerokit.zerokitdemo.fragments;

import com.tresorit.zerokit.PasswordEditText;
import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.tresorit.zerokit.response.ResponseZerokitRegister;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.common.AppConf;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.entities.LoginData;
import com.wanari.zerokit.zerokitdemo.interfaces.ISignIn;
import com.wanari.zerokit.zerokitdemo.rest.APIManager;
import com.wanari.zerokit.zerokitdemo.rest.entities.InitUserResponseJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ValidateUserRequestJson;
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

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignUpFragment extends Fragment implements TextWatcher, View.OnFocusChangeListener {

    private TextInputEditText usernameEditText;

    private TextInputLayout usernameContainer;

    private PasswordEditText passwordEditText;

    private TextInputLayout passwordContainer;

    private PasswordEditText passwordConfirmEditText;

    private TextInputLayout passwordConfirmContainer;

    private Button signUpBtn;

    private ISignIn parentListener;

    private PasswordEditText.PasswordExporter mPasswordExporter;

    private PasswordEditText.PasswordExporter mPasswordConfirmExporter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        mPasswordExporter = new PasswordEditText.PasswordExporter();
        mPasswordConfirmExporter = new PasswordEditText.PasswordExporter();

        usernameEditText = (TextInputEditText) view.findViewById(R.id.usernameEditText);
        usernameContainer = (TextInputLayout) view.findViewById(R.id.usernameTextInputLayout);

        passwordEditText = (PasswordEditText) view.findViewById(R.id.passwordEditText);
        passwordContainer = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);

        passwordConfirmEditText = (PasswordEditText) view.findViewById(R.id.passwordConfirmEditText);
        passwordConfirmContainer = (TextInputLayout) view.findViewById(R.id.passwordConfirmTextInputLayout);

        signUpBtn = (Button) view.findViewById(R.id.signUpBtn);
        setListeners();
        return view;
    }

    private void setListeners() {
        usernameEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        passwordConfirmEditText.addTextChangedListener(this);
        usernameEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        passwordConfirmEditText.setOnFocusChangeListener(this);

        passwordEditText.setPasswordExporter(mPasswordExporter);
        passwordConfirmEditText.setPasswordExporter(mPasswordConfirmExporter);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        if (ValidationUtils.hasText(usernameContainer) && ValidationUtils.hasText(passwordContainer, mPasswordExporter) && ValidationUtils
                .hasText(passwordConfirmContainer, mPasswordConfirmExporter)) {
            if (mPasswordExporter.isContentEqual(mPasswordConfirmExporter)) {
                final String alias = usernameEditText.getText().toString();
                showProgress();
                APIManager.getInstance().getService().getUserIdByUserName(alias).observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io()).subscribe(
                        new rx.functions.Action1<UserJson>() {
                            @Override
                            public void call(UserJson user) {
                                showError(getString(R.string.alert_already_registered));
                            }
                        }, new rx.functions.Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                APIManager.getInstance().getService().initUserRegistration().subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.functions.Action1<InitUserResponseJson>() {
                                    @Override
                                    public void call(final InitUserResponseJson initUserResponseJson) {
                                        ZerokitManager.getInstance().getZerokit()
                                                .register(initUserResponseJson.getUserId(), initUserResponseJson.getRegSessionId(), mPasswordExporter)
                                                .subscribe(new Action1<ResponseZerokitRegister>() {
                                                    @Override
                                                    public void call(ResponseZerokitRegister responseZerokitRegister) {
                                                        ValidateUserRequestJson requestJson = new ValidateUserRequestJson(initUserResponseJson.getUserId(),
                                                                alias,
                                                                initUserResponseJson.getRegSessionId(), responseZerokitRegister.getRegValidationVerifier());

                                                        APIManager.getInstance().getService().validateUserRegistration(requestJson)
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new rx.functions.Action1<ResponseBody>() {
                                                                    @Override
                                                                    public void call(ResponseBody response) {
                                                                        AppConf.storeLoginData(new LoginData(alias, initUserResponseJson.getUserId()));
                                                                        mPasswordExporter.clear();
                                                                        mPasswordConfirmExporter.clear();
                                                                        registrationSuccess();
                                                                    }
                                                                }, new rx.functions.Action1<Throwable>() {
                                                                    @Override
                                                                    public void call(Throwable throwable) {
                                                                        showError(throwable.getMessage());
                                                                    }
                                                                });
                                                    }
                                                }, new Action1<ResponseZerokitError>() {
                                                    @Override
                                                    public void call(ResponseZerokitError responseZerokitError) {
                                                        showError(responseZerokitError.getMessage());
                                                    }
                                                });
                                    }
                                }, new rx.functions.Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        showError(throwable.getMessage());
                                    }
                                });
                            }
                        });
            } else {
                showError(getString(R.string.alert_not_identical));
            }
        }
    }

    private void showError(String error) {
        if (parentListener != null) {
            parentListener.showError(error);
        }
    }

    private void registrationSuccess() {
        if (parentListener != null) {
            parentListener.navigateToSignIn();
        }
    }

    private void showProgress() {
        if (parentListener != null) {
            parentListener.showProgress();
        }
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
        passwordConfirmContainer.setError(null);
    }
}
