package com.wanari.zerokit.zerokitdemo;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.wanari.zerokit.zerokitdemo.adapters.TodoListFragmentPagerAdapter;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.fragments.TodoDetailFragment;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IMain {

    private FloatingActionButton mAddTodo;

    private FrameLayout mFragmentContainer;

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private TodoListFragmentPagerAdapter mTodoListFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mFragmentContainer = (FrameLayout) findViewById(R.id.mainFragmentContainer);
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
        List<String> tabs = new ArrayList<>();
        tabs.add("Első");
        tabs.add("Második");
        mTodoListFragmentPagerAdapter = new TodoListFragmentPagerAdapter(getSupportFragmentManager(), tabs);
        mViewPager.setAdapter(mTodoListFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
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
        mFragmentContainer.setVisibility(View.VISIBLE);
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
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragment != null) {
            mAddTodo.show();
            mFragmentContainer.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return true;
        } else {
            return false;
        }
    }
}
