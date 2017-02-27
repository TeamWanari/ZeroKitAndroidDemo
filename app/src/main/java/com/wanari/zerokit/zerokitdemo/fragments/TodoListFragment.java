package com.wanari.zerokit.zerokitdemo.fragments;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;
import com.wanari.zerokit.zerokitdemo.interfaces.OnListFragmentInteractionListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";

    private RecyclerView mList;

    private int mColumnCount = 1;

    private IMain mainListener;

    private TodoRecyclerViewAdapter mTodoRecyclerViewAdapter;

    public TodoListFragment() {
    }

    public static TodoListFragment newInstance(int columnCount) {
        TodoListFragment fragment = new TodoListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        mList = (RecyclerView) view.findViewById(R.id.todoList);
        getData();
        return view;
    }

    private void getData() {
        FireBaseHelper.getInstance().getTodos(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Todo> todoList = new ArrayList<>();
                for (DataSnapshot todoSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> map = (HashMap<String, String>) todoSnapshot.getValue();
                    Todo todoItem = new Todo(map);
                    todoList.add(todoItem);
                }
                if (mTodoRecyclerViewAdapter == null) {
                    initAdapter(todoList);
                } else {
                    mTodoRecyclerViewAdapter.setItems(todoList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initAdapter(List<Todo> todoList) {
        if (mColumnCount <= 1) {
            mList.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            mList.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }
        mTodoRecyclerViewAdapter = new TodoRecyclerViewAdapter(mainListener, todoList);
        mList.setAdapter(mTodoRecyclerViewAdapter);
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
}
