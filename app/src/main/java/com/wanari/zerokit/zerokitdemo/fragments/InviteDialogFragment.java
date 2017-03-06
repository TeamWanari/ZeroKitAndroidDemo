package com.wanari.zerokit.zerokitdemo.fragments;

import com.tresorit.zerokit.PasswordEditText;
import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitCreateInvitationLink;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.rest.APIManager;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveInvitationCreationJson;
import com.wanari.zerokit.zerokitdemo.utils.CommonUtils;
import com.wanari.zerokit.zerokitdemo.utils.ValidationUtils;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InviteDialogFragment extends DialogFragment {

    private static final String ARG_TERSORID = "tresorId";

    private TextInputEditText messageEditText;

    private TextInputLayout messageContainer;

    private PasswordEditText passwordEditText;

    private TextInputLayout passwordContainer;

    private FrameLayout progressContainer;

    private PasswordEditText.PasswordExporter mPasswordExporter;

    private Button cancelBtn;

    private Button createBtn;

    private String tresorId;

    public static InviteDialogFragment newInstance(String tresorId) {

        Bundle args = new Bundle();

        InviteDialogFragment fragment = new InviteDialogFragment();
        args.putString(ARG_TERSORID, tresorId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (getArguments() != null) {
            tresorId = getArguments().getString(ARG_TERSORID);
        }

        View view = inflater.inflate(R.layout.dialog_invite, container);
        mPasswordExporter = new PasswordEditText.PasswordExporter();
        messageEditText = (TextInputEditText) view.findViewById(R.id.messageEditText);
        messageContainer = (TextInputLayout) view.findViewById(R.id.messageTextInputLayout);

        passwordEditText = (PasswordEditText) view.findViewById(R.id.passwordEditText);
        passwordContainer = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);

        progressContainer = (FrameLayout) view.findViewById(R.id.progressBarContainer);

        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        createBtn = (Button) view.findViewById(R.id.createInvitationBtn);

        passwordEditText.setPasswordExporter(mPasswordExporter);

        initListeners();

        messageEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getString(R.string.invite_title));
        return view;
    }

    private void initListeners() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        if (ValidationUtils.hasText(messageContainer) && ValidationUtils.hasText(passwordContainer, mPasswordExporter)) {
            createInvitation(messageEditText.getText().toString());
        }
    }

    private void createInvitation(String message) {
        showProgress();
        ZerokitManager.getInstance().getZerokit().createInvitationLink("http://peter.wanari.com/invitation", tresorId, message, mPasswordExporter)
                .subscribe(
                        new Action1<ResponseZerokitCreateInvitationLink>() {
                            @Override
                            public void call(final ResponseZerokitCreateInvitationLink responseZerokitCreateInvitationLink) {
                                ApproveInvitationCreationJson requestJson = new ApproveInvitationCreationJson(
                                        responseZerokitCreateInvitationLink.getId());
                                APIManager.getInstance().getService().approveInvitationCreation(requestJson).observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io()).subscribe(
                                        new rx.functions.Action1<ResponseBody>() {
                                            @Override
                                            public void call(ResponseBody responseBody) {
                                                CommonUtils.copyToClipBoard("Invitation link: ", responseZerokitCreateInvitationLink.getUrl());
                                                creationSuccess();
                                            }
                                        }, new rx.functions.Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
                                                showMessage(throwable.getMessage());
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

    private void creationSuccess() {
        dismiss();
        Toast.makeText(getContext(), getString(R.string.invitation_success), Toast.LENGTH_LONG).show();
    }

    private void showMessage(String message) {
        hideProgress();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressContainer.setVisibility(View.GONE);
            }
        });
    }
}
