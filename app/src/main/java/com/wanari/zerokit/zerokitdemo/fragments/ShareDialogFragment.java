package com.wanari.zerokit.zerokitdemo.fragments;

import com.tresorit.zerokit.PasswordEditText;
import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.adapters.UsersAutoCompleteAdapter;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.rest.APIManager;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveShareJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserJson;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShareDialogFragment extends DialogFragment {

    private static final String ARG_TERSORID = "tresorId";

    private AutoCompleteTextView userAutoCompleteTextView;

    private FrameLayout progressContainer;

    private PasswordEditText.PasswordExporter mPasswordExporter;

    private Button cancelBtn;

    private Button createBtn;

    private String tresorId;

    private UserJson selectedUser;

    public static ShareDialogFragment newInstance(String tresorId) {

        Bundle args = new Bundle();

        ShareDialogFragment fragment = new ShareDialogFragment();
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

        View view = inflater.inflate(R.layout.dialog_share, container);
        userAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.userAutocompleteText);

        progressContainer = (FrameLayout) view.findViewById(R.id.progressBarContainer);

        cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        createBtn = (Button) view.findViewById(R.id.createInvitationBtn);

        initListeners();

        initData();

        userAutoCompleteTextView.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle(getString(R.string.share));
        return view;
    }

    private void initData() {
        APIManager.getInstance().getService().getUsers().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.functions.Action1<List<UserJson>>() {
            @Override
            public void call(List<UserJson> users) {
                final UsersAutoCompleteAdapter adapter = new UsersAutoCompleteAdapter(getContext(), users);
                userAutoCompleteTextView.setAdapter(adapter);
                userAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedUser = (UserJson) adapterView.getItemAtPosition(i);
                    }
                });
            }
        }, new rx.functions.Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showMessage(throwable.getMessage());
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

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInput();
            }
        });
    }

    private void validateInput() {
        if (selectedUser != null) {
            shareTable();
        } else {
            Toast.makeText(getContext(), getString(R.string.alert_empty), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareTable() {
        showProgress();
        ZerokitManager.getInstance().getZerokit().shareTresor(tresorId, selectedUser.getUserId()).subscribe(new Action1<String>() {
            @Override
            public void call(String operationId) {
                APIManager.getInstance().getService().approveShare(new ApproveShareJson(operationId)).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.functions.Action1<ResponseBody>() {
                    @Override
                    public void call(ResponseBody responseBody) {
                        showMessage(getString(R.string.share_success));
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