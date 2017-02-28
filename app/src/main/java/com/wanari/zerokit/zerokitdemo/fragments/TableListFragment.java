package com.wanari.zerokit.zerokitdemo.fragments;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.tresorit.adminapi.AdminApi;
import com.tresorit.zerokit.observer.Action1;
import com.tresorit.zerokit.response.ResponseZerokitError;
import com.wanari.zerokit.zerokitdemo.R;
import com.wanari.zerokit.zerokitdemo.adapters.TableRecyclerViewAdapter;
import com.wanari.zerokit.zerokitdemo.common.AppConf;
import com.wanari.zerokit.zerokitdemo.common.ZerokitManager;
import com.wanari.zerokit.zerokitdemo.database.FireBaseHelper;
import com.wanari.zerokit.zerokitdemo.entities.Table;
import com.wanari.zerokit.zerokitdemo.interfaces.IMain;
import com.wanari.zerokit.zerokitdemo.interfaces.ITableList;
import com.wanari.zerokit.zerokitdemo.rest.APIManager;
import com.wanari.zerokit.zerokitdemo.rest.entities.ApproveTresorCreationJson;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TableListFragment extends Fragment implements ITableList {

    private RecyclerView mTableList;

    private Button mAddNewTableBtn;

    private TableRecyclerViewAdapter mTableRecyclerViewAdapter;

    private IMain parentListener;

    public static TableListFragment newInstance() {

        Bundle args = new Bundle();

        TableListFragment fragment = new TableListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tablelist, container, false);
        mTableList = (RecyclerView) view.findViewById(R.id.tableList);
        mAddNewTableBtn = (Button) view.findViewById(R.id.aaddNewTableBtn);
        setListeners();
        getData();
        return view;
    }

    private void setListeners() {
        mAddNewTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
        final EditText newTableEdit = (EditText) getActivity().getLayoutInflater().inflate(R.layout.dialog_new_table, null);
        alertBuilder.setView(newTableEdit);
        alertBuilder.setTitle(getString(R.string.new_table));
        alertBuilder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (newTableEdit.getText() != null && newTableEdit.getText().length() > 0) {
                            createNewTable(newTableEdit.getText().toString());
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.alert_empty), Toast.LENGTH_SHORT).show();
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


    private void createNewTable(final String tableName) {
        parentListener.showProgress();
        ZerokitManager.getInstance().getZerokit().createTresor().subscribe(new Action1<String>() {
            @Override
            public void call(final String tresorId) {
                ZerokitManager.getInstance().getAdminApi().approveTresorCreation(tresorId).subscribe(new Action1<String>() {
                    @Override
                    public void call(String response) {
                        FireBaseHelper.getInstance().saveTable(new Table(tableName, tresorId));

                        parentListener.hideProgress();
                    }
                });
//                APIManager.getInstance().getService().approveTresorCreation(new ApproveTresorCreationJson(tresorId)).subscribeOn(
//                        Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new rx.functions.Action1<ApproveTresorCreationJson>() {
//                    @Override
//                    public void call(ApproveTresorCreationJson approveTresorCreationJson) {
//                        FireBaseHelper.getInstance().saveTable(new Table(tableName, approveTresorCreationJson.getTresorId()));
//                        parentListener.hideProgress();
//                    }
//                }, new rx.functions.Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e(TableListFragment.class.getName(), throwable.getMessage());
//                        parentListener.showError(throwable.getMessage());
//                    }
//                });

            }
        }, new Action1<ResponseZerokitError>() {
            @Override
            public void call(ResponseZerokitError responseZerokitError) {
                Log.e(TableListFragment.class.getName(), responseZerokitError.getMessage());
                parentListener.showError(responseZerokitError.getMessage());
            }
        });
    }

    private void getData() {
        FireBaseHelper.getInstance().getTableLists(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Table> tableNames = new ArrayList<>();
                List<Table> alreadyAddedList = AppConf.getAddedTableNames();
                for (DataSnapshot tableSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> map = (HashMap<String, String>) tableSnapshot.getValue();
                    Table table = new Table(tableSnapshot.getKey(), map);
                    if (!alreadyAddedList.contains(table)) {
                        tableNames.add(table);
                    }
                }
                initLayout(tableNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initLayout(List<Table> tableNames) {
        if (mTableRecyclerViewAdapter == null) {
            mTableRecyclerViewAdapter = new TableRecyclerViewAdapter(this, tableNames);
            mTableList.setAdapter(mTableRecyclerViewAdapter);
        } else {
            mTableRecyclerViewAdapter.setItems(tableNames);
        }
    }

    @Override
    public void tableItemSelected(Table table) {
        AppConf.putTable(table);
        mTableRecyclerViewAdapter.removeItem(table);
    }

    @Override
    public void closeTableList() {
        if (parentListener != null) {
            parentListener.closeTableList();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof IMain) {
            parentListener = (IMain) getActivity();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentListener = null;
    }
}
