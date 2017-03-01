package com.wanari.zerokit.zerokitdemo.activities;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.tresorit.zerokit.PasswordEditText;
import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitCreateInvitationLink;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.adapters.TodoListFragmentPagerAdapter;
import com.wanari.zerokit.zerokitdemo.adapters.UsersAutoCompleteAdapter;
import com.wanari.zerokit.zerokitdemo.common.AppConf;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.entities.Todo;
import com.wanari.zerokit.zerokitdemo.fragments.TableListFragment;
import com.wanari.zerokit.zerokitdemo.fragments.TodoDetailFragment;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;
import com.wanari.zerokit.zerokitdemo.rest.APIManager;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveInvitationCreationJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveShareJson;
import com.wanari.zerokit.zerokitdemo.rest.entities.UserJson;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements IMain {

    public static final String BUNDLE_USERID = "userId";

    private CoordinatorLayout mMainParent;

    private FloatingActionButton mAddTodo;

    private FrameLayout mFragmentContainer;

    private TabLayout mTabLayout;

    private ViewPager mViewPager;

    private FrameLayout mProgressContainer;

    private TodoListFragmentPagerAdapter mTodoListFragmentPagerAdapter;

    private MenuItem mSearchMenuItem;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent() != null && getIntent().hasExtra(BUNDLE_USERID)) {
            userId = getIntent().getStringExtra(BUNDLE_USERID);
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
        List<Table> addedTables = AppConf.getAddedTables(userId);
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
            case R.id.copyUserId:
                copyUserIdToClipboard();
                return true;
            case R.id.share:
                showDialog();
                return true;
            case R.id.invite:
                showInvitationDialog();
                return true;
            case R.id.removeTable:
                Table tableToRemove = getCurrentTable();
                mTodoListFragmentPagerAdapter.deleteTable(tableToRemove);
                AppConf.removeTable(userId, tableToRemove);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showInvitationDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    }

    private void createInvitation(String message, PasswordEditText.PasswordExporter passwordExporter) {
        showProgress();
        ZerokitManager.getInstance().getZerokit().createInvitationLink("linkbase", getCurrentTable().getTresorId(), message, passwordExporter)
                .subscribe(
                        new Action1<ResponseZerokitCreateInvitationLink>() {
                            @Override
                            public void call(final ResponseZerokitCreateInvitationLink responseZerokitCreateInvitationLink) {
                                ApproveInvitationCreationJson requestJson = new ApproveInvitationCreationJson(
                                        responseZerokitCreateInvitationLink.getUrl());
                                APIManager.getInstance().getService().approveInvitationCreation(requestJson).observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io()).subscribe(
                                        new rx.functions.Action1<ResponseBody>() {
                                            @Override
                                            public void call(ResponseBody responseBody) {
                                                copyToClipBoard("Invitation link: ", responseZerokitCreateInvitationLink.getUrl());
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

    private void showDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) getLayoutInflater().inflate(R.layout.dialog_new_table, null);
        APIManager.getInstance().getService().getUsers().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.functions.Action1<List<UserJson>>() {
            @Override
            public void call(List<UserJson> users) {
                final UsersAutoCompleteAdapter adapter = new UsersAutoCompleteAdapter(MainActivity.this, users);
                autoCompleteTextView.setAdapter(adapter);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        UserJson userJson = (UserJson) adapterView.getItemAtPosition(i);
                        adapter.setSelectedUser(userJson);
                    }
                });
            }
        }, new rx.functions.Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showMessage(throwable.getMessage());
            }
        });
        alertBuilder.setView(autoCompleteTextView);
        alertBuilder.setTitle(getString(R.string.share));
        alertBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserJson selectedUser = ((UsersAutoCompleteAdapter) autoCompleteTextView.getAdapter()).getSelectedUser();
                        if (selectedUser != null) {
                            shareTable(selectedUser.getUserId());
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.alert_empty), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        alertBuilder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alertBuilder.create().show();
    }

    private void shareTable(String userIdToShare) {
        showProgress();
        ZerokitManager.getInstance().getZerokit().shareTresor(getCurrentTable().getTresorId(), userIdToShare).subscribe(new Action1<String>() {
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

    private void copyUserIdToClipboard() {
        ZerokitManager.getInstance().getZerokit().whoAmI().subscribe(new Action1<String>() {
            @Override
            public void call(String userId) {
                copyToClipBoard("User id:", userId);
                showMessage("Copied User Id: " + userId);
            }
        });
    }

    private void copyToClipBoard(String label, String toCopy) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, toCopy);
        clipboard.setPrimaryClip(clip);
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
        openFragment(TableListFragment.newInstance(userId), TableListFragment.class.getName());
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
