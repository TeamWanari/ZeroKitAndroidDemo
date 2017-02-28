package com.wanari.zerokit.zerokitdemo.fragments;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;
import com.wanari.zerokit.zerokitdemo.utils.ValidationUtils;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class TodoDetailFragment extends Fragment {

    private static final String ARG_TODO = "todo";

    private static final String ARG_TABLE = "Table";

    private TextInputEditText mTodoDetailTitle;

    private TextInputLayout mTodoDetailTitleContainer;

    private TextInputEditText mTodoDetailDescription;

    private TextInputLayout mTodoDetailDescriptionContainer;

    private Button mTodoSaveBtn;

    private IMain mainListener;

    private Todo selectedTodo;

    private Table selectedTable;

    public TodoDetailFragment() {
    }

    public static TodoDetailFragment newInstance(Todo todo, Table table) {
        TodoDetailFragment fragment = new TodoDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, todo);
        args.putParcelable(ARG_TABLE, table);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTodo = getArguments().getParcelable(ARG_TODO);
            selectedTable = getArguments().getParcelable(ARG_TABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_todo_detail, container, false);
        mTodoDetailTitle = (TextInputEditText) view.findViewById(R.id.todoDetailTitle);
        mTodoDetailTitleContainer = (TextInputLayout) view.findViewById(R.id.todoDetailTitleContainer);
        mTodoDetailDescription = (TextInputEditText) view.findViewById(R.id.todoDetailDescription);
        mTodoDetailDescriptionContainer = (TextInputLayout) view.findViewById(R.id.todoDetailDescriptionContainer);

        if (selectedTodo != null) {
            mTodoDetailTitle.setText(selectedTodo.getTitle());
            mTodoDetailDescription.setText(selectedTodo.getDescription());
        }

        mTodoSaveBtn = (Button) view.findViewById(R.id.todoDetailSave);
        mTodoSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                if (ValidationUtils.hasText(mTodoDetailTitleContainer) && ValidationUtils.hasText(mTodoDetailDescriptionContainer)) {
                    if (selectedTodo == null) {
                        selectedTodo = new Todo();
                    }
                    selectedTodo.setTitle(mTodoDetailTitle.getText().toString());
                    selectedTodo.setDescription(mTodoDetailDescription.getText().toString());

                    mainListener.showProgress();
                    String todoString = new Gson().toJson(selectedTodo);
                    ZerokitManager.getInstance().getZerokit().encrypt(selectedTable.getTresorId(), todoString).subscribe(
                            new Action1<String>() {
                                @Override
                                public void call(String encryptedTodo) {
                                    FireBaseHelper.getInstance()
                                            .saveTodo(selectedTodo.getId(), encryptedTodo, selectedTable.getId(),
                                                    new OnSuccessListener() {
                                                        @Override
                                                        public void onSuccess(Object o) {
                                                            if (mainListener != null) {
                                                                mainListener.saveSuccess();
                                                            }
                                                        }
                                                    });
                                }
                            }, new Action1<ResponseZerokitError>() {
                                @Override
                                public void call(ResponseZerokitError responseZerokitError) {
                                    mainListener.showError(responseZerokitError.getMessage());
                                }
                            });

                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof IMain) {
            mainListener = ((IMain) getActivity());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainListener = null;
    }

    private void closeKeyboard() {
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
