package com.wanari.zerokit.zerokitdemo.fragments;

import com.tresorit.zerokit.PasswordEditText;
import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.tresorit.zerokit.response.ResponseZerokitInvitationLinkInfo;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.rest.APIManager;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveInvitationAcceptionJson;
import com.wanari.zerokit.zerokitdemo.utils.CommonUtils;
import com.wanari.zerokit.zerokitdemo.utils.ValidationUtils;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AcceptInvitationDialogFragment extends DialogFragment {

    private static final String ARG_INVITATION_URL = "invitationUrl";

    private TextView messageText;

    private PasswordEditText passwordEditText;

    private TextInputLayout passwordContainer;

    private FrameLayout progressContainer;

    private PasswordEditText.PasswordExporter mPasswordExporter;

    private Button cancelBtn;

    private Button acceptBtn;

    private String invitationUrl;

    private Boolean needsPass;

    public static AcceptInvitationDialogFragment newInstance(String invitationUrl) {

        Bundle args = new Bundle();
        args.putString(ARG_INVITATION_URL, invitationUrl);
        AcceptInvitationDialogFragment fragment = new AcceptInvitationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (getArguments() != null) {
            invitationUrl = getArguments().getString(ARG_INVITATION_URL);
        }
        View view = inflater.inflate(R.layout.dialog_accept, container);
        mPasswordExporter = new PasswordEditText.PasswordExporter();
        messageText = (TextView) view.findViewById(R.id.messageText);

        passwordEditText = (PasswordEditText) view.findViewById(R.id.passwordEditText);
        passwordContainer = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);

        progressContainer = (FrameLayout) view.findViewById(R.id.progressBarContainer);

        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        acceptBtn = (Button) view.findViewById(R.id.acceptInvitationBtn);

        passwordEditText.setPasswordExporter(mPasswordExporter);

        getDialog().setTitle(getString(R.string.invite_accept));

        initListeners();
        initData();

        return view;
    }

    private void initData() {
        ZerokitManager.getInstance().getZerokit().getInvitationLinkInfo(CommonUtils.trimInvitationUrl(invitationUrl))
                .subscribe(new Action1<ResponseZerokitInvitationLinkInfo>() {
                    @Override
                    public void call(final ResponseZerokitInvitationLinkInfo responseZerokitInvitationLinkInfo) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messageText.setText(responseZerokitInvitationLinkInfo.getMessage());
                                needsPass = responseZerokitInvitationLinkInfo.getPasswordProtected();
                                passwordEditText.setEnabled(needsPass);
                            }
                        });
                    }
                }, new Action1<ResponseZerokitError>() {
                    @Override
                    public void call(ResponseZerokitError responseZerokitError) {
                        showMessage(responseZerokitError.getMessage());
                    }
                });
    }

    private void initListeners() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        if (needsPass && ValidationUtils.hasText(passwordContainer, mPasswordExporter)) {
            acceptInvitation();
        } else {
            acceptInvitationWithoutPassword();
        }
    }

    private void acceptInvitationWithoutPassword() {
        showProgress();
        ZerokitManager.getInstance().getZerokit().getInvitationLinkInfo(CommonUtils.trimInvitationUrl(invitationUrl))
                .subscribe(new Action1<ResponseZerokitInvitationLinkInfo>() {
                    @Override
                    public void call(ResponseZerokitInvitationLinkInfo responseZerokitInvitationLinkInfo) {
                        ZerokitManager.getInstance().getZerokit().acceptInvitationLinkNoPassword(responseZerokitInvitationLinkInfo.getToken())
                                .subscribe(
                                        new Action1<String>() {
                                            @Override
                                            public void call(String operationId) {
                                                approveByAdmin(operationId);
                                            }
                                        }, new Action1<ResponseZerokitError>() {
                                            @Override
                                            public void call(ResponseZerokitError responseZerokitError) {
                                                showMessage(responseZerokitError.getMessage());
                                            }
                                        });
                    }
                });
    }

    private void acceptInvitation() {
        showProgress();
        ZerokitManager.getInstance().getZerokit().getInvitationLinkInfo(CommonUtils.trimInvitationUrl(invitationUrl))
                .subscribe(new Action1<ResponseZerokitInvitationLinkInfo>() {
                    @Override
                    public void call(final ResponseZerokitInvitationLinkInfo responseZerokitInvitationLinkInfo) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ZerokitManager.getInstance().getZerokit()
                                        .acceptInvitationLink(responseZerokitInvitationLinkInfo.getToken(), mPasswordExporter)
                                        .subscribe(
                                                new Action1<String>() {
                                                    @Override
                                                    public void call(String operationId) {
                                                        mPasswordExporter.clear();
                                                        approveByAdmin(operationId);
                                                    }
                                                }, new Action1<ResponseZerokitError>() {
                                                    @Override
                                                    public void call(ResponseZerokitError responseZerokitError) {
                                                        showMessage(responseZerokitError.getMessage());
                                                    }
                                                });
                            }
                        });
                    }
                }, new Action1<ResponseZerokitError>() {
                    @Override
                    public void call(ResponseZerokitError responseZerokitError) {
                        showMessage(responseZerokitError.getMessage());
                    }
                });
    }

    private void approveByAdmin(String operationId) {
        APIManager.getInstance().getService().approveInvitationAcception(new ApproveInvitationAcceptionJson(operationId))
                .observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(
                new rx.functions.Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        acceptSuccess();
                    }
                }, new rx.functions.Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showMessage(throwable.getMessage());
                    }
                });
    }

    private void acceptSuccess() {
        Toast.makeText(getContext(), getString(R.string.accept_success), Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void showMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgress();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgress() {
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressContainer.setVisibility(View.GONE);
    }
}