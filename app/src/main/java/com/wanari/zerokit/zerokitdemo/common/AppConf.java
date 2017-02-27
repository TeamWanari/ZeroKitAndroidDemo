package com.wanari.zerokit.zerokitdemo.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.wanari.zerokit.zerokitdemo.ZerokitApplication;
import com.wanari.zerokit.zerokitdemo.entities.Table;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AppConf {

    private static final String KEY_TABLE_NAMES = "KEY_TABLE_NAMES";

    private static SharedPreferences sharedPreferences;

    private static void init() {
        if (sharedPreferences == null) {
            sharedPreferences = ZerokitApplication.getInstance().getBaseContext()
                    .getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        }
    }

    public static void putUserId(String alias, String userId) {
        init();
        sharedPreferences.edit().putString(alias, userId).apply();
    }

    public static
    @Nullable
    String getUserId(String alias) {
        init();
        return sharedPreferences.getString(alias, null);
    }

    public static void putTable(Table table) {
        init();
        List<Table> tableNames = getAddedTableNames();
        if (!tableNames.contains(table)) {
            tableNames.add(table);
        }
        String tableNamesString = new Gson().toJson(tableNames, new TypeToken<List<Table>>() {
        }.getType());
        sharedPreferences.edit().putString(KEY_TABLE_NAMES, tableNamesString).apply();
    }

    public static List<Table> getAddedTableNames() {
        init();
        List<Table> tableNames;
        tableNames = new Gson().fromJson(sharedPreferences.getString(KEY_TABLE_NAMES, ""), new TypeToken<List<Table>>() {
        }.getType());
        if (tableNames == null) {
            tableNames = new ArrayList<>();
        }
        return tableNames;
    }
}
