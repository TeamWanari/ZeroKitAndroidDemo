package com.wanari.zerokit.zerokitdemo.fragments;

import com.google.android.gms.tasks.OnSuccessListener;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
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

    private static final String ARG_TABLE_NAME = "TableName";

    private TextInputEditText mTodoDetailTitle;

    private TextInputLayout mTodoDetailTitleContainer;

    private TextInputEditText mTodoDetailDescription;

    private TextInputLayout mTodoDetailDescriptionContainer;

    private Button mTodoSaveBtn;

    private IMain mainListener;

    private Todo selectedTodo;

    private String selectedTableName;

    public TodoDetailFragment() {
    }

    public static TodoDetailFragment newInstance(Todo todo, String tableName) {
        TodoDetailFragment fragment = new TodoDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, todo);
        args.putString(ARG_TABLE_NAME, tableName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTodo = getArguments().getParcelable(ARG_TODO);
            selectedTableName = getArguments().getString(ARG_TABLE_NAME);
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
                        selectedTodo = new Todo(mTodoDetailTitle.getText().toString(), mTodoDetailDescription.getText().toString());
                    } else {
                        selectedTodo.setTitle(mTodoDetailTitle.getText().toString());
                        selectedTodo.setDescription(mTodoDetailDescription.getText().toString());
                    }
                    FireBaseHelper.getInstance()
                            .saveTodo(selectedTodo, selectedTableName,
                                    new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            if (mainListener != null) {
                                                mainListener.saveSuccess();
                                            }
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
