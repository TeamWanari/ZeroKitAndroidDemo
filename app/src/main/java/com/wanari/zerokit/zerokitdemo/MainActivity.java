package com.wanari.zerokit.zerokitdemo;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.fragments.TodoDetailFragment;
import com.wanari.zerokit.zerokitdemo.fragments.TodoListFragment;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements IMain {

    private FloatingActionButton mAddTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddTodo = (FloatingActionButton) findViewById(R.id.addTodo);
        mAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTodoDetailFragment(null);
            }
        });
        initLayout();
    }

    private void initLayout() {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainFragmentContainer, TodoListFragment.newInstance(1))
                .addToBackStack(TodoListFragment.class.getName()).commit();
    }

    @Override
    public void saveFinished() {
        removeTopFragment();
    }

    @Override
    public void todoItemSelected(Todo item) {
        openTodoDetailFragment(item);
    }

    @Override
    public void todoItemDelete(Todo item) {
        FireBaseHelper.getInstance().deleteTodo(item, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    private void openTodoDetailFragment(@Nullable Todo todo) {
        mAddTodo.hide();
        getSupportFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, TodoDetailFragment.newInstance(todo))
                .addToBackStack(TodoDetailFragment.class.getName()).commit();
    }

    @Override
    public void onBackPressed() {
        if (!removeTopFragment()) {
            super.onBackPressed();
        }
    }

    private boolean removeTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            mAddTodo.show();
            getSupportFragmentManager().popBackStack();
            return true;
        } else {
            return false;
        }
    }
}
