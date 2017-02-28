package com.wanari.zerokit.zerokitdemo.activities;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.adapters.TodoListFragmentPagerAdapter;
import com.wanari.zerokit.zerokitdemo.common.AppConf;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.fragments.TableListFragment;
import com.wanari.zerokit.zerokitdemo.fragments.TodoDetailFragment;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IMain {

    private CoordinatorLayout mMainParent;

    private FloatingActionButton mAddTodo;

    private FrameLayout mFragmentContainer;

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private FrameLayout mProgressContainer;

    private TodoListFragmentPagerAdapter mTodoListFragmentPagerAdapter;

    private MenuItem mSearchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainParent = (CoordinatorLayout) findViewById(R.id.activity_main);
        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mFragmentContainer = (FrameLayout) findViewById(R.id.mainFragmentContainer);
        mProgressContainer = (FrameLayout) findViewById(R.id.progressBarContainer);
        mAddTodo = (FloatingActionButton) findViewById(R.id.addTodo);
        mAddTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTodoDetailFragment(null);
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Table currentTable = mTodoListFragmentPagerAdapter.getTable(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initLayout();
    }

    private void initLayout() {
        List<Table> addedTables = AppConf.getAddedTableNames();
        if (addedTables.size() > 0) {
            if (mTodoListFragmentPagerAdapter == null) {
                mTodoListFragmentPagerAdapter = new TodoListFragmentPagerAdapter(getSupportFragmentManager(), addedTables);
                mViewPager.setAdapter(mTodoListFragmentPagerAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
            } else {
                mTodoListFragmentPagerAdapter.setItems(addedTables);
            }
            mAddTodo.show();
        } else {
            mAddTodo.hide();
            openTableList();
        }
    }

    @Override
    public void saveSuccess() {
        hideProgress();
        removeTopFragment();
    }

    @Override
    public void closeTableList() {
        removeTopFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.search_table);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_table:
                openTableList();
                return true;
            case R.id.sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        showProgress();
        ZerokitManager.getInstance().getZerokit().logout(true).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                logoutSuccess();
            }
        }, new Action1<ResponseZerokitError>() {
            @Override
            public void call(ResponseZerokitError responseZerokitError) {
                showError(responseZerokitError.getMessage());
            }
        });
    }

    @Override
    public void todoItemSelected(Todo item) {
        openTodoDetailFragment(item);
    }

    @Override
    public void todoItemDelete(Todo item) {
        FireBaseHelper.getInstance().deleteTodo(item, getCurrentTable().getId(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

            }
        });
    }

    private Table getCurrentTable() {
        return mTodoListFragmentPagerAdapter.getTable(mViewPager.getCurrentItem());
    }

    private void openTableList() {
        openFragment(TableListFragment.newInstance(), TableListFragment.class.getName());
    }

    private void openTodoDetailFragment(@Nullable Todo todo) {
        openFragment(TodoDetailFragment.newInstance(todo, getCurrentTable()),
                TodoDetailFragment.class.getName());
    }

    private void openFragment(Fragment fragment, String tag) {
        if (mSearchMenuItem != null) {
            mSearchMenuItem.setVisible(false);
        }
        mAddTodo.hide();
        mFragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.mainFragmentContainer, fragment)
                .addToBackStack(tag).commit();
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
            if (fragment instanceof TableListFragment) {
                initLayout();
            } else {
                mAddTodo.show();
            }
            if (mSearchMenuItem != null) {
                mSearchMenuItem.setVisible(true);
            }
            mFragmentContainer.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showError(String message) {
        hideProgress();
        Snackbar.make(mMainParent, message, Snackbar.LENGTH_SHORT).show();
    }

    public void logoutSuccess() {
        hideProgress();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }
}
