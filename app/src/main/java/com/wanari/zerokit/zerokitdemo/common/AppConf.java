package com.wanari.zerokit.zerokitdemo.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.wanari.zerokit.zerokitdemo.ZerokitApplication;
import com.wanari.zerokit.zerokitdemo.entities.Table;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AppConf {

    private static SharedPreferences sharedPreferences;

    private static void init() {
        if (sharedPreferences == null) {
            sharedPreferences = ZerokitApplication.getInstance().getBaseContext()
                    .getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        }
    }

    public static void putTable(String userId, Table table) {
        init();
        List<Table> tables = getAddedTables(userId);
        if (!tables.contains(table)) {
            tables.add(table);
        }
        storeTables(userId, tables);
    }

    private static void storeTables(String userId, List<Table> tables) {
        String tableNamesString = new Gson().toJson(tables, new TypeToken<List<Table>>() {
        }.getType());
        sharedPreferences.edit().putString(userId, tableNamesString).apply();
    }

    public static List<Table> getAddedTables(String userId) {
        init();
        List<Table> tableNames = new Gson().fromJson(sharedPreferences.getString(userId, ""), new TypeToken<List<Table>>() {
        }.getType());
        if (tableNames == null) {
            tableNames = new ArrayList<>();
        }
        return tableNames;
    }

    public static void removeTable(String userId, Table table) {
        init();
        List<Table> tables = getAddedTables(userId);
        tables.remove(table);
        storeTables(userId, tables);
    }
}
