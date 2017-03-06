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
import com.wanari.zerokit.zerokitdemo.fragments.AcceptInvitationDialogFragment;
import com.wanari.zerokit.zerokitdemo.fragments.InviteDialogFragment;
import com.wanari.zerokit.zerokitdemo.fragments.ShareDialogFragment;
import com.wanari.zerokit.zerokitdemo.fragments.TableListFragment;
import com.wanari.zerokit.zerokitdemo.fragments.TodoDetailFragment;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;
import com.wanari.zerokit.zerokitdemo.utils.CommonUtils;

import android.content.Intent;
import android.net.Uri;
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

    private Menu mMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null) {
            Uri data = getIntent().getData();
            if (data != null) {
                AcceptInvitationDialogFragment acceptInvitationDialogFragment = AcceptInvitationDialogFragment.newInstance(data.toString());
                acceptInvitationDialogFragment.show(getSupportFragmentManager(), AcceptInvitationDialogFragment.class.getName());
            }
        }

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
        initLayout();
    }

    private void initLayout() {
        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(String userId) {
                final List<Table> addedTables = AppConf.getAddedTables(userId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                });
            }
        });
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
        mMainMenu = menu;
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
            case R.id.copyUserId:
                copyUserIdToClipboard();
                return true;
            case R.id.share:
                showShareDialog();
                return true;
            case R.id.invite:
                showInvitationDialog();
                return true;
            case R.id.removeTable:
                removeCurrentTable();
                return true;
            case R.id.refreshTable:
                mTodoListFragmentPagerAdapter.getCurrentFragment().refresList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeCurrentTable() {
        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(String userId) {
                final Table tableToRemove = getCurrentTable();
                if (tableToRemove != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTodoListFragmentPagerAdapter.deleteTable(tableToRemove);
                        }
                    });
                    if (AppConf.removeTable(userId, tableToRemove) == 0) {
                        initLayout();
                    }
                } else {
                    showMessage(getString(R.string.alert_no_table_added));
                }
            }
        });
    }

    private void showInvitationDialog() {
        final Table tableToInvite = getCurrentTable();
        if (tableToInvite != null) {
            InviteDialogFragment inviteDialogFragment = InviteDialogFragment.newInstance(tableToInvite.getTresorId());
            inviteDialogFragment.show(getSupportFragmentManager(), InviteDialogFragment.class.getName());
        } else {
            showMessage(getString(R.string.alert_no_table_added));
        }
    }

    private void showShareDialog() {
        final Table tableToShare = getCurrentTable();
        if (tableToShare != null) {
            ShareDialogFragment shareDialogFragment = ShareDialogFragment.newInstance(tableToShare.getTresorId());
            shareDialogFragment.show(getSupportFragmentManager(), ShareDialogFragment.class.getName());
        } else {
            showMessage(getString(R.string.alert_no_table_added));
        }
    }

    private void copyUserIdToClipboard() {
        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(String userId) {
                CommonUtils.copyToClipBoard("User id:", userId);
                showMessage("Copied User Id: " + userId);
            }
        });
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
                showMessage(responseZerokitError.getMessage());
            }
        });
    }

    @Override
    public void todoItemSelected(Todo item) {
        openTodoDetailFragment(item);
    }

    @Override
    public void todoItemDelete(Todo item) {
        final Table table = getCurrentTable();
        if (table != null) {
            FireBaseHelper.getInstance().deleteTodo(item, table.getId(), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
        } else {
            showMessage(getString(R.string.alert_no_table_added));
        }
    }

    private
    @Nullable
    Table getCurrentTable() {
        if (mTodoListFragmentPagerAdapter != null) {
            return mTodoListFragmentPagerAdapter.getTable(mViewPager.getCurrentItem());
        } else {
            return null;
        }
    }

    private void openTableList() {
        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(final String userId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openFragment(TableListFragment.newInstance(userId), TableListFragment.class.getName());
                    }
                });
            }
        });
    }

    private void openTodoDetailFragment(@Nullable Todo todo) {
        openFragment(TodoDetailFragment.newInstance(todo, getCurrentTable()),
                TodoDetailFragment.class.getName());
    }

    private void openFragment(Fragment fragment, String tag) {
        if (mMainMenu != null) {
            mMainMenu.setGroupVisible(R.id.main_menu_group, false);
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
            if (mMainMenu != null) {
                mMainMenu.setGroupVisible(R.id.main_menu_group, true);
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
    public void showMessage(String message) {
        hideProgress();
        Snackbar.make(mMainParent, message, Snackbar.LENGTH_SHORT).show();
    }

    public void logoutSuccess() {
        hideProgress();
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
        finish();
    }
}
