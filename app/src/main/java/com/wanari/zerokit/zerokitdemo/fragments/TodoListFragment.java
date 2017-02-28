package com.wanari.zerokit.zerokitdemo.fragments;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.adapters.TodoRecyclerViewAdapter;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wanari.zerokit.zerokitdemo.R.id.todoList;

public class TodoListFragment extends Fragment {

    private static final String ARG_TABLE = "Table";

    private RecyclerView mList;

    private IMain mainListener;

    private TodoRecyclerViewAdapter mTodoRecyclerViewAdapter;

    private Table selectedTable;

    public TodoListFragment() {
    }

    public static TodoListFragment newInstance(Table table) {
        TodoListFragment fragment = new TodoListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TABLE, table);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTable = getArguments().getParcelable(ARG_TABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_list, container, false);
        mList = (RecyclerView) view.findViewById(todoList);
        getData();
        return view;
    }

    private void getData() {
        FireBaseHelper.getInstance().getTodos(selectedTable.getId(), new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Todo> todoList = new ArrayList<>();
                for (DataSnapshot todoSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> map = (HashMap<String, String>) todoSnapshot.getValue();
                    Todo todoItem = new Todo(todoSnapshot.getKey(), map);
                    todoList.add(todoItem);
                }
                if (mTodoRecyclerViewAdapter == null || mList.getAdapter() == null) {
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
        mList.setLayoutManager(new LinearLayoutManager(getContext()));
        mList.setHasFixedSize(true);
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
